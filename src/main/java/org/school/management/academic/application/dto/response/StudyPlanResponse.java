package org.school.management.academic.application.dto.response;

import java.time.LocalDateTime;

public record StudyPlanResponse (
        String studyPlanId,
        String name,
        String code,
        Integer yearLevel,
        String orientationId,
        String orientationName,
        Boolean isGeneralPlan,
        String description,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
){

}
