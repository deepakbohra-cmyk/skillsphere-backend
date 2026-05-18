package com.skillsphere.skillsphere.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.skillsphere.skillsphere.dto.UserDTO;
import com.skillsphere.skillsphere.entity.User;
import com.skillsphere.skillsphere.mapper.UserMapper;
import com.skillsphere.skillsphere.model.UserModel;
import com.skillsphere.skillsphere.repository.UserRepository;
import com.skillsphere.skillsphere.service.UserService;

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

        if (userRepository.existsByLdap(model.getLdap())) {
            throw new RuntimeException("LDAP already exists");
        }

        User user = User.builder()
                .name(model.getName())
                .email(model.getEmail())
                .ldap(model.getLdap())
                .roles(model.getRoles())
                .password(passwordEncoder.encode(model.getPassword()))
                .build();

        User savedUser = userRepository.save(user);

        return UserMapper.mapToDTO(savedUser);
    }

    @Override
    public List<UserModel> getAllUsers() {

        return userRepository.findAll().stream().map(UserMapper::mapToModel).collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserByUsername(String username) {

        User user = userRepository.findByEmail(username).orElseGet(() -> userRepository
                .findByLdap(username)
                .orElseThrow(() -> new RuntimeException("User not found")));

        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .ldap(user.getLdap())
                .roles(user.getRoles())
                .build();
    }
}
