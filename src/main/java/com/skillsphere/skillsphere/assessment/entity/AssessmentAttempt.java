package com.skillsphere.skillsphere.assessment.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;

import com.skillsphere.skillsphere.common.entity.BaseEntity;
import com.skillsphere.skillsphere.user.entity.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "assessment_attempts", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"assessment_id", "user_id", "attempt_number"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentAttempt extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id", nullable = false)
    private Assessment assessment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "attempt_number", nullable = false)
    private Integer attemptNumber;

    @Builder.Default
    @Column(nullable = false)
    private Double score = 0.0;

    @Builder.Default
    @Column(name = "score_pct", nullable = false)
    private Double scorePct = 0.0;

    @Builder.Default
    @Column(name = "weighted_points", nullable = false)
    private Double weightedPoints = 0.0;

    @Builder.Default
    @Column(nullable = false)
    private Boolean passed = false;

    @CreatedDate
    @Column(name = "started_at", nullable = false, updatable = false)
    private LocalDateTime startedAt;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Builder.Default
    @Column(name = "lead_notified", nullable = false)
    private Boolean leadNotified = false;

    @Column(name = "lead_notified_at")
    private LocalDateTime leadNotifiedAt;
}
