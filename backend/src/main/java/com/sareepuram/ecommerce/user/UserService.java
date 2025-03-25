package com.sareepuram.ecommerce.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;

import com.sareepuram.ecommerce.security.CustomUserDetails;

import jakarta.servlet.http.HttpSession;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Get all users from User table
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }

    // Add a user to the User table
    public User addUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User updatePassword(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user = userOptional.get();
        user.setPassword(passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    // Update a user
    public User updateUser(User updatedUser) throws IllegalArgumentException {
        Optional<User> userOptional = userRepository.findById(updatedUser.getUserId());
        if (userOptional.isPresent()) {
            return userRepository.save(updatedUser);
        }
        return null;
    }

    // Delete a user
    public boolean deleteUser(int id) throws IllegalArgumentException {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Find a user by email
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByUserId(Integer userId) {
        return userRepository.findById(userId);
    }

    // Get the currently logged-in user
    public User getCurrentUser(HttpSession httpSession) {
        SecurityContext securityContext = (SecurityContext) httpSession.getAttribute("SPRING_SECURITY_CONTEXT");
        if (securityContext != null) {
            Authentication authentication = securityContext.getAuthentication();
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails customUserDetails) {
                int userId = customUserDetails.getId();
                String username = customUserDetails.getUsername();
                String name = customUserDetails.getName();
                String password = customUserDetails.getPassword();
                String phone = customUserDetails.getPhone();
                return new User(userId, username, name, password, phone);
            }
        }
        return new User(-1, "empty", "empty", "empty", "empty");
    }
}
