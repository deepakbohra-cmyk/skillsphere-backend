package com.skillsphere.skillsphere.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.skillsphere.skillsphere.security.*;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    // @Bean
    // public SecurityFilterChain filterChain(
    // HttpSecurity http) throws Exception {

    // return http
    // .csrf(AbstractHttpConfigurer::disable)
    // .headers(headers ->
    // headers.frameOptions(
    // frame -> frame.sameOrigin()))
    // .authorizeHttpRequests(auth -> auth
    // .requestMatchers(
    // "/api/login",
    // "/api/register",
    // "/swagger-ui/**",
    // "/v3/api-docs/**",
    // "/h2-console/**")
    // .permitAll()
    // .anyRequest()
    // .authenticated())
    // .sessionManagement(session ->
    // session.sessionCreationPolicy(
    // SessionCreationPolicy.STATELESS))
    // .authenticationProvider(authenticationProvider())
    // .addFilterBefore(
    // jwtRequestFilter,
    // UsernamePasswordAuthenticationFilter.class)
    // .build();
    // }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http.csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {

        return config.getAuthenticationManager();
    }
}
