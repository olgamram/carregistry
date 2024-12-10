package com.name.carregistry.controller;

import com.name.carregistry.controller.dtos.CarRequest;
import com.name.carregistry.controller.dtos.CarResponse;
import com.name.carregistry.controller.mapper.CarMapper;
import com.name.carregistry.service.CarService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController  //esta clase es un controlador de Spring que maneja solicitudes HTTP
@Slf4j  //logger (lombok), que simplifica el registro de mensajes

public class CarController {

    // Inyección de dependencias: se inyecta el servicio DemoService para interactuar con la lógica de negocio
    @Autowired
    private CarService carService;
    private CarMapper carMapper;

    // Endpoint GET para obtener un coche por su ID. Se espera un ID como parámetro en la URL.
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENT','VENDOR')")
    public ResponseEntity<?> getCar(@PathVariable Integer id) {
        log.info("Se ha accedido a la aplicación para obtener el coche con ID: {}:");  // Mensaje de log para indicar que se ha accedido al endpoint
        try {
            // Si se encuentra el coche, se devuelve con el código HTTP 200 (OK)
            return ResponseEntity.ok(carService.getCarById(id));
        } catch (Exception e) {
            // Si ocurre una excepción (por ejemplo, si no se encuentra el coche), se devuelve un 404 (Not Found)
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/cars")
    public CompletableFuture<?> getCars() {
        try {
            CompletableFuture<List<CarResponse>> cars = carService.getAllCars();
            List<CarResponse> response = new ArrayList<>();
            cars.get().forEach(car -> {
                response.add(carMapper.toResponse(car));
            });
            return CompletableFuture.completedFuture(response);
        }
        catch (Exception e) {
            return  CompletableFuture.completedFuture(ResponseEntity.notFound());
        }
    }


    // Endpoint DELETE para eliminar un coche por su ID. Se espera un ID como parámetro en la URL.
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCarById(@PathVariable Integer id) {
        log.info("Información borrada");  // Mensaje de log para indicar que se está intentando borrar un coche
        try {
            // Si la eliminación es exitosa, se devuelve un 200 (OK) sin cuerpo
            carService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            // Si el coche no existe o se produce una excepción, se devuelve un 404 (Not Found)
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoint POST para añadir un coche nuevo. El objeto CarRequest se envía en el cuerpo de la solicitud.
    @PostMapping("/add")
    public ResponseEntity<?> addCar(@Valid @RequestBody CarRequest carRequest) {
        log.info("Intentando añadir coche con datos: {}", carRequest);  // Log para los datos del coche a añadir
        try {
            // Si la creación es exitosa, se devuelve un 200 (OK)
            carService.saveCar(carRequest);
            log.info("Coche añadido con éxito"); // Log después de añadir con éxito
            return ResponseEntity.status(HttpStatus.CREATED).body("Car created successfully");  // Devuelve 201 Created
        } catch (Exception e) {
            log.error("Error al añadir coche: {}", carRequest, e);  // Log si ocurre algún error
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error creating car");  // Devuelve 400 si falla
        }
    }

    // Endpoint PUT para actualizar un coche existente por su ID. Se recibe el ID en la URL y el nuevo objeto en el cuerpo.
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<?> updateCar(@PathVariable Integer id, @RequestBody CarRequest carRequest) {
        log.info("Intentando actualizar coche con ID: {} y datos: {}", id, carRequest);  // Log del ID y los datos a actualizar
        try {
            carService.updateById(id, carRequest);
            log.info("Coche con ID: {} actualizado con éxito", id);  // Log después de actualizar con éxito
            return ResponseEntity.ok().body("Car updated successfully");  // Devuelve 200 OK si se actualiza correctamente
        } catch (Exception e) {
            log.error("Error al actualizar coche con ID: {}", id, e);  // Log si ocurre algún error
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Car not found");  // Devuelve 404 si no se encuentra
        }
    }


    @GetMapping(value="/downloadCars")
    @PreAuthorize("hasAnyRole('CLIENT','VENDOR')")
    public ResponseEntity<?> downloadCars() throws IOException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "users.csv");

        byte[] csvBytes = carService.carsCsv().getBytes();

        return new ResponseEntity<>(csvBytes, headers, HttpStatus.OK);
    }

    //Endpoint para guardar coches en la BD
    @PostMapping(value = "/uploadCsv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<String> uploadCsv(@RequestParam(value = "file") MultipartFile file) {
        if (file.isEmpty()) {
            log.error("The file it's empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (file.getOriginalFilename().contains(".csv")) {
            carService.uploadCars(file);
            return ResponseEntity.ok("File successfully uploaded.");
        }

        log.error("The file it's not a CSV");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The file it's not a CSV");
    }

}
