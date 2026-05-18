package com.skillsphere.skillsphere.user.dto;

import java.util.Set;

import com.skillsphere.skillsphere.user.entity.Role;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {

    private Long id;

    private String name;

    private String ldap;

    private String email;

    private Set<Role> roles;
}