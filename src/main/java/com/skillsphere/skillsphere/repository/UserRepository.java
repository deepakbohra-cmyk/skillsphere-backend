package com.skillsphere.skillsphere.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skillsphere.skillsphere.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByLdap(String ldap);

    boolean existsByEmail(String email);

    boolean existsByLdap(String ldap);
}