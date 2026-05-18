package com.skillsphere.learning.dto;

import lombok.Data;

@Data
public class ProgramDto {

    private Long id;

    private String name;

    private String description;

    private String category;

    private String level;

    private Integer durationHours;
}