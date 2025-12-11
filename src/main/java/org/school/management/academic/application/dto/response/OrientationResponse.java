package org.school.management.academic.application.dto.response;

import org.school.management.academic.domain.valueobject.OrientationCode;
import org.school.management.academic.domain.valueobject.Year;
import org.school.management.academic.domain.valueobject.ids.OrientationId;

import java.time.LocalDateTime;


public record OrientationResponse (
        OrientationId orientationId,
        String name,
        OrientationCode code,
        String description,
        Year availableFromYear,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
){

}
