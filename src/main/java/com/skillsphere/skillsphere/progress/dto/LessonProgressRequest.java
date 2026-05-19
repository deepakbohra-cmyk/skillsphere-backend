package com.skillsphere.skillsphere.progress.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LessonProgressRequest {
    private Boolean completed;
    private Integer watchTimeSeconds;
}
