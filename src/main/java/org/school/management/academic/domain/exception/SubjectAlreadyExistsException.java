package org.school.management.academic.domain.exception;

public class SubjectAlreadyExistsException extends AcademicDomainException {
    public SubjectAlreadyExistsException(String message) {
        super(message);
    }
}
