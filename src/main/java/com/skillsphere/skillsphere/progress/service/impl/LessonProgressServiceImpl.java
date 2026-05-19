package com.skillsphere.skillsphere.progress.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skillsphere.skillsphere.enrollment.entity.Enrollment;
import com.skillsphere.skillsphere.enrollment.repository.EnrollmentRepository;
import com.skillsphere.skillsphere.learning.entity.Course;
import com.skillsphere.skillsphere.learning.entity.CourseSection;
import com.skillsphere.skillsphere.learning.entity.Lesson;
import com.skillsphere.skillsphere.learning.repository.CourseSectionRepository;
import com.skillsphere.skillsphere.learning.repository.LessonRepository;
import com.skillsphere.skillsphere.progress.dto.LessonProgressRequest;
import com.skillsphere.skillsphere.progress.entity.LessonProgress;
import com.skillsphere.skillsphere.progress.repository.LessonProgressRepository;
import com.skillsphere.skillsphere.progress.service.LessonProgressService;
import com.skillsphere.skillsphere.user.entity.User;
import com.skillsphere.skillsphere.user.repository.UserRepository;

@Service
@Transactional
public class LessonProgressServiceImpl implements LessonProgressService {

    @Autowired
    private LessonProgressRepository lessonProgressRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private CourseSectionRepository sectionRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public LessonProgress getProgress(Long lessonId, String username) {
        User user = getUser(username);
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found: " + lessonId));

        return lessonProgressRepository.findByUserAndLesson(user, lesson)
                .orElseGet(() -> LessonProgress.builder()
                        .user(user)
                        .lesson(lesson)
                        .completed(false)
                        .watchTimeSeconds(0)
                        .build());
    }

    @Override
    public void saveProgress(Long lessonId, LessonProgressRequest request, String username) {
        User user = getUser(username);
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found: " + lessonId));

        LessonProgress progress = lessonProgressRepository.findByUserAndLesson(user, lesson)
                .orElse(LessonProgress.builder()
                        .user(user)
                        .lesson(lesson)
                        .completed(false)
                        .watchTimeSeconds(0)
                        .build());

        boolean wasCompleted = progress.getCompleted();
        
        if (request.getWatchTimeSeconds() != null) {
            progress.setWatchTimeSeconds(request.getWatchTimeSeconds());
        }
        
        if (request.getCompleted() != null) {
            progress.setCompleted(request.getCompleted());
            if (request.getCompleted() && !wasCompleted) {
                progress.setCompletedAt(LocalDateTime.now());
            }
        }

        lessonProgressRepository.save(progress);

        // Re-calculate course enrollment progress
        Course course = lesson.getSection().getCourse();
        recalculateCourseProgress(user, course);
    }

    private User getUser(String username) {
        return userRepository.findByEmail(username)
                .or(() -> userRepository.findByLdap(username))
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    private void recalculateCourseProgress(User user, Course course) {
        Optional<Enrollment> enrollmentOpt = enrollmentRepository.findByUserAndCourse(user, course);
        if (enrollmentOpt.isEmpty()) {
            return;
        }

        Enrollment enrollment = enrollmentOpt.get();

        // Calculate total lessons in course
        List<CourseSection> sections = sectionRepository.findAllByCourseAndIsDeletedFalseOrderByPositionAsc(course);
        int totalLessons = 0;
        for (CourseSection section : sections) {
            totalLessons += lessonRepository.findAllBySectionAndIsDeletedFalseOrderByPositionAsc(section).size();
        }

        if (totalLessons == 0) {
            return;
        }

        // Calculate completed lessons for this user in course
        long completedLessons = lessonProgressRepository.findAllByUserAndLesson_Section_Course(user, course)
                .stream()
                .filter(LessonProgress::getCompleted)
                .count();

        int progressPct = (int) ((completedLessons * 100) / totalLessons);
        enrollment.setProgressPct(progressPct);

        if (progressPct >= 100 && !enrollment.getCompleted()) {
            enrollment.setCompleted(true);
            enrollment.setCompletedAt(LocalDateTime.now());
        }

        enrollmentRepository.save(enrollment);
    }
}
