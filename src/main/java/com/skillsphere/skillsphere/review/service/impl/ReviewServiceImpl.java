package com.skillsphere.skillsphere.review.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skillsphere.skillsphere.learning.entity.Course;
import com.skillsphere.skillsphere.learning.repository.CourseRepository;
import com.skillsphere.skillsphere.review.dto.ReviewDTO;
import com.skillsphere.skillsphere.review.entity.Review;
import com.skillsphere.skillsphere.review.repository.ReviewRepository;
import com.skillsphere.skillsphere.review.service.ReviewService;
import com.skillsphere.skillsphere.user.entity.User;
import com.skillsphere.skillsphere.user.repository.UserRepository;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<ReviewDTO> getReviews(Long courseId) {
        Course course = courseRepository.findByIdAndIsDeletedFalse(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found: " + courseId));

        return reviewRepository.findAllByCourseOrderByCreatedAtDesc(course).stream()
                .map(this::mapToReviewDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ReviewDTO submitReview(Long courseId, Integer rating, String comment, String username) {
        User user = userRepository.findByEmail(username)
                .or(() -> userRepository.findByLdap(username))
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Course course = courseRepository.findByIdAndIsDeletedFalse(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found: " + courseId));

        Review review = reviewRepository.findByUserAndCourse(user, course)
                .orElse(new Review());

        review.setUser(user);
        review.setCourse(course);
        review.setRating(rating);
        review.setComment(comment);

        Review savedReview = reviewRepository.save(review);
        return mapToReviewDTO(savedReview);
    }

    private ReviewDTO mapToReviewDTO(Review review) {
        String name = review.getUser().getName();
        String initials = getInitials(name);
        String relativeTime = getRelativeTime(review.getCreatedAt());

        return ReviewDTO.builder()
                .id(review.getId())
                .name(name)
                .initials(initials)
                .rating(review.getRating())
                .text(review.getComment())
                .time(relativeTime)
                .build();
    }

    private String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "U";
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
        if (sb.length() == 0) return "U";
        return sb.length() > 2 ? sb.substring(0, 2) : sb.toString();
    }

    private String getRelativeTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "2 weeks ago"; // Default placeholder if audit details not yet set
        }

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(dateTime, now);
        long days = duration.toDays();

        if (days <= 0) {
            long hours = duration.toHours();
            if (hours <= 0) {
                long mins = duration.toMinutes();
                if (mins <= 1) {
                    return "Just now";
                }
                return mins + " minutes ago";
            }
            return hours + " hours ago";
        }

        if (days == 1) {
            return "Yesterday";
        }

        if (days < 7) {
            return days + " days ago";
        }

        if (days < 30) {
            long weeks = days / 7;
            return weeks == 1 ? "1 week ago" : weeks + " weeks ago";
        }

        long months = days / 30;
        return months == 1 ? "1 month ago" : months + " months ago";
    }
}
