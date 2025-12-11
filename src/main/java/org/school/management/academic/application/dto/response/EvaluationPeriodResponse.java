package org.school.management.academic.application.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;


public record EvaluationPeriodResponse (
        String periodId,
        String academicYearId,
        Integer periodNumber,
        String name,
        LocalDate startDate,
        LocalDate endDate,
        Boolean isInProgress,
        String status,
        LocalDateTime createdAt
){

}
