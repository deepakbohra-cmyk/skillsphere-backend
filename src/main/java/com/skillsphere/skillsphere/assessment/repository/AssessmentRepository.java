package com.skillsphere.assessment.repository;

import com.skillsphere.assessment.entity.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssessmentRepository
        extends JpaRepository<Assessment, Long> {
}