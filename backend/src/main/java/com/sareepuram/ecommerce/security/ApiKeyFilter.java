//package com.sareepuram.ecommerce.security;
//
//import jakarta.servlet.http.Cookie;
//
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//
//import lombok.Getter;
//import lombok.Setter;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.http.ResponseEntity;
//
//@Component
//public class ApiKeyFilter extends OncePerRequestFilter {
//
//    private static final String VALIDATION_ENDPOINT = "http://localhost:9000/api/validate-api-key";
//
//    private boolean validateApiKeyWithServer(String appId, String appSecret) {
//        RestTemplate restTemplate = new RestTemplate();
//        try {
//            // Send the plain-text API key to the server for validation
//            ResponseEntity<ApiKeyValidationResponse> response = restTemplate.postForEntity(
//                    VALIDATION_ENDPOINT,
//                    new ApiKeyValidationRequest(appId, appSecret),
//                    ApiKeyValidationResponse.class
//            );
//            return response.getBody() != null && response.getBody().isValid();
//        } catch (Exception e) {
//            // Handle error (e.g., server down)
//            return false;
//        }
//    }
//
//    @Override
//    protected void doFilterInternal(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, jakarta.servlet.FilterChain filterChain) throws jakarta.servlet.ServletException, IOException {
//        String requestURI = request.getRequestURI();
//        String appId = null;
//        String appSecret = null;
//        // Skip API key validation for specific endpoints
//        if (requestURI.contains("user/checkout/success") || requestURI.contains("/invoice" +
//                "/generate")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        Cookie[] cookies = request.getCookies();
//
//        for (Cookie cookie : cookies) {
//            System.out.println(cookie.getName());
//            if (cookie.getName().equals("app-id"))
//                appId = cookie.getValue();
//            if (cookie.getName().equals("app-secret"))
//                appSecret = cookie.getValue();
//        }
//
//        System.out.println("The appid in filter" + appId);
//        System.out.println("The appsecret in filter" + appSecret);
//        if (appSecret == null || appSecret.isEmpty()) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getWriter().write("API key missing");
//            return;
//        }
//
//        boolean isValid = validateApiKeyWithServer(appId, appSecret);
//        if (!isValid) {
//            System.out.println("The api key validation failed maybe bcoz app-secret or app-id is null or incorrect or" +
//                    " the validation server is off");
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getWriter().write("Invalid API key or Server error");
//            return;
//        }
//
//        // Proceed with the filter chain if API key is valid
//        filterChain.doFilter(request, response);
//
//    }
//
//    // Request object for sending to the server
//    @Setter
//    @Getter
//    static class ApiKeyValidationRequest {
//        private String appId;
//        private String appSecret;
//
//        public ApiKeyValidationRequest(String appId, String appSecret) {
//            this.appId = appId;
//            this.appSecret = appSecret;
//        }
//
//    }
//
//    // Response object returned from the server
//    @Setter
//    @Getter
//    static class ApiKeyValidationResponse {
//        private boolean valid;
//        private String appId;
//
//    }
//}
