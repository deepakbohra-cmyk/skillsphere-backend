package com.skillsphere.skillsphere.mapper;

import com.skillsphere.skillsphere.dto.user.UserDTO;
import com.skillsphere.skillsphere.entity.User;
import com.skillsphere.skillsphere.model.UserModel;

public class UserMapper {

    private UserMapper() {
    }

    public static UserModel mapToModel(User user) {

        UserModel model = new UserModel();

        model.setName(user.getName());
        model.setEmail(user.getEmail());
        model.setLdap(user.getLdap());
        model.setRoles(user.getRoles());

        return model;
    }

    public static UserDTO mapToDTO(User user) {

        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .ldap(user.getLdap())
                .roles(user.getRoles())
                .build();
    }
}