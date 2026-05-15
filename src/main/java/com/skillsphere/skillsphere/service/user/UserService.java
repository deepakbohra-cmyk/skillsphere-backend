package com.skillsphere.skillsphere.service.user;

import java.util.List;

import com.skillsphere.skillsphere.dto.user.UserDTO;
import com.skillsphere.skillsphere.model.UserModel;

public interface UserService {

    UserDTO createUser(UserModel model);

    List<UserModel> getAllUsers();
}