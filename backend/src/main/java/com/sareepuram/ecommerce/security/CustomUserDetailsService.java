package com.sareepuram.ecommerce.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.*;

import com.sareepuram.ecommerce.user.UserRepository;
import com.sareepuram.ecommerce.user.User;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("The user given by frontend is " + email);
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if (user.get().isRoleUser()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        if (user.get().isRoleAdmin()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        return new CustomUserDetails(user.get().getUserId(), user.get().getEmail(), user.get().getPassword(),
                user.get().getName(), user.get().getPhone(), authorities);
    }

}