package com.skillsphere.skillsphere.forum.entity;

import com.skillsphere.skillsphere.common.entity.BaseEntity;
import com.skillsphere.skillsphere.user.entity.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "forum_replies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForumReply extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thread_id", nullable = false)
    private ForumThread thread;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;
}
