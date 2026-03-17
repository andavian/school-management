package org.school.management.grades.domain.exception;

import org.school.management.shared.domain.exception.DomainException;

import java.util.UUID;

public class GradeAlreadyValidatedException extends DomainException {

    public GradeAlreadyValidatedException(String message) {
        super(message);
    }

    public static GradeAlreadyValidatedException evaluation(UUID evaluationId) {
        return new GradeAlreadyValidatedException(
                "Evaluation is already validated: " + evaluationId
        );
    }

    public static GradeAlreadyValidatedException periodGrade(UUID periodGradeId) {
        return new GradeAlreadyValidatedException(
                "Period grade is already validated: " + periodGradeId
        );
    }

    public static GradeAlreadyValidatedException finalGrade(UUID finalGradeId) {
        return new GradeAlreadyValidatedException(
                "Final grade is already validated: " + finalGradeId
        );
    }
}