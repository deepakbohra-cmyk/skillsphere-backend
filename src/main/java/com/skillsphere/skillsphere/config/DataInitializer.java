package com.skillsphere.skillsphere.config;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.skillsphere.skillsphere.entity.Role;
import com.skillsphere.skillsphere.entity.User;
import com.skillsphere.skillsphere.repository.UserRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {

        return args -> {

            // Create ADMIN user
            if (!userRepository.existsByEmail("admin@skillsphere.com")) {

                Set<Role> adminRoles = new HashSet<>();
                adminRoles.add(Role.ADMIN);

                User admin = User.builder()
                        .name("System Admin")
                        .ldap("admin001")
                        .email("admin@skillsphere.com")
                        .password(passwordEncoder.encode("Admin@123"))
                        .roles(adminRoles)
                        .build();

                userRepository.save(admin);

                System.out.println("✅ Default ADMIN created");
            }

            // Create STUDENT user
            if (!userRepository.existsByEmail("user@skillsphere.com")) {

                Set<Role> userRoles = new HashSet<>();
                userRoles.add(Role.USER);

                User user = User.builder()
                        .name("Default User")
                        .ldap("user001")
                        .email("user@skillsphere.com")
                        .password(passwordEncoder.encode("User@123"))
                        .roles(userRoles)
                        .build();

                userRepository.save(user);

                System.out.println("✅ Default USER created");
            }
        };
    }
}
