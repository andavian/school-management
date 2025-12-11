package org.school.management.academic.domain.exception;

public class GradeLevelAlreadyExistsException extends AcademicDomainException {
    public GradeLevelAlreadyExistsException(String message) {
        super(message);
    }
}
