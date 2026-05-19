package com.skillsphere.skillsphere.user.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstructorDTO {
    private Long id;
    private String name;
    private String title;
    private String initials;
    private String bio;
    private Integer experienceYears;
}
