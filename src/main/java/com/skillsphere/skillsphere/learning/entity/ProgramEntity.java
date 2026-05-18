package com.skillsphere.learning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "programs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Program {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private String category;

    private String level;

    private Integer durationHours;

    private Boolean active = true;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL)
    private List<Workflow> workflows;
}