package org.school.management.grades.domain.exception;

import org.school.management.shared.domain.exception.DomainException;

import java.util.UUID;

public class GradeNotFoundException extends DomainException {

    public GradeNotFoundException(String message) {
        super(message);
    }

    public static GradeNotFoundException evaluation(UUID evaluationId) {
        return new GradeNotFoundException("Evaluation not found with id: " + evaluationId);
    }

    public static GradeNotFoundException periodGrade(UUID periodGradeId) {
        return new GradeNotFoundException("Period grade not found with id: " + periodGradeId);
    }

    public static GradeNotFoundException periodGradeForSubject(UUID studentCourseSubjectId, UUID periodId) {
        return new GradeNotFoundException(
                "Period grade not found for studentCourseSubject: " + studentCourseSubjectId +
                        " and period: " + periodId
        );
    }

    public static GradeNotFoundException finalGrade(UUID finalGradeId) {
        return new GradeNotFoundException("Final grade not found with id: " + finalGradeId);
    }

    public static GradeNotFoundException finalGradeForSubject(UUID studentCourseSubjectId, UUID academicYearId) {
        return new GradeNotFoundException(
                "Final grade not found for studentCourseSubject: " + studentCourseSubjectId +
                        " and academicYear: " + academicYearId
        );
    }
}