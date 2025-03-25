package com.sareepuram.ecommerce.user;

import lombok.Data;

@Data
public class UserDTO {
    private int userId;
    private String name;
    private String email;
    private String phone;

    // Constructors, Getters, and Setters
    public UserDTO(int userId, String name, String email, String phone) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
}
