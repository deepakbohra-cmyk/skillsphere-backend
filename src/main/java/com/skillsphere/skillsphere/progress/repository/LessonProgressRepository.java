package com.skillsphere.skillsphere.progress.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.skillsphere.skillsphere.learning.entity.Course;
import com.skillsphere.skillsphere.learning.entity.Lesson;
import com.skillsphere.skillsphere.progress.entity.LessonProgress;
import com.skillsphere.skillsphere.user.entity.User;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {
    Optional<LessonProgress> findByUserAndLesson(User user, Lesson lesson);
    List<LessonProgress> findAllByUserAndLesson_Section_Course(User user, Course course);
}
