package com.skillsphere.learning.dto;

import lombok.Data;

@Data
public class WorkflowDto {

    private Long id;

    private String name;

    private String description;

    private Integer sequenceOrder;

    private Long programId;
}