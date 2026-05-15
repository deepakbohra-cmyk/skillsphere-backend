package com.skillsphere.skillsphere.dto.user;

import lombok.Data;

@Data
public class AuthRequest {

    private String email;
    private String password;
}
