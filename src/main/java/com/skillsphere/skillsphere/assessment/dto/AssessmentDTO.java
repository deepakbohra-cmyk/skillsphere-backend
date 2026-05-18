package com.skillsphere.assessment.dto;

import lombok.Data;

@Data
public class AssessmentDto {

    private Long id;

    private String title;

    private String description;

    private String assessmentType;

    private Integer totalMarks;

    private Integer passingMarks;

    private Integer maxAttempts;
}