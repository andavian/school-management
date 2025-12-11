package org.school.management.academic.application.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AcademicYearResponse (
        String academicYearId,
        Integer year,
        LocalDate startDate,
        LocalDate endDate,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
){

}

