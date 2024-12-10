package com.name.carregistry.service.converters;

import org.springframework.stereotype.Component;
import com.name.carregistry.controller.dtos.CarResponse;

@Component
public class CarConverter {

    public CarResponse toCar(CarResponse car) {
        CarResponse carResponse = new CarResponse();

        carResponse.setBrand(car.getBrand());
        carResponse.setModel(car.getModel());
        carResponse.setYear(car.getYear());


        return carResponse;
    }
}

