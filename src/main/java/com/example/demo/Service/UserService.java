package com.example.demo.Service;


import com.example.demo.AllSecurity.JwtUtils;
import com.example.demo.DTO.EmailStorage.TempStorage;
import com.example.demo.DTO.LoginUser.LoginDto;
import com.example.demo.DTO.RegistrationUser.RegisterDto;
import com.example.demo.EmailOtp.EmailService;
import com.example.demo.Entity.User;
import com.example.demo.Enum.Role;
import com.example.demo.Repository.UserRepo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.util.*;

@Service
public class UserService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtils jwtUtils;


    @Autowired
    public UserService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    private EmailService emailService;

    private int generateOTP() {
        SecureRandom secureRandom = new SecureRandom();
        return secureRandom.nextInt(900000) + 100000;
    }

    // User Registration
    public ResponseEntity<String> registerUser(RegisterDto dto) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("Passwords do not match");
        }

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            logger.info("Email already registered: {}", dto.getEmail());
            return ResponseEntity.badRequest().body("Email is already registered. Please use a different email.");
        }

        if (userRepository.findByMobile(dto.getMobile()).isPresent()) {
            return ResponseEntity.badRequest().body("Phone number is already registered");
        }

        try {
            int emailOtp = generateOTP();
            TempStorage.storeEmailOtp(dto.getEmail(), emailOtp);
            TempStorage.storeRegistrationDataByEmail(dto.getEmail(), dto);

            emailService.sendEmailOtp(dto.getEmail(), emailOtp);

            return ResponseEntity.ok("OTP sent to your email for verification.");
        } catch (Exception e) {
            logger.error("Error generating OTP or sending email: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while sending OTP. Please try again.");
        }
    }

    // Verify OTP and Register User
    public ResponseEntity<String> verifyOtpAndRegister(String email, int emailOtp) {
        Integer storedEmailOtp = TempStorage.getEmailOtp(email);

        if (storedEmailOtp == null || storedEmailOtp != emailOtp) {
            return ResponseEntity.badRequest().body("Invalid or expired OTP.");
        }

        RegisterDto dto = TempStorage.getRegistrationDataByEmail(email);
        if (dto == null) {
            return ResponseEntity.badRequest().body("No registration data found for this email.");
        }

        User user = new User(dto.getName(), dto.getEmail(), passwordEncoder.encode(dto.getPassword()),
                dto.getAddress(), dto.getMobile(), dto.getAge(), dto.getGender(), dto.getBloodgroup());

        user.setRole(Role.USER); // Ensure default role is USER
        userRepository.save(user);

        // Clear OTP and registration data
        TempStorage.clearEmailOtp(email);
        TempStorage.clearRegistrationDataByEmail(email);
        emailService.sendRegistrationConfirmation(email, dto.getName());

        return ResponseEntity.ok("User registered successfully.");
    }

    public String resendRegistrationOtp(String email) {
        if (!TempStorage.hasEmail(email)) {
            // Generate and send a new OTP if the email is not in TempStorage
            int newOtp = generateOTP();
            TempStorage.storeEmailOtp(email, newOtp);
            emailService.sendEmailOtp(email, newOtp);
            return "New OTP sent to your email for verification.";
        }

        // Retrieve existing OTP (allowing null handling)
        Integer existingOtp = TempStorage.getEmailOtp(email);

        if (existingOtp != null) {
            emailService.sendEmailOtp(email, existingOtp);
            return "Existing OTP resent to your email.";
        }

        return "No OTP found or expired. Please register again.";
    }





    // Login User
    public ResponseEntity<Map<String, Object>> loginUser(LoginDto loginDto) {
        Optional<User> userOpt = userRepository.findByEmail(loginDto.getEmail());

        if (!userOpt.isPresent() || !passwordEncoder.matches(loginDto.getPassword(), userOpt.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("message", "Invalid credentials"));
        }

        User user = userOpt.get();
        String token = jwtUtils.generateToken(user.getEmail());

        // Preparing response data
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login successful");
        response.put("userId", user.getId());
        response.put("email", user.getEmail());
        response.put("name", user.getName());
        response.put("role", user.getRole());
        response.put("token", token);

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<String> forgotPassword(String email) {
        if (!StringUtils.hasText(email)) {
            return ResponseEntity.badRequest().body("Email cannot be empty.");
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent()) {
            return ResponseEntity.badRequest().body("Email not registered.");
        }

        int otp = generateOTP();
        TempStorage.storeEmailOtp(email, otp);
        emailService.sendPasswordResetEmail(email, otp);

        return ResponseEntity.ok("OTP sent to " + email + " for password reset. Please check your inbox.");
    }

    public ResponseEntity<String> resendForgotPasswordOtp(String email) {
        if (!StringUtils.hasText(email)) {
            return ResponseEntity.badRequest().body("Email cannot be empty.");
        }

        if (!userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body("Email is not registered.");
        }

        // Always generate a new OTP, even if the old one expired
        int newOtp = generateOTP();
        TempStorage.storeEmailOtp(email, newOtp);
        emailService.sendPasswordResetEmail(email, newOtp);

        return ResponseEntity.ok("A new OTP has been sent to your email for password reset.");
    }


    // ✅ Reset Password
    public ResponseEntity<String> resetPassword(String email, int otp, String newPassword) {
        if (!StringUtils.hasText(email)) {
            return ResponseEntity.badRequest().body("Email cannot be empty.");
        }

        Integer storedOtp = TempStorage.getEmailOtp(email);
        if (storedOtp == null || storedOtp != otp) {
            return ResponseEntity.badRequest().body("Invalid or expired OTP.");
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent()) {
            return ResponseEntity.badRequest().body("User not found.");
        }

        if (!StringUtils.hasText(newPassword)) {
            return ResponseEntity.badRequest().body("Password cannot be empty.");
        }

        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        TempStorage.clearEmailOtp(email);
        return ResponseEntity.ok("Your password has been reset successfully. You can now log in with your new password.");
    }


    // Update User Details
    public User updateUser(Long userId, User updatedUser) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        updateUserDetails(existingUser, updatedUser);
        return userRepository.save(existingUser);
    }

    private void updateUserDetails(User existingUser, User updatedUser) {
        if (StringUtils.hasText(updatedUser.getName())) {
            existingUser.setName(updatedUser.getName());
        }
        if (StringUtils.hasText(updatedUser.getMobile())) {
            existingUser.setMobile(updatedUser.getMobile());
        }
        if (StringUtils.hasText(updatedUser.getAddress())) {
            existingUser.setAddress(updatedUser.getAddress());
        }
        if (updatedUser.getAge() != null) {
            existingUser.setAge(updatedUser.getAge());
        }
        if (updatedUser.getGender() != null) {
            existingUser.setGender(updatedUser.getGender());
        }
        if (StringUtils.hasText(updatedUser.getPassword())) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        if (StringUtils.hasText(updatedUser.getBloodgroup())) {
            existingUser.setBloodgroup(updatedUser.getBloodgroup());
        }

        existingUser.setActive(updatedUser.isActive());
    }
    // Logout User
    public ResponseEntity<String> logoutUser(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        return ResponseEntity.ok("Logout successful");
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(name)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + name));
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
    }

    public User getUserByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        return userOptional.orElse(null); // Returns the user if present, otherwise null
    }


}
