package com.skillsphere.skillsphere.user.model;

import java.util.Set;

import com.skillsphere.skillsphere.user.entity.Role;

import lombok.Data;

@Data
public class UserModel {

    private String name;

    private String ldap;

    private String email;

    private String password;

    private Set<Role> roles;
}