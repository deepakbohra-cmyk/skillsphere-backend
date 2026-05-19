package com.skillsphere.skillsphere.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skillsphere.skillsphere.user.dto.InstructorDTO;
import com.skillsphere.skillsphere.user.entity.Instructor;
import com.skillsphere.skillsphere.user.repository.InstructorRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/instructors")
@Tag(name = "Instructor Profile Management", description = "APIs for accessing instructor biographies and credentials")
public class InstructorController {

    @Autowired
    private InstructorRepository instructorRepository;

    @Operation(summary = "Get Instructor Profile By ID", description = "Fetch credentials and biography about a specific course instructor.")
    @GetMapping("/{id}")
    public ResponseEntity<InstructorDTO> getInstructorProfile(@PathVariable Long id) {
        Instructor inst = instructorRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Instructor not found with id: " + id));

        String name = inst.getUser().getName();
        String initials = getInitials(name);

        InstructorDTO dto = InstructorDTO.builder()
                .id(inst.getId())
                .name(name)
                .title(inst.getTitle())
                .initials(initials)
                .bio(inst.getBio())
                .experienceYears(inst.getExperienceYears())
                .build();

        return ResponseEntity.ok(dto);
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
}
