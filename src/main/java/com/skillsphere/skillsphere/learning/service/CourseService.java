package com.skillsphere.skillsphere.learning.service;

import java.util.List;
import com.skillsphere.skillsphere.learning.dto.CategoryDTO;
import com.skillsphere.skillsphere.learning.dto.CourseDTO;
import com.skillsphere.skillsphere.learning.dto.CourseDetailDTO;

public interface CourseService {
    List<CourseDTO> getCourses(String category, String search, String username);
    List<CategoryDTO> getCategories();
    CourseDetailDTO getCourseDetail(String slug, String username);
    void enroll(Long courseId, String username);
}
