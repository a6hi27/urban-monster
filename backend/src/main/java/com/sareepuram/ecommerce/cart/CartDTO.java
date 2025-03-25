package com.sareepuram.ecommerce.cart;

import com.sareepuram.ecommerce.product.Product;


import com.sareepuram.ecommerce.user.User;
import lombok.Data;

import java.io.Serializable;

@Data
public class CartDTO implements Serializable {
    private Product product;
    private int quantity;

    // Constructors, Getters, and Setters
    public CartDTO(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

}
