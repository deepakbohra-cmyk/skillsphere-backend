package com.skillsphere.skillsphere.user.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.skillsphere.skillsphere.user.enums.SourceType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_points")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false, length = 50)
    private SourceType sourceType;

    @Column(name = "source_id")
    private Long sourceId;

    @Builder.Default
    @Column(name = "points_raw", nullable = false)
    private Double pointsRaw = 0.0;

    @Builder.Default
    @Column(nullable = false)
    private Double weightage = 1.0;

    @Builder.Default
    @Column(name = "points_weighted", nullable = false)
    private Double pointsWeighted = 0.0;

    private String description;

    @CreatedDate
    @Column(name = "earned_at", nullable = false, updatable = false)
    private LocalDateTime earnedAt;
}
