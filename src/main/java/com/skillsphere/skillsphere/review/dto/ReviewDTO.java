package com.skillsphere.skillsphere.review.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDTO {
    private Long id;
    private String name;
    private String initials;
    private Integer rating;
    private String text; // comment
    private String time; // e.g. "2 weeks ago"
}
