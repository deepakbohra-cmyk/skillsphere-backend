package com.skillsphere.skillsphere.learning.entity;

import com.skillsphere.skillsphere.common.entity.BaseEntity;
import com.skillsphere.skillsphere.learning.enums.LessonType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lessons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lesson extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private LessonType type;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "video_url", length = 500)
    private String videoUrl;

    @Builder.Default
    @Column(name = "duration_seconds")
    private Integer durationSeconds = 0;

    @Builder.Default
    @Column(name = "is_free", nullable = false)
    private Boolean isFree = false;

    @Builder.Default
    @Column(name = "is_published", nullable = false)
    private Boolean isPublished = false;

    @Builder.Default
    @Column(nullable = false)
    private Integer position = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private CourseSection section;

    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;
}
