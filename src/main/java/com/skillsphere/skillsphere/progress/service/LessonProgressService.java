package com.skillsphere.skillsphere.progress.service;

import com.skillsphere.skillsphere.progress.dto.LessonProgressRequest;
import com.skillsphere.skillsphere.progress.entity.LessonProgress;

public interface LessonProgressService {
    LessonProgress getProgress(Long lessonId, String username);
    void saveProgress(Long lessonId, LessonProgressRequest request, String username);
}
