package com.sareepuram.ecommerce.order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sareepuram.ecommerce.address.Address;
import com.sareepuram.ecommerce.item.Item;
import com.sareepuram.ecommerce.user.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "`order`")
@Data
@NoArgsConstructor
public class Order implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderId;

    private String paypalPaymentId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference(value = "order_user")
    private User user;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address shippingAddress;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private List<Item> items = new ArrayList<>();

    public Order(User user, List<Item> items, String paypalPaymentId, Address shippingAddress) {
        this.user = user;
        this.items = items;
        this.paypalPaymentId = paypalPaymentId;
        this.shippingAddress = shippingAddress;
    }
}
