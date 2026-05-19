package com.skillsphere.skillsphere.progress.entity;

import java.time.LocalDateTime;

import com.skillsphere.skillsphere.common.entity.BaseEntity;
import com.skillsphere.skillsphere.learning.entity.Lesson;
import com.skillsphere.skillsphere.user.entity.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lesson_progress", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "lesson_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonProgress extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @Builder.Default
    @Column(nullable = false)
    private Boolean completed = false;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Builder.Default
    @Column(name = "watch_time_seconds", nullable = false)
    private Integer watchTimeSeconds = 0;
}
