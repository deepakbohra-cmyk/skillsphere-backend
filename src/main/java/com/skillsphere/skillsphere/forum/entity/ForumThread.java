package com.skillsphere.skillsphere.forum.entity;

import com.skillsphere.skillsphere.common.entity.BaseEntity;
import com.skillsphere.skillsphere.learning.entity.Category;
import com.skillsphere.skillsphere.learning.entity.Course;
import com.skillsphere.skillsphere.user.entity.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "forum_threads")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForumThread extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @Builder.Default
    @Column(name = "reply_count", nullable = false)
    private Integer replyCount = 0;

    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;
}
