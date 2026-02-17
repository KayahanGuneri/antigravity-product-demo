package com.antigravity.demo.config;

import com.antigravity.demo.model.Role;
import com.antigravity.demo.model.User;
import com.antigravity.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner dataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Seed Admin
            if (!userRepository.existsByEmail("admin@demo.com")) {
                User admin = new User();
                admin.setEmail("admin@demo.com");
                admin.setPasswordHash(passwordEncoder.encode("Admin123!"));
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);
                System.out.println("Seeded admin user: admin@demo.com");
            }

            // Seed User
            if (!userRepository.existsByEmail("user@demo.com")) {
                User user = new User();
                user.setEmail("user@demo.com");
                user.setPasswordHash(passwordEncoder.encode("User123!"));
                user.setRole(Role.USER);
                userRepository.save(user);
                System.out.println("Seeded regular user: user@demo.com");
            }
        };
    }
}
