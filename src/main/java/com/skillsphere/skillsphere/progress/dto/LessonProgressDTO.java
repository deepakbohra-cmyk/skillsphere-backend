package com.skillsphere.skillsphere.progress.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonProgressDTO {
    private String id;
    private String lessonId;
    private Boolean completed;
    private Integer watchTimeSeconds;
}
