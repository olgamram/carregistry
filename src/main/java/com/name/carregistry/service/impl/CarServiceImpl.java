package com.name.carregistry.service.impl;

import com.name.carregistry.controller.dtos.CarRequest;
import com.name.carregistry.controller.dtos.CarResponse;
import com.name.carregistry.domain.Car;
import com.name.carregistry.domain.UserEntity;
import com.name.carregistry.repository.BrandRepository;
import com.name.carregistry.repository.CarRepository;
import com.name.carregistry.service.CarService;
import com.name.carregistry.service.converters.CarConverter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.antlr.v4.runtime.tree.xpath.XPath.findAll;

@Service
@Slf4j
public class CarServiceImpl implements CarService {

    // Inyección del repositorio para interactuar con la base de datos (si fuera necesario en el futuro)
    @Autowired
    private CarRepository carRepository;
    private CarConverter carConverter;
    private BrandRepository brandRepository;

    // Lista de respuesta de los coches, inicializada con datos simulados mediante el metodo poblateCarList()
    List<CarResponse> carResponseList = poblateCarList();

    // Obtener un coche por su ID. Si el coche se encuentra, se retorna, si no lanza una excepción.
    @Override
    public CarResponse getCarById(Integer id) throws Exception {

        // Se loguea la marca y el modelo de cada coche en la lista
        carResponseList.forEach(car -> {
            log.info("Coche marca {} y modelo {}", car.getBrand(), car.getModel());
        });

        // Filtra la lista para buscar el coche con el ID correspondiente y lo retorna si lo encuentra.
        // Se usa get() directamente, lo cual puede lanzar una excepción si no se encuentra el coche.
        return carResponseList.stream()
                .filter(car -> car.getId().equals(id))
                .findFirst()
                .get();  // Esto podría causar un error si el ID no existe
    }

    // Eliminar un coche por su ID. Si el coche se encuentra, se elimina de la lista.
    @Override
    public void deleteById(Integer id) throws Exception {
        // Filtrar la lista para buscar el coche con el ID y luego eliminarlo si se encuentra.
        carResponseList.remove(
                carResponseList.stream()
                        .filter(car -> car.getId().equals(id))
                        .findFirst()
                        .get()  // Esto también puede causar un error si el ID no existe
        );

        // Después de eliminar el coche, se loguea la marca y el modelo de cada coche que permanece en la lista
        carResponseList.forEach(car -> {
            log.info("Coche marca {} y modelo {}", car.getBrand(), car.getModel());
        });
    }

    // Actualizar un coche por su ID. Si el coche existe, se reemplaza con la nueva información.
    @Override
    public CarResponse updateById(Integer id, CarRequest carRequest) throws Exception {
        // Buscar el coche por ID
        Optional<CarResponse> carResponse = carResponseList.stream()
                .filter(car -> car.getId().equals(id))
                .findFirst();

        // Mapea los datos del CarRequest al objeto CarResponse
        CarResponse requestMapped = this.mapper(carRequest);

        // Si el coche existe, se actualiza la información y se reemplaza en la lista
        if (carResponse.isPresent()) {
            requestMapped.setId(id); // Se conserva el mismo ID
            carResponseList.remove(carResponse.get());  // Elimina el coche antiguo
            carResponseList.add(requestMapped);  // Añade el coche actualizado

            // Se loguea la marca y el modelo de cada coche en la lista después de la actualización
            carResponseList.forEach(car -> {
                log.info("Coche marca {} y modelo {}", car.getBrand(), car.getModel());
            });
        } else {
            throw new Exception(); // Si no se encuentra el coche, lanza una excepción
        }

        return requestMapped; // Retorna el coche actualizado
    }

    // Guardar un nuevo coche en la lista.
    @Override
    public CarResponse saveCar(CarRequest carRequest) {
        // Mapea los datos del CarRequest al objeto CarResponse
        CarResponse carResponse = this.mapper(carRequest);

        // Añade el nuevo coche a la lista
        carResponseList.add(carResponse);

        // Se loguea la marca y el modelo de cada coche en la lista
        carResponseList.forEach(car -> {
            log.info("Coche marca {} y modelo {}", car.getBrand(), car.getModel());
        });

        return carResponse; // Retorna el coche recién guardado
    }

    @Override
    @Async
    public CompletableFuture<List<CarResponse>> getAllCars() {
        List<CarResponse> carResponseList = carRepository.findAll();

        List<CarResponse> cars = new ArrayList<>();
        carResponseList.forEach(car -> {
            cars.add(carConverter.toCar(car));
        });
        return CompletableFuture.completedFuture(cars);
    }

    @Override
    public String carsCsv(){
        List<CarResponse> carResponseList = carRepository.findAll();
        StringBuilder csvContent = new StringBuilder();

        for ( CarResponse car : carResponseList){
            csvContent.append(car.getBrand()).append(".")
                    .append(car.getModel()).append(".")
                    .append(car.getColour()).append(".")
                    .append(car.getDescription()).append(".")
                    .append(car.getYear()).append(".")
                    .append(car.getPrice()).append(".")
                    .append("\n");
        }
        return csvContent.toString();
    }

    @Override
    public void uploadCars(MultipartFile file){
        List<CarResponse> carResponseList = new ArrayList<>();
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())){

            Iterable<CSVRecord> csvRecord = csvParser.getRecords();

            for( CSVRecord record : csvRecord){
                CarResponse car = new CarResponse();

                car.setBrand(brandRepository.findByName(record.get("brand")));

                car.setDescription(record.get("description"));
                car.setYear(Integer.parseInt(record.get("year")));
                car.setColour(record.get("colour"));
                car.setMilleage(Integer.parseInt(record.get("milleage")));

                carResponseList.add(car);
            }
            carRepository.saveAll(carResponseList);
        }
        catch (Exception e){
            log.error("Failed to load users");
            throw new RuntimeException("Failed to load users");
        }

    }

    // Metodo para poblar la lista inicial de coches con algunos datos simulados
    private List<CarResponse> poblateCarList() {
        List<CarResponse> carList = new ArrayList<>();

        // Crear el primer coche con datos simulados y agregarlo a la lista
        CarResponse car = new CarResponse();
        car.setId(1);
        car.setBrand("BMW");
        car.setModel("Serie 1");
        car.setMilleage(10000);
        car.setPrice(35000.99);

        carList.add(car);  // Añadir el primer coche a la lista

        // Crear el segundo coche
        CarResponse car2 = new CarResponse();
        car2.setId(2);  // Aquí debería ser 'car2.setId(2);' (esto está corregido en el código ajustado)
        car2.setBrand("Audi");  // Aquí también debería ser 'car2.setBrand("Audi");'
        car2.setModel("A4");
        car2.setMilleage(9000);
        car2.setPrice(3000.99);

        carList.add(car2);  // Añadir el segundo coche a la lista

        return carList;  // Retornar la lista de coches simulados
    }

    // Metodo auxiliar para mapear los datos de un CarRequest a un CarResponse
    private CarResponse mapper(CarRequest request) {
        CarResponse car = new CarResponse();
        car.setId(request.getId());  // Asignar el ID desde el request
        car.setBrand(request.getBrand());  // Asignar la marca
        car.setModel(request.getModel());  // Asignar el modelo
        car.setMilleage(request.getMilleage());  // Asignar el kilometraje
        car.setPrice(request.getPrice());  // Asignar el precio

        return car;  // Retornar el coche mapeado
    }
}
