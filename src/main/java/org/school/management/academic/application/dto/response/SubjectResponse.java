package org.school.management.academic.application.dto.response;

import java.time.LocalDateTime;

public record SubjectResponse (
        String subjectId,
        String name,
        String code,
        Integer yearLevel,
        String orientationId,
        String orientationName,  // Populated if orientation exist,
        boolean isCommonSubject,  // true if orientationId is nul,
        Integer weeklyHours,
        String description,
        boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
){

}
