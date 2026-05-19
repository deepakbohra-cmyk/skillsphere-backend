package com.skillsphere.skillsphere.learning.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skillsphere.skillsphere.enrollment.entity.Enrollment;
import com.skillsphere.skillsphere.enrollment.repository.EnrollmentRepository;
import com.skillsphere.skillsphere.learning.dto.CategoryDTO;
import com.skillsphere.skillsphere.learning.dto.CourseDTO;
import com.skillsphere.skillsphere.learning.dto.CourseDetailDTO;
import com.skillsphere.skillsphere.learning.dto.LessonDTO;
import com.skillsphere.skillsphere.learning.dto.SectionDTO;
import com.skillsphere.skillsphere.learning.entity.Category;
import com.skillsphere.skillsphere.learning.entity.Course;
import com.skillsphere.skillsphere.learning.entity.CourseSection;
import com.skillsphere.skillsphere.learning.entity.Lesson;
import com.skillsphere.skillsphere.learning.repository.CategoryRepository;
import com.skillsphere.skillsphere.learning.repository.CourseRepository;
import com.skillsphere.skillsphere.learning.repository.CourseSectionRepository;
import com.skillsphere.skillsphere.learning.repository.LessonRepository;
import com.skillsphere.skillsphere.progress.entity.LessonProgress;
import com.skillsphere.skillsphere.progress.repository.LessonProgressRepository;
import com.skillsphere.skillsphere.learning.service.CourseService;
import com.skillsphere.skillsphere.user.dto.InstructorDTO;
import com.skillsphere.skillsphere.user.entity.Instructor;
import com.skillsphere.skillsphere.user.entity.User;
import com.skillsphere.skillsphere.user.repository.UserRepository;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CourseSectionRepository sectionRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private LessonProgressRepository lessonProgressRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<CourseDTO> getCourses(String category, String search, String username) {
        User user = getUser(username).orElse(null);
        List<Course> courses;

        if (category != null && !category.isEmpty() && !category.equalsIgnoreCase("All")) {
            Optional<Category> catOpt = categoryRepository.findByName(category);
            if (catOpt.isPresent()) {
                if (search != null && !search.isEmpty()) {
                    courses = courseRepository.searchCoursesByCategory(catOpt.get(), search);
                } else {
                    courses = courseRepository.findAllByCategoryAndIsPublishedTrueAndIsDeletedFalse(catOpt.get());
                }
            } else {
                courses = Collections.emptyList();
            }
        } else {
            if (search != null && !search.isEmpty()) {
                courses = courseRepository.searchCourses(search);
            } else {
                courses = courseRepository.findAllByIsPublishedTrueAndIsDeletedFalse();
            }
        }

        return courses.stream()
                .map(course -> mapToCourseDTO(course, user))
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryDTO> getCategories() {
        return categoryRepository.findAll().stream()
                .filter(cat -> !cat.getIsDeleted())
                .map(cat -> CategoryDTO.builder()
                        .id(cat.getId())
                        .name(cat.getName())
                        .slug(cat.getSlug())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public CourseDetailDTO getCourseDetail(String slug, String username) {
        User user = getUser(username).orElse(null);
        Course course = courseRepository.findBySlugAndIsDeletedFalse(slug)
                .orElseThrow(() -> new RuntimeException("Course not found with slug: " + slug));

        CourseDTO baseDto = mapToCourseDTO(course, user);

        // Fetch curriculum structure
        List<CourseSection> sections = sectionRepository.findAllByCourseAndIsDeletedFalseOrderByPositionAsc(course);
        List<SectionDTO> curriculum = new ArrayList<>();

        // Fetch all progress for user if logged in
        Set<Long> completedLessonIds = Collections.emptySet();
        if (user != null) {
            completedLessonIds = lessonProgressRepository.findAllByUserAndLesson_Section_Course(user, course)
                    .stream()
                    .filter(LessonProgress::getCompleted)
                    .map(lp -> lp.getLesson().getId())
                    .collect(Collectors.toSet());
        }

        for (CourseSection section : sections) {
            List<Lesson> lessons = lessonRepository.findAllBySectionAndIsDeletedFalseOrderByPositionAsc(section);
            
            final Set<Long> finalCompletedIds = completedLessonIds;
            List<LessonDTO> lessonDtos = lessons.stream()
                    .map(lesson -> mapToLessonDTO(lesson, finalCompletedIds.contains(lesson.getId())))
                    .collect(Collectors.toList());

            curriculum.add(SectionDTO.builder()
                    .id("s" + section.getId())
                    .title(section.getTitle())
                    .lessons(lessonDtos)
                    .build());
        }

        return CourseDetailDTO.builder()
                .id(baseDto.getId())
                .title(baseDto.getTitle())
                .tagline(baseDto.getTagline())
                .category(baseDto.getCategory())
                .level(baseDto.getLevel())
                .duration(baseDto.getDuration())
                .modules(baseDto.getModules())
                .icon(baseDto.getIcon())
                .iconColor(baseDto.getIconColor())
                .iconBg(baseDto.getIconBg())
                .tagClass(baseDto.getTagClass())
                .rating(baseDto.getRating())
                .reviewCount(baseDto.getReviewCount())
                .enrolledCount(baseDto.getEnrolledCount())
                .lastUpdated(baseDto.getLastUpdated())
                .instructor(baseDto.getInstructor())
                .progress(baseDto.getProgress())
                .enrolled(baseDto.getEnrolled())
                .mandatory(baseDto.getMandatory())
                .description(course.getDescription())
                .whatYouLearn(course.getWhatYouLearn() != null ? course.getWhatYouLearn() : Collections.emptyList())
                .requirements(course.getRequirements() != null ? course.getRequirements() : Collections.emptyList())
                .curriculum(curriculum)
                .build();
    }

    @Override
    public void enroll(Long courseId, String username) {
        User user = getUser(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        Course course = courseRepository.findByIdAndIsDeletedFalse(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));

        if (enrollmentRepository.existsByUserAndCourse(user, course)) {
            // Already enrolled or wishlisted
            Enrollment enrollment = enrollmentRepository.findByUserAndCourse(user, course).get();
            if (enrollment.getIsWishlist()) {
                enrollment.setIsWishlist(false);
                enrollmentRepository.save(enrollment);
            }
            return;
        }

        Enrollment enrollment = Enrollment.builder()
                .user(user)
                .course(course)
                .enrolledAt(LocalDateTime.now())
                .progressPct(0)
                .completed(false)
                .isWishlist(false)
                .build();

        enrollmentRepository.save(enrollment);

        // Increment enrolled count
        course.setEnrolledCount(course.getEnrolledCount() + 1);
        courseRepository.save(course);
    }

    private Optional<User> getUser(String username) {
        if (username == null || username.isEmpty()) {
            return Optional.empty();
        }
        return userRepository.findByEmail(username).or(() -> userRepository.findByLdap(username));
    }

    private CourseDTO mapToCourseDTO(Course course, User user) {
        // Enrolled and progress computation
        boolean enrolled = false;
        Integer progress = null;
        if (user != null) {
            Optional<Enrollment> enrollmentOpt = enrollmentRepository.findByUserAndCourse(user, course);
            if (enrollmentOpt.isPresent() && !enrollmentOpt.get().getIsWishlist()) {
                enrolled = true;
                progress = enrollmentOpt.get().getProgressPct();
            }
        }

        // Instructor mapping
        InstructorDTO instructorDTO = null;
        if (course.getInstructor() != null) {
            Instructor inst = course.getInstructor();
            String name = inst.getUser().getName();
            String title = inst.getTitle();
            String initials = getInitials(name);
            instructorDTO = InstructorDTO.builder()
                    .id(inst.getId())
                    .name(name)
                    .title(title)
                    .initials(initials)
                    .bio(inst.getBio())
                    .experienceYears(inst.getExperienceYears())
                    .build();
        }

        // Section count as modules
        List<CourseSection> sections = sectionRepository.findAllByCourseAndIsDeletedFalseOrderByPositionAsc(course);
        int modules = sections.size();

        // Duration mapping
        String duration = "0h";
        if (course.getDurationMinutes() != null && course.getDurationMinutes() > 0) {
            int mins = course.getDurationMinutes();
            if (mins >= 60) {
                duration = (mins / 60) + "h";
            } else {
                duration = mins + "m";
            }
        }

        // Last updated format
        String lastUpdated = "May 2025";
        if (course.getLastUpdatedAt() != null) {
            lastUpdated = course.getLastUpdatedAt().format(DateTimeFormatter.ofPattern("MMM yyyy"));
        } else if (course.getCreatedAt() != null) {
            lastUpdated = course.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM yyyy"));
        }

        // Dynamic visual mapping
        String catName = course.getCategory() != null ? course.getCategory().getName() : "";
        String icon = "Database";
        String iconColor = "text-blue-600";
        String iconBg = "bg-blue-50";
        String tagClass = "bg-blue-50 text-blue-600";

        if (catName.equalsIgnoreCase("ETL")) {
            icon = "GitMerge";
            iconColor = "text-emerald-500";
            iconBg = "bg-emerald-50";
            tagClass = "bg-emerald-50 text-emerald-600";
        } else if (catName.equalsIgnoreCase("Cybersecurity")) {
            icon = "ShieldCheck";
            iconColor = "text-red-500";
            iconBg = "bg-red-50";
            tagClass = "bg-red-50 text-red-500";
        } else if (catName.equalsIgnoreCase("Data Science")) {
            icon = "ChartColumn";
            iconColor = "text-amber-500";
            iconBg = "bg-amber-50";
            tagClass = "bg-amber-50 text-amber-600";
        } else if (catName.equalsIgnoreCase("Leadership")) {
            icon = "Users";
            iconColor = "text-blue-600";
            iconBg = "bg-blue-50";
            tagClass = "bg-blue-50 text-blue-600";
        } else if (catName.equalsIgnoreCase("Mandatory")) {
            icon = "ClipboardCheck";
            iconColor = "text-emerald-500";
            iconBg = "bg-emerald-50";
            tagClass = "bg-emerald-50 text-emerald-600";
        }

        return CourseDTO.builder()
                .id(course.getSlug())
                .title(course.getTitle())
                .tagline(course.getTagline())
                .category(catName)
                .level(course.getLevel())
                .duration(duration)
                .modules(modules)
                .icon(icon)
                .iconColor(iconColor)
                .iconBg(iconBg)
                .tagClass(tagClass)
                .rating(course.getRating() != null ? course.getRating() : 0.0)
                .reviewCount(course.getReviewCount())
                .enrolledCount(course.getEnrolledCount())
                .lastUpdated(lastUpdated)
                .instructor(instructorDTO)
                .progress(progress)
                .enrolled(enrolled)
                .mandatory(course.getIsMandatory())
                .build();
    }

    private LessonDTO mapToLessonDTO(Lesson lesson, boolean completed) {
        // format duration (mm:ss or X min)
        String duration = "0:00";
        if (lesson.getDurationSeconds() != null && lesson.getDurationSeconds() > 0) {
            int totalSecs = lesson.getDurationSeconds();
            if (lesson.getType().name().equalsIgnoreCase("VIDEO")) {
                int mins = totalSecs / 60;
                int secs = totalSecs % 60;
                duration = String.format("%d:%02d", mins, secs);
            } else {
                int mins = Math.max(1, totalSecs / 60);
                duration = mins + " min";
            }
        }

        return LessonDTO.builder()
                .id("l" + lesson.getId())
                .title(lesson.getTitle())
                .type(lesson.getType().name().toLowerCase())
                .duration(duration)
                .durationSeconds(lesson.getDurationSeconds())
                .free(lesson.getIsFree())
                .completed(completed)
                .videoUrl(lesson.getVideoUrl())
                .description(lesson.getDescription())
                .build();
    }

    private String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "VR";
        }
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part.equalsIgnoreCase("Dr.") || part.equalsIgnoreCase("Prof.")) {
                continue;
            }
            if (!part.isEmpty()) {
                sb.append(Character.toUpperCase(part.charAt(0)));
            }
        }
        if (sb.length() == 0) return "SK";
        return sb.length() > 2 ? sb.substring(0, 2) : sb.toString();
    }
}
