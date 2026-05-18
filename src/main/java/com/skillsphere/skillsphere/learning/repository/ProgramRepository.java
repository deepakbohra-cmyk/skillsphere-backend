package com.skillsphere.learning.repository;

import com.skillsphere.learning.entity.Program;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgramRepository extends JpaRepository<Program, Long> {
}