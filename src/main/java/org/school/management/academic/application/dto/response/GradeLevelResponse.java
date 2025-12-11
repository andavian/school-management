package org.school.management.academic.application.dto.response;

import java.time.LocalDateTime;


public record GradeLevelResponse (
        String gradeLevelId,
        String academicYearId,
        Integer yearLevel,
        String division,
        String displayName,  // "4° A,
        String orientationId,
        String orientationName,  // Populated if orientation exist,
        String fullDisplayName,  // "4° A - Técnico Electricista,
        String shift,
        Integer maxStudents,
        String homeroomTeacherId,
        boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
){

}
