package org.school.management.academic.application.dto.response;

public record GradeLevelSummaryResponse (
        GradeLevelResponse gradeLevel,
        Integer enrolledStudents,
        Integer availableSpots,
        boolean isFull
){

}
