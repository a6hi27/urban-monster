package com.sareepuram.ecommerce.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
}