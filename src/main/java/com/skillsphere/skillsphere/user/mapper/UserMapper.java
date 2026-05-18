package com.skillsphere.skillsphere.user.mapper;

import com.skillsphere.skillsphere.user.entity.User;
import com.skillsphere.skillsphere.user.dto.UserDTO;
import com.skillsphere.skillsphere.user.model.UserModel;

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