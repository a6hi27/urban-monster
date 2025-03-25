package com.sareepuram.ecommerce.user;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import com.sareepuram.ecommerce.dto.OtpToken;
import com.sareepuram.ecommerce.dto.OtpValidationRequest;
import com.sareepuram.ecommerce.dto.UpdatePasswordRequest;
import com.sareepuram.ecommerce.util.EmailService;
import com.sareepuram.ecommerce.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;


    //Getting all registered users' details
    @GetMapping("user")
    public ResponseEntity<List<UserDTO>> findAll() {
        List<User> users = userService.findAll();
        List<UserDTO> usersDTO = users.stream().map(user -> new UserDTO(user.getUserId(), user.getName(),
                user.getEmail(), user.getPhone())).collect(Collectors.toList());
        return new ResponseEntity<>(usersDTO, HttpStatus.OK);
    }

    //Getting user details by their id
    @GetMapping("/user/{id}")
    public ResponseEntity<UserDTO> findUserById(@PathVariable int id) {
        try {
            Optional<User> userOptional = userService.findByUserId(id);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                UserDTO userDTO = new UserDTO(user.getUserId(), user.getName(), user.getEmail(), user.getPhone());
                return new ResponseEntity<>(userDTO, HttpStatus.OK);
            } else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("user/changepassword")
    public ResponseEntity<?> changePassword(@RequestBody String userEmail) {
        try {
            Random randInt = new Random();
            int passwordChangeOtp = randInt.nextInt(900000) + 100000;
            String passwordChangeOtpToken = JwtUtil.generateOtpToken(String.valueOf(passwordChangeOtp), userEmail);
            emailService.sendEmail(userEmail, "verification@shopzone.com", "Your OTP for password change", "Your OTP" +
                    " for " +
                    "changing password (valid for 8 minutes) is " + passwordChangeOtp);
            return new ResponseEntity<>(new OtpToken(passwordChangeOtpToken), HttpStatus.OK);
        } catch (MessagingException e) {
            return new ResponseEntity<>(e.getCause(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("user/updatepassword")
    public ResponseEntity<?> updatePassword(@RequestHeader("Authorization") String token,
                                            @RequestBody UpdatePasswordRequest request) {
        try {
            String jwt = token.substring(7);

            Claims claims = JwtUtil.getClaims(jwt);
            boolean validated = claims.get("validated", Boolean.class);

            if (validated) {
                // Allow password reset
                String email = claims.getSubject().replace("\"", "");
                String newPassword = request.getNewPassword();
                userService.updatePassword(email, newPassword);
                return ResponseEntity.ok("Password changed successfully for " + email);
            }
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("OTP token expired, password change denied");
        }
        return new ResponseEntity<>("OTP is incorrect", HttpStatus.UNAUTHORIZED);
    }

    //Creation of user after email verification
    @PostMapping("user")
    public ResponseEntity<?> addUser(@RequestHeader("Authorization") String token,
                                     @RequestBody User user) {
        try {
            String jwt = token.substring(7);

            Claims claims = JwtUtil.getClaims(jwt);
            boolean validated = claims.get("validated", Boolean.class);

            if (validated) {
                User addedUser = userService.addUser(user);
                UserDTO userDTO = new UserDTO(addedUser.getUserId(), addedUser.getName(), addedUser.getEmail(), addedUser.getPhone());
                return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
            }
            return new ResponseEntity<>("OTP invalid or expired", HttpStatus.OK);
        } catch (SignatureException e) {
            e.printStackTrace();
            return new ResponseEntity<>("JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.", HttpStatus.BAD_REQUEST);
        }

    }

    //Verfication of User email
    @PostMapping("user/register")
    public ResponseEntity<?> registerUser(@RequestBody String email) {
        try {
            Random randInt = new Random();
            int registrationOtp = randInt.nextInt(900000) + 100000;
            String registerOtpToken = JwtUtil.generateOtpToken(String.valueOf(registrationOtp), email);
            emailService.sendEmail(email, "verification@shopzone.com", "Your OTP for registration", "Your " +
                    "OTP for registering (valid for 8 minutes) is " +
                    registrationOtp);
            return new ResponseEntity<>(new OtpToken(registerOtpToken), HttpStatus.OK);
        } catch (MessagingException e) {
            e.toString();
            return new ResponseEntity<>(e.getCause(), HttpStatus.BAD_REQUEST);
        }
    }


    //OTP Validation
    @PostMapping("user/otp-validation")
    public ResponseEntity<?> doOtpValidation(@RequestBody OtpValidationRequest otpValidationRequest) {
        try {
            String otpToken = otpValidationRequest.getOtpToken();
            String otp = otpValidationRequest.getOtp();
            if (JwtUtil.validateOtpToken(otpToken, otp)) {
                String email = JwtUtil.getEmailFromToken(otpToken);
                String resetToken = JwtUtil.generateResetToken(email, true);
                return new ResponseEntity<>(new OtpToken(resetToken), HttpStatus.OK);
            }
            return new ResponseEntity<>("OTP is incorrect or expired", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>("Please provide an integer OTP", HttpStatus.BAD_REQUEST);
        }
    }

    //Updating a user
    @PutMapping("user")
    public ResponseEntity<UserDTO> updateUser(@RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(user);
            if (updatedUser != null) {
                UserDTO userDTO = new UserDTO(updatedUser.getUserId(), updatedUser.getName(), updatedUser.getEmail(),
                        updatedUser.getPhone());
                return new ResponseEntity<>(userDTO, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //Deleting a user
    @DeleteMapping("user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        try {
            if (userService.deleteUser(id))
                return new ResponseEntity<>(HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}