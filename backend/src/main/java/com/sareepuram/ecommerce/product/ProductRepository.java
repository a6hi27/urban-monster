package com.sareepuram.ecommerce.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    Optional<Product> findByName(String searchString) throws IllegalArgumentException;

    Integer deleteByProductId(Integer productId) throws IllegalArgumentException;
}
