package com.skillsphere.skillsphere.enrollment.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.skillsphere.skillsphere.enrollment.entity.Enrollment;
import com.skillsphere.skillsphere.learning.entity.Course;
import com.skillsphere.skillsphere.user.entity.User;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByUserAndCourse(User user, Course course);
    List<Enrollment> findAllByUser(User user);
    List<Enrollment> findAllByUserAndIsWishlistFalse(User user);
    boolean existsByUserAndCourse(User user, Course course);
}
