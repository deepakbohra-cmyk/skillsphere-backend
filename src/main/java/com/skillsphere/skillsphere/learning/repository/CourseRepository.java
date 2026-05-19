package com.skillsphere.skillsphere.learning.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.skillsphere.skillsphere.learning.entity.Category;
import com.skillsphere.skillsphere.learning.entity.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findBySlugAndIsDeletedFalse(String slug);

    Optional<Course> findByIdAndIsDeletedFalse(Long id);

    List<Course> findAllByIsPublishedTrueAndIsDeletedFalse();

    List<Course> findAllByCategoryAndIsPublishedTrueAndIsDeletedFalse(Category category);

    @Query("SELECT c FROM Course c WHERE c.isPublished = true AND c.isDeleted = false AND " +
           "(LOWER(c.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(c.tagline) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(c.category.name) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Course> searchCourses(@Param("query") String query);

    @Query("SELECT c FROM Course c WHERE c.category = :category AND c.isPublished = true AND c.isDeleted = false AND " +
           "(LOWER(c.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           " LOWER(c.tagline) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Course> searchCoursesByCategory(@Param("category") Category category, @Param("query") String query);
}
