package com.skillsphere.skillsphere.user.service;

import java.util.List;

import com.skillsphere.skillsphere.user.dto.UserDTO;
import com.skillsphere.skillsphere.user.model.UserModel;

public interface UserService {

    UserDTO createUser(UserModel model);

    List<UserModel> getAllUsers();

    UserDTO getUserByUsername(String username);
}