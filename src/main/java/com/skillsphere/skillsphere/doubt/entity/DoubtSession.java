package com.skillsphere.skillsphere.doubt.entity;

import java.time.LocalDateTime;

import com.skillsphere.skillsphere.common.entity.BaseEntity;
import com.skillsphere.skillsphere.doubt.enums.SessionType;
import com.skillsphere.skillsphere.learning.entity.Course;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "doubt_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoubtSession extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false, length = 255)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "session_type", nullable = false, length = 50)
    private SessionType sessionType;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "meeting_url", length = 500)
    private String meetingUrl;

    @Column(name = "recording_url", length = 500)
    private String recordingUrl;
}
