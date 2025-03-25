package com.sareepuram.ecommerce.config;

import com.sareepuram.ecommerce.security.CustomUserDetailsService;
import jakarta.servlet.http.Cookie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.*;

@Configuration
//@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //
    @Bean
    public AuthenticationSuccessHandler customSuccessHandler() {
        return (request, response, authentication) -> {
            String selectedRole = request.getParameter("role");
            String responseBody = "Successfully authenticated with role user!";
            response.addCookie(new Cookie("app-id", request.getHeader("app-id")));
            response.addCookie(new Cookie("app-secret", request.getHeader("app-secret")));
            // Redirect based on the selected role
            if ("ADMIN".equals(selectedRole) && authentication.getAuthorities().stream()
                    .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"))) {
                responseBody = "Successfully authenticated with role admin!";
            }
            ByteArrayInputStream bais = new ByteArrayInputStream(responseBody.getBytes());
            BufferedOutputStream bout = new BufferedOutputStream(response.getOutputStream());
            BufferedInputStream bin = new BufferedInputStream(bais);
            int ch;
            while ((ch = bin.read()) != -1)
                bout.write(ch);
            bais.close();
            bout.close();
            bin.close();
            response.setStatus(200);
        };
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults()).csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll())
                // Handle form login for both users and admins
                .formLogin(form -> form
                        .loginPage("/login")  // Unified login page for both
                        .loginProcessingUrl("/login") // Unified URL for login processing
                        .successHandler(customSuccessHandler())  // Custom handler to redirect based on role
                        .permitAll())

                // Configure logout behavior
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .invalidateHttpSession(true)
                        .deleteCookies("SESSION"));

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;

    }
}
