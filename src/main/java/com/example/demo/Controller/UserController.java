package com.example.demo.Controller;

import com.example.demo.AllSecurity.JwtUtils;
import com.example.demo.DTO.LoginUser.LoginDto;

import com.example.demo.DTO.RegistrationUser.RegisterDto;
import com.example.demo.Entity.AuthRequest;
import com.example.demo.Entity.User;
import com.example.demo.Repository.UserRepo.UserRepository;
import com.example.demo.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@RestController
@CrossOrigin(origins = "https://blood-donation-frontended-8.onrender.com")
public class UserController {

    @Autowired
    private UserService userServ;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String welcomeMessage()
    {
        return "Welcome to Blood Bank Management system !!!";
    }
    @PostMapping("/authenticate")
    public ResponseEntity<?> generateToken(@RequestBody AuthRequest authRequest) {
        try {
            System.out.println("Email: " + authRequest.getEmail());
            System.out.println("Password: " + authRequest.getPassword());

            // Fetch user by email using Optional
            Optional<User> optionalUser = userRepository.findByEmail(authRequest.getEmail());

            if (!optionalUser.isPresent()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "Invalid Email/Password"));
            }

            User user = optionalUser.get(); // Extract the user from Optional

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );

            String token = jwtUtils.generateToken(authRequest.getEmail());

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("role", user.getRole());
            response.put("name", user.getName());

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "Invalid Email/Password"));
        }
    }




    // Register User and Send OTP
    @PostMapping("/register")
    public ResponseEntity<String> registerStudent(@Valid @RequestBody RegisterDto dto) {
        try {
            // Call registerUser method which handles all the registration logic
            return userServ.registerUser(dto);
        } catch (Exception e) {
            // Log the error and return a bad request response with the error message
            logger.error("Registration error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed: " + e.getMessage());
        }
    }


    // Verify OTP and Register User
    @PostMapping("/verify")
    public ResponseEntity<String> verifyOtpAndRegister(@RequestBody Map<String, Object> request) {
        try {
            // Extract email
            String email = (String) request.get("email");
            if (email == null || email.isEmpty()) {
                return ResponseEntity.badRequest().body("Email is required.");
            }

            // Extract OTP as a String first to avoid casting issues
            Object emailOtpObject = request.get("emailOtp");
            if (emailOtpObject == null) {
                return ResponseEntity.badRequest().body("OTP is required.");
            }

            String emailOtpStr = emailOtpObject.toString();
            int emailOtp = Integer.parseInt(emailOtpStr); // Convert safely

            return userServ.verifyOtpAndRegister(email, emailOtp);
        } catch (NumberFormatException e) {
            logger.error("Invalid OTP format: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Invalid OTP format. Please enter numeric values.");
        } catch (Exception e) {
            logger.error("Verification error for email: {} - {}", request.get("email"), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during verification. Please try again.");
        }
    }
    @PostMapping("/resend-registration")
    public ResponseEntity<String> resendRegistrationOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String responseMessage = userServ.resendRegistrationOtp(email);
        return ResponseEntity.ok(responseMessage);
    }

    // Login User
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginDto userLogin) {
        return userServ.loginUser(userLogin);
    }


    // Forgot Password
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        try {
            userServ.forgotPassword(email);  // Generate OTP and send to email for password reset
            return ResponseEntity.ok("OTP sent to your email for password reset.");
        } catch (Exception e) {
            logger.error("Forgot password error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/resend-forgot-password")
    public ResponseEntity<String> resendForgotPasswordOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String responseMessage = String.valueOf(userServ.resendForgotPasswordOtp(email));
        return ResponseEntity.ok(responseMessage);
    }

    // Reset Password
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, Object> request) {
        String email = (String) request.get("email");
        Integer otp = (Integer) request.get("otp");
        String newPassword = (String) request.get("newPassword");

        if (email == null || otp == null || newPassword == null || newPassword.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid request parameters.");
        }

        return userServ.resetPassword(email, otp, newPassword);
    }



    //Update
    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long userId,
            @RequestBody User updatedUser) {

        try {
            User updated = userServ.updateUser(userId, updatedUser);
            return ResponseEntity.ok(updated);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating user");
        }
    }



    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        try {
            return userServ.logoutUser(email);
        } catch (Exception e) {
            logger.error("Logout error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
