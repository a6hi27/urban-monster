package com.sareepuram.ecommerce.checkout;


import lombok.Getter;
@Getter
public final class CheckoutConstants {

    public static final String CURRENCY = "USD";
    public static final String METHOD = "PAYPAL";
    public static final String DESCRIPTION = "The description of transaction";
    public static final String CANCEL_URL = "http://localhost:8080/user/checkout/cancel";
    public static final String SUCCESS_URL = "http://localhost:8080/user/checkout/success";
    public static final String INTENT = "SALE";

    private CheckoutConstants() {
    }
}
