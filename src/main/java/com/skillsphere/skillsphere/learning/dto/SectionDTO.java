package com.skillsphere.skillsphere.learning.dto;

import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionDTO {
    private String id;
    private String title;
    private List<LessonDTO> lessons;
}
