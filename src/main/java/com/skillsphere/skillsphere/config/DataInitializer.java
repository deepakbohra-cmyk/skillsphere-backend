package com.skillsphere.skillsphere.config;

import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.skillsphere.skillsphere.user.entity.Role;
import com.skillsphere.skillsphere.user.entity.User;
import com.skillsphere.skillsphere.user.repository.UserRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            if (!userRepository.existsByEmail("admin@skillsphere.com")) {

                User admin = User.builder()
                        .name("Admin User")
                        .ldap("admin")
                        .email("admin@skillsphere.com")
                        .password(passwordEncoder.encode("admin123"))
                        .roles(Set.of(Role.ADMIN))
                        .build();

                userRepository.save(admin);
            }

            if (!userRepository.existsByEmail("user@skillsphere.com")) {

                User user = User.builder()
                        .name("Normal User")
                        .ldap("user")
                        .email("user@skillsphere.com")
                        .password(passwordEncoder.encode("user123"))
                        .roles(Set.of(Role.USER))
                        .build();

                userRepository.save(user);
            }
        };
    }
}