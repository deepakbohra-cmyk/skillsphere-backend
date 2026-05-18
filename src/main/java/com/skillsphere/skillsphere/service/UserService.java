package com.skillsphere.skillsphere.service;

import java.util.List;

import com.skillsphere.skillsphere.dto.UserDTO;
import com.skillsphere.skillsphere.model.UserModel;

public interface UserService {

    UserDTO createUser(UserModel model);

    List<UserModel> getAllUsers();

    UserDTO getUserByUsername(String username);
}