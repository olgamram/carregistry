package com.name.carregistry.controller.mapper;

import com.name.carregistry.controller.dtos.CarResponse;

public class CarMapper {

    public CarResponse toResponse(CarResponse request) {
        CarResponse car = new CarResponse();
        car.setId(request.getId());
        car.setBrand(request.getBrand());
        car.setModel(request.getModel());
        car.setMilleage(request.getMilleage());
        car.setPrice(request.getPrice());

        return car;
    }
}
