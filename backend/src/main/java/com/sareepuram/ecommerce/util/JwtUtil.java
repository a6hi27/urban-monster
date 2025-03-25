package com.sareepuram.ecommerce.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtil {
    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Generate a secure key
    private static final long OTP_VALIDITY_MS = 8 * 60 * 1000; // OTP valid for 8 minutes

    // Generate a JWT with OTP
    public static String generateOtpToken(String otp, String email) {
        return Jwts.builder()
                .setSubject(email)
                .claim("otp", otp)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + OTP_VALIDITY_MS))
                .signWith(key)
                .compact();
    }

    // Validate OTP from JWT
    public static boolean validateOtpToken(String token, String otp) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
            String tokenOtp = claims.get("otp", String.class);
            return tokenOtp.equals(otp);
        } catch (Exception e) {
            return false; // Token is invalid or expired
        }
    }

    // Get email from JWT
    public static String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public static String generateResetToken(String email, boolean validated) {
        return Jwts.builder()
                .setSubject(email)
                .claim("validated", validated) // Include validated status in token
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 8 * 60 * 1000)) // 8 mins valid
                .signWith(key)
                .compact();
    }

    public static Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}
