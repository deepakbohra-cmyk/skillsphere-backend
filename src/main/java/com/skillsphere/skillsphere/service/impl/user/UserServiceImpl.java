package com.skillsphere.skillsphere.service.impl.user;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.skillsphere.skillsphere.dto.user.UserDTO;
import com.skillsphere.skillsphere.entity.User;
import com.skillsphere.skillsphere.model.UserModel;
import com.skillsphere.skillsphere.repository.UserRepository;
import com.skillsphere.skillsphere.service.user.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDTO createUser(UserModel model) {

        if (userRepository.existsByEmail(model.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .name(model.getName())
                .email(model.getEmail())
                .ldap(model.getLdap())
                .roles(model.getRoles())
                .password(passwordEncoder.encode(model.getPassword()))
                .build();

        User savedUser = userRepository.save(user);

        return mapToDTO(savedUser);
    }

    @Override
    public List<UserModel> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::mapToModel)
                .collect(Collectors.toList());
    }

    private UserDTO mapToDTO(User user) {

        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .ldap(user.getLdap())
                .roles(user.getRoles())
                .build();
    }

    private UserModel mapToModel(User user) {

        UserModel model = new UserModel();

        model.setName(user.getName());
        model.setEmail(user.getEmail());
        model.setLdap(user.getLdap());
        model.setRoles(user.getRoles());

        return model;
    }
}