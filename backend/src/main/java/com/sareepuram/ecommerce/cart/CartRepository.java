package com.sareepuram.ecommerce.cart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.Optional;
import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    List<Cart> findAllByUser_UserId(Integer userId);

    Optional<Cart> findByUser_UserIdAndProduct_ProductId(Integer userId, Integer productId) throws IllegalArgumentException;

    @Query("SELECT new com.sareepuram.ecommerce.cart.CartDTO(c.product, c.quantity) " +
            "FROM Cart c WHERE c.user.userId = :userId")
    Optional<List<CartDTO>> findProductsInCartByUserId(@Param("userId") Integer userId);


    Integer deleteByUser_UserIdAndProduct_ProductId(Integer userId, Integer productId) throws IllegalArgumentException;

    Integer deleteAllByUser_UserId(Integer userId);
}
