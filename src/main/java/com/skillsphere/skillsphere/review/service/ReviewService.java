package com.skillsphere.skillsphere.review.service;

import java.util.List;
import com.skillsphere.skillsphere.review.dto.ReviewDTO;

public interface ReviewService {
    List<ReviewDTO> getReviews(Long courseId);
    ReviewDTO submitReview(Long courseId, Integer rating, String comment, String username);
}
