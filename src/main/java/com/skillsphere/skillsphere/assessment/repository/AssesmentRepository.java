package com.skillsphere.skillsphere.assessment.repository;

import org.springframework.stereotype.Repository;

import com.skillsphere.skillsphere.assessment.entity.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface AssesmentRepository extends JpaRepository<Assessment, Long> {
    
}
