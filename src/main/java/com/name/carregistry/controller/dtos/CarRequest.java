package com.name.carregistry.controller.dtos;

import lombok.Data;

@Data
public class CarRequest {

    private Integer Id;
    private String brand;
    private String model;
    private Integer milleage;
    private Double price;
    private Integer year;
    private String Description;
    private String colour;
    private String fuelType;
    private Integer numDoors;

}
