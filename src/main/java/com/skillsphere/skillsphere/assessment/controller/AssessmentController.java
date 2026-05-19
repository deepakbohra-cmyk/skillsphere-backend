package com.skillsphere.skillsphere.assessment.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/assessments")
@Tag(name = "Assessment Management", description = "APIs for assessment creation, retrieval, update and deletion")
public class AssessmentController {
    
}
