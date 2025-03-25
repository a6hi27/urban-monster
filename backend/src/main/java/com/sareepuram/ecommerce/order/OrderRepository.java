package com.sareepuram.ecommerce.order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    Optional<List<Order>> findByUser_UserId(Integer userId) throws IllegalArgumentException;
}
