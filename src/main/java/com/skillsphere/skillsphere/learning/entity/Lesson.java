package com.skillsphere.skillsphere.learning.entity;


import com.skillsphere.skillsphere.common.enums.BaseEntity;
import com.skillsphere.skillsphere.learning.enums.LessonType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "lessons")
@Getter
@Setter
public class Lesson extends BaseEntity {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

private String title;

private String description;

@Enumerated(EnumType.STRING)
private LessonType type;

private String content;

private String videoUrl;

private Integer durationMinutes;

private Boolean isFree;

private Boolean isPublished;

private Integer position;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "section_id")
private CourseSection section;
}
