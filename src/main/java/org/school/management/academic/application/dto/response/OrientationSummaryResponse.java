package org.school.management.academic.application.dto.response;

public record OrientationSummaryResponse (
        OrientationResponse orientation,
        Integer totalGradeLevels,
        Integer totalSubjects,
        Integer totalStudyPlans
){

}
