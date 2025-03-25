package com.sareepuram.ecommerce.admin;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Admin {
    @Id
    private String email;
    private String password;
}
