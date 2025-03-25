package com.sareepuram.ecommerce.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;

@Configuration
public class PaypalConfig {

    private static final String clientId = System.getenv("PAYPAL_CLIENT_ID");
    private static final String clientSecret = System.getenv("PAYPAL_CLIENT_SECRET");
    @Value("${paypal.mode}")
    private String mode;

    @Bean
    public Map<String, String> setPaypalSdkConfig() {
        Map<String, String> configMap = new HashMap<>();
        configMap.put("mode", mode);
        return configMap;
    }

    @Bean
    public OAuthTokenCredential getOAuthTokenCredential() {
        return new OAuthTokenCredential(clientId, clientSecret, setPaypalSdkConfig());
    }

    @Bean
    public APIContext getApiContext() throws PayPalRESTException {
        APIContext context = new APIContext(getOAuthTokenCredential().getAccessToken());
        context.setConfigurationMap(setPaypalSdkConfig());
        return context;
    }

}