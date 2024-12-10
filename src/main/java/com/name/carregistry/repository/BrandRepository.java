package com.name.carregistry.repository;

import com.name.carregistry.domain.BrandEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<BrandEntity, Long> {

    String findByName(String name);
}
