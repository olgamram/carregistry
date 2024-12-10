package com.name.carregistry.repository;

import com.name.carregistry.controller.dtos.CarResponse;

import java.util.List;

import com.name.carregistry.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<CarResponse, Long> {

    void getCar();

}
