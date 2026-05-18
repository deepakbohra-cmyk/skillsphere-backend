package com.skillsphere.skillsphere.user.security;

import java.util.List;
import java.util.stream.Collectors;

import com.skillsphere.skillsphere.user.entity.User;
import com.skillsphere.skillsphere.user.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User existingUser = userRepository
                .findByEmailOrLdap(username, username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found with email/ldap : " + username));

        List<GrantedAuthority> authorities =
                existingUser.getRoles()
                        .stream()
                        .map(role ->
                                new SimpleGrantedAuthority(
                                        "ROLE_" + role.name()))
                        .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                existingUser.getEmail(),
                existingUser.getPassword(),
                authorities);
    }
}