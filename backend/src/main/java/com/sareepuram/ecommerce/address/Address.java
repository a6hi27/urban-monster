package com.sareepuram.ecommerce.address;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
public class Address implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long addressId;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String zipCode;
    private String countryCode;

    public Address(String addressLine1, String addressLine2, String city, String countryCode, String state,
                   String zipCode) {
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.countryCode = countryCode;
        this.state = state;
        this.zipCode = zipCode;
    }

    @Override
    public String toString() {
        return addressLine1 + ",\n" + addressLine2 + ",\n" + city + " - " + zipCode + "\n" + state + ",\n" + countryCode;
    }
}
