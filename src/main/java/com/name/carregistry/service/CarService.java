package com.name.carregistry.service;

import com.name.carregistry.controller.dtos.CarRequest;
import com.name.carregistry.controller.dtos.CarResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface CarService {


    CarResponse getCarById(Integer id) throws Exception;

    void deleteById(Integer id) throws Exception;

    CarResponse updateById(Integer id, CarRequest carRequest) throws Exception;

    CarResponse saveCar(CarRequest carRequest);

    CompletableFuture<List<CarResponse>> getAllCars();

    String carsCsv();

    void uploadCars(MultipartFile file);


}
