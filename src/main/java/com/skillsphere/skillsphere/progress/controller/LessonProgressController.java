package com.skillsphere.skillsphere.progress.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skillsphere.skillsphere.progress.dto.LessonProgressDTO;
import com.skillsphere.skillsphere.progress.dto.LessonProgressRequest;
import com.skillsphere.skillsphere.progress.entity.LessonProgress;
import com.skillsphere.skillsphere.progress.service.LessonProgressService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/courses/{courseId}/lessons/{lessonId}/progress")
@Tag(name = "Lesson Progress Management", description = "APIs for tracking and saving individual lesson completions and watch times")
public class LessonProgressController {

    @Autowired
    private LessonProgressService lessonProgressService;

    @Operation(summary = "Get Lesson Progress", description = "Fetch progress status (watch time, completed) for a specific lesson.")
    @GetMapping
    public ResponseEntity<LessonProgressDTO> getProgress(
            @PathVariable Long courseId,
            @PathVariable Long lessonId,
            Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }

        LessonProgress progress = lessonProgressService.getProgress(lessonId, authentication.getName());
        LessonProgressDTO dto = LessonProgressDTO.builder()
                .id(progress.getId() != null ? "lp" + progress.getId() : null)
                .lessonId("l" + progress.getLesson().getId())
                .completed(progress.getCompleted())
                .watchTimeSeconds(progress.getWatchTimeSeconds())
                .build();

        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Save Lesson Progress", description = "Save/update watch time or mark a lesson as completed.")
    @PostMapping
    public ResponseEntity<String> saveProgress(
            @PathVariable Long courseId,
            @PathVariable Long lessonId,
            @RequestBody LessonProgressRequest request,
            Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(401).body("Authentication required.");
        }

        lessonProgressService.saveProgress(lessonId, request, authentication.getName());
        return ResponseEntity.ok("Progress updated successfully.");
    }
}
