package com.name.carregistry.controller.dtos;

import lombok.Data;

@Data
public class BrandRequest {

    private String name;
    private String description;

    public BrandRequest() {}

    public BrandRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
