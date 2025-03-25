package com.sareepuram.ecommerce.user;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sareepuram.ecommerce.cart.Cart;
import com.sareepuram.ecommerce.order.Order;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class User implements Serializable {

    public User(int userId, String email, String name, String password, String phone) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.password = password;
        this.phone = phone;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;
    private String name;
    private String email;
    private String password;
    private String phone;
    private boolean roleUser;
    private boolean roleAdmin;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonManagedReference(value = "cart_user")
    private List<Cart> products = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    @JsonManagedReference(value = "order_user")
    private List<Order> orders = new ArrayList<>();
}
