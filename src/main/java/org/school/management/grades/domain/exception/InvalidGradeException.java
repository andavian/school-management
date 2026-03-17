package org.school.management.grades.domain.exception;

import org.school.management.shared.domain.exception.DomainException;

import java.util.UUID;

public class InvalidGradeException extends DomainException {

    public InvalidGradeException(String message) {
        super(message);
    }

    public static InvalidGradeException withReason(String reason) {
        return new InvalidGradeException("Invalid grade operation: " + reason);
    }

    public static InvalidGradeException gradeOutOfRange(UUID evaluationId) {
        return new InvalidGradeException(
                "Grade value is out of allowed range for evaluation: " + evaluationId
        );
    }

    public static InvalidGradeException missingGradeForValidation(UUID entityId) {
        return new InvalidGradeException(
                "Cannot validate entity without a grade: " + entityId
        );
    }

    public static InvalidGradeException notInPendingExamStatus(UUID studentCourseSubjectId) {
        return new InvalidGradeException(
                "Student course subject is not in PENDING_EXAM status: " + studentCourseSubjectId
        );
    }
}