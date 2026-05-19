package com.skillsphere.skillsphere.user.entity;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "leaderboard")
@Immutable
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Leaderboard {

    @Id
    @Column(name = "user_id")
    private Long userId;

    private String name;

    private String email;

    @Column(name = "total_points")
    private Double totalPoints;

    private Long rank;
}
