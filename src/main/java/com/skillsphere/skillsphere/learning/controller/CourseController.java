package com.skillsphere.skillsphere.learning.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/courses")
@Tag(name = "Course Management", description = "APIs for course creation, retrieval, update and deletion")
public class CourseController {
    
}
