package com.skillsphere.skillsphere.learning.dto;

import com.skillsphere.skillsphere.user.dto.InstructorDTO;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDTO {
    private String id; // maps to course slug for frontend routing
    private String title;
    private String tagline;
    private String category;
    private String level;
    private String duration;
    private Integer modules;
    private String icon;
    private String iconColor;
    private String iconBg;
    private String tagClass;
    private Double rating;
    private Integer reviewCount;
    private Integer enrolledCount;
    private String lastUpdated;
    private InstructorDTO instructor;
    private Integer progress;
    private Boolean enrolled;
    private Boolean mandatory;
}
