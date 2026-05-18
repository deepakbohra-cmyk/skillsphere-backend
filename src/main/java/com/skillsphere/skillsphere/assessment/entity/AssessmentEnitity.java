package com.skillsphere.assessment.entity;

import com.skillsphere.learning.entity.Workflow;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "assessments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private String assessmentType;

    private Integer totalMarks;

    private Integer passingMarks;

    private Integer maxAttempts;

    private Double weightage;

    private Boolean legacyProcess = false;

    private Boolean active = true;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "workflow_id")
    private Workflow workflow;

    @OneToMany(mappedBy = "assessment")
    private List<Question> questions;
}