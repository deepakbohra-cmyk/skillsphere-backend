package com.skillsphere.skillsphere.enrollment.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;

import com.skillsphere.skillsphere.common.entity.BaseEntity;
import com.skillsphere.skillsphere.learning.entity.Course;
import com.skillsphere.skillsphere.user.entity.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "enrollments", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "course_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @CreatedDate
    @Column(name = "enrolled_at", nullable = false, updatable = false)
    private LocalDateTime enrolledAt;

    @Builder.Default
    @Column(name = "progress_pct", nullable = false)
    private Integer progressPct = 0;

    @Builder.Default
    @Column(nullable = false)
    private Boolean completed = false;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Builder.Default
    @Column(name = "is_wishlist", nullable = false)
    private Boolean isWishlist = false;

    @Column(name = "wishlist_approved")
    private Boolean wishlistApproved;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;
}
