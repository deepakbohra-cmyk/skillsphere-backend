package com.skillsphere.skillsphere.learning.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skillsphere.skillsphere.learning.dto.CategoryDTO;
import com.skillsphere.skillsphere.learning.dto.CourseDTO;
import com.skillsphere.skillsphere.learning.dto.CourseDetailDTO;
import com.skillsphere.skillsphere.learning.service.CourseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/courses")
@Tag(name = "Course Management", description = "APIs for course catalog retrieval, enrollment, and progress structure")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Operation(summary = "Get Courses", description = "Fetch courses matching category and search filter. Includes user enrollment status.")
    @GetMapping
    public ResponseEntity<List<CourseDTO>> getCourses(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            Authentication authentication) {

        String username = (authentication != null) ? authentication.getName() : null;
        List<CourseDTO> courses = courseService.getCourses(category, search, username);
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Get Categories", description = "Get list of all active categories in SkillSphere.")
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDTO>> getCategories() {
        return ResponseEntity.ok(courseService.getCategories());
    }

    @Operation(summary = "Get Course Details By Slug", description = "Fetch absolute details of a course including structured sections, lessons, and completion markers.")
    @GetMapping("/{slug}")
    public ResponseEntity<CourseDetailDTO> getCourseDetail(
            @PathVariable String slug,
            Authentication authentication) {

        String username = (authentication != null) ? authentication.getName() : null;
        CourseDetailDTO courseDetail = courseService.getCourseDetail(slug, username);
        return ResponseEntity.ok(courseDetail);
    }

    @Operation(summary = "Enroll User In Course", description = "Enroll currently authenticated user in selected course.")
    @PostMapping("/{id}/enroll")
    public ResponseEntity<String> enroll(
            @PathVariable Long id,
            Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(401).body("Authentication required for enrollment.");
        }
        courseService.enroll(id, authentication.getName());
        return ResponseEntity.ok("Successfully enrolled in course.");
    }
}
