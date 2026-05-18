package com.skillsphere.skillsphere.user.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {

    private String token;

    private int status;

    private String message;

    private LocalDateTime timestamp;
}