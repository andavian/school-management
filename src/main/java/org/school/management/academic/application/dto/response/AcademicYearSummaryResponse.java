package org.school.management.academic.application.dto.response;

public record AcademicYearSummaryResponse (
        AcademicYearResponse academicYear,
        Integer totalGradeLevels,
        Integer activeGradeLevels,
        Integer totalRegistries,
        Integer activeRegistries,
        Integer totalEvaluationPeriods
){

}
