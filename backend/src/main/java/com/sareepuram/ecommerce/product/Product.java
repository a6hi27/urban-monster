package com.sareepuram.ecommerce.product;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sareepuram.ecommerce.cart.Cart;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Product implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int productId;
    private int availableQuantity;
    private Character gender;
    private String imageUrl;
    private String name;
    private Long price;
    private String size;
    private String productDescription;

    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonManagedReference(value = "cart_product")
    private List<Cart> users = new ArrayList<>();

}
