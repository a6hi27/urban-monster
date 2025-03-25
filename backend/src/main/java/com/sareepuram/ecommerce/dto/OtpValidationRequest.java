package com.sareepuram.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OtpValidationRequest {
    private String otp;
    private String otpToken;
}
