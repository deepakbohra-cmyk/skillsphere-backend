package com.skillsphere.skillsphere.controller.user;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.skillsphere.skillsphere.dto.user.JwtResponse;
import com.skillsphere.skillsphere.dto.user.UserDTO;
import com.skillsphere.skillsphere.model.AuthModel;
import com.skillsphere.skillsphere.model.UserModel;
import com.skillsphere.skillsphere.security.CustomUserDetailsService;
import com.skillsphere.skillsphere.service.user.UserService;
import com.skillsphere.skillsphere.util.JwtTokenUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
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

    @Operation(summary = "Login User", description = "Authenticate user and generate JWT token")
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
                new UsernamePasswordAuthenticationToken(authModel.getEmail(), authModel.getPassword()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(authModel.getEmail());

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
}
