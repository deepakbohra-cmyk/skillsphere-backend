package com.skillsphere.skillsphere.learning.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.skillsphere.skillsphere.learning.entity.CourseSection;
import com.skillsphere.skillsphere.learning.entity.Lesson;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    List<Lesson> findAllBySectionAndIsDeletedFalseOrderByPositionAsc(CourseSection section);
}
