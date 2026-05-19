package com.skillsphere.skillsphere.user.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import com.skillsphere.skillsphere.common.util.JwtTokenUtil;
import com.skillsphere.skillsphere.user.dto.JwtResponse;
import com.skillsphere.skillsphere.user.dto.UserDTO;
import com.skillsphere.skillsphere.user.model.AuthModel;
import com.skillsphere.skillsphere.user.model.UserModel;
import com.skillsphere.skillsphere.user.security.CustomUserDetailsService;
import com.skillsphere.skillsphere.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "User Management", description = "APIs for user registration, login and management")
public class UserController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Operation(summary = "Register User", description = "Register a new user into SkillSphere")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "201",
                        description = "User registered successfully",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = UserDTO.class))),
                @ApiResponse(responseCode = "400", description = "Invalid request")
            })
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody UserModel userModel) {

        return new ResponseEntity<>(userService.createUser(userModel), HttpStatus.CREATED);
    }

    @Operation(summary = "Login User", description = "Authenticate user using email or ldap")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Login successful",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = JwtResponse.class))),
                @ApiResponse(responseCode = "401", description = "Invalid credentials")
            })
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody AuthModel authModel) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authModel.getUsername(), authModel.getPassword()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(authModel.getUsername());

        String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(
                new JwtResponse(token, HttpStatus.OK.value(), "Login Successful", LocalDateTime.now()));
    }

    @Operation(summary = "Get All Users", description = "Fetch all registered users")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Users fetched successfully"),
                @ApiResponse(responseCode = "403", description = "Access denied")
            })
    @GetMapping("/users")
    public ResponseEntity<List<UserModel>> getAllUsers() {

        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/test")
    public String test() {
        return "Swagger Working";
    }

    @Operation(summary = "Get User Details By JWT Token", description = "Fetch logged in user details using JWT token")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "User fetched successfully"),
                @ApiResponse(responseCode = "401", description = "Invalid token")
            })
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(Authentication authentication) {

        String username = authentication.getName();

        UserDTO user = userService.getUserByUsername(username);

        return ResponseEntity.ok(user);
    }
}
