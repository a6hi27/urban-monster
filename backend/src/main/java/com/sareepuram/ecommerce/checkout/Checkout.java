package com.sareepuram.ecommerce.checkout;

import lombok.Data;


@Data
public class Checkout {
    private final String CURRENCY = "USD";
    private final String METHOD = "PAYPAL";
    private final String DESCRIPTION = "The description of transaction";
    private final String CANCEL_URL = "http://localhost:8080/user/cart";
    private final String SUCCESS_URL = "http://localhost:8080/user/checkout/success";
    private final String INTENT = "SALE";
}
