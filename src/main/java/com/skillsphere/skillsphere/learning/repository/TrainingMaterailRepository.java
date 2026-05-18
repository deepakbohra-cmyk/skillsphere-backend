package com.skillsphere.learning.repository;

import com.skillsphere.learning.entity.TrainingMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainingMaterialRepository
        extends JpaRepository<TrainingMaterial, Long> {
}