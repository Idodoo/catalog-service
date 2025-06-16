package com.idev.catalogservice.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {


    Optional<ProductEntity> findByCode(String code);
}
