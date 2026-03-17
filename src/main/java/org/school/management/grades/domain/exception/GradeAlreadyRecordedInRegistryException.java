package org.school.management.grades.domain.exception;

import org.school.management.shared.domain.exception.DomainException;

import java.util.UUID;

public class GradeAlreadyRecordedInRegistryException extends DomainException {

    public GradeAlreadyRecordedInRegistryException(String message) {
        super(message);
    }

    public static GradeAlreadyRecordedInRegistryException forFinalGrade(UUID finalGradeId) {
        return new GradeAlreadyRecordedInRegistryException(
                "Final grade is already recorded in registry: " + finalGradeId
        );
    }
}
