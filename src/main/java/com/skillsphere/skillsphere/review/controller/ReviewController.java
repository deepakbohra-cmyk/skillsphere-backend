package com.skillsphere.skillsphere.review.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skillsphere.skillsphere.review.dto.ReviewDTO;
import com.skillsphere.skillsphere.review.dto.ReviewRequest;
import com.skillsphere.skillsphere.review.service.ReviewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/courses/{courseId}/reviews")
@Tag(name = "Review Management", description = "APIs for course reviews retrieval and submission")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Operation(summary = "Get Course Reviews", description = "Get list of all submitted reviews for a course.")
    @GetMapping
    public ResponseEntity<List<ReviewDTO>> getReviews(@PathVariable Long courseId) {
        List<ReviewDTO> reviews = reviewService.getReviews(courseId);
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "Submit Course Review", description = "Submit a rating and comment for a course.")
    @PostMapping
    public ResponseEntity<ReviewDTO> submitReview(
            @PathVariable Long courseId,
            @RequestBody ReviewRequest request,
            Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }

        ReviewDTO review = reviewService.submitReview(
                courseId,
                request.getRating(),
                request.getComment(),
                authentication.getName());

        return ResponseEntity.ok(review);
    }
}
