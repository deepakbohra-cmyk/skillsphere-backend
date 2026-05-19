package com.skillsphere.skillsphere.learning.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonDTO {
    private String id; // We can use the string ID (or slug/string conversion of Long ID)
    private String title;
    private String type; // video, reading, quiz, assignment
    private String duration; // e.g. "8:34", "10 min"
    private Integer durationSeconds;
    private Boolean free;
    private Boolean completed;
    private String videoUrl;
    private String description;
}
