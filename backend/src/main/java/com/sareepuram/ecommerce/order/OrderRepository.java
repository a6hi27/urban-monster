package com.sareepuram.ecommerce.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    Optional<List<Order>> findByUser_UserId(Integer userId) throws IllegalArgumentException;

    @Query("SELECT o.orderCreationStatus FROM Order o WHERE o.orderId = :orderId")
    Optional<String> findOrderCreationStatusByOrderId(@Param("orderId") Integer orderId);
}
