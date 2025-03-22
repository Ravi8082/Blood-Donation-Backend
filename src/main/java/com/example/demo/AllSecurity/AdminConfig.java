package com.example.demo.AllSecurity;

import com.example.demo.Entity.User;
import com.example.demo.Enum.Role;

import com.example.demo.Repository.UserRepo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminConfig {

    @Bean
    CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String adminEmail = "palravi1093@gmail.com";

            // Ensure findByEmail returns Optional<User>
            if (!userRepository.findByEmail(adminEmail).isPresent()) {
                User admin = new User();
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode("808080")); // Default password
                admin.setName("Admin");
                admin.setMobile("8052696360");
                admin.setAge("21");
                admin.setGender("Male");
                admin.setAddress("Mumbai Maharashtra ");
                admin.setBloodgroup("A+");
                admin.setActive(true);
                admin.setRole(Role.ADMIN); // Ensure your User entity has a Role field

                userRepository.save(admin);
                System.out.println("Admin user created successfully.");
            }
        };
    }
}
