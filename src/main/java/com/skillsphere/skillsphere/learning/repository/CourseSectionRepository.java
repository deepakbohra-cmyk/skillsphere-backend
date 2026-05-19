package com.skillsphere.skillsphere.learning.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.skillsphere.skillsphere.learning.entity.Course;
import com.skillsphere.skillsphere.learning.entity.CourseSection;

@Repository
public interface CourseSectionRepository extends JpaRepository<CourseSection, Long> {
    List<CourseSection> findAllByCourseAndIsDeletedFalseOrderByPositionAsc(Course course);
}
