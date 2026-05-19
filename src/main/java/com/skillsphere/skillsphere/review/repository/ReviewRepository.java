package com.skillsphere.skillsphere.review.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.skillsphere.skillsphere.learning.entity.Course;
import com.skillsphere.skillsphere.review.entity.Review;
import com.skillsphere.skillsphere.user.entity.User;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByCourseOrderByCreatedAtDesc(Course course);
    Optional<Review> findByUserAndCourse(User user, Course course);
}
