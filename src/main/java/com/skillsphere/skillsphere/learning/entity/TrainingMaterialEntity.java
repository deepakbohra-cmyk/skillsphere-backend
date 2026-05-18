package com.skillsphere.learning.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "training_materials")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String materialType;

    private String fileUrl;

    private Integer durationMinutes;

    @ManyToOne
    @JoinColumn(name = "workflow_id")
    private Workflow workflow;
}