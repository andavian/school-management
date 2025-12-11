package org.school.management.academic.domain.exception;

public abstract class AcademicDomainException extends RuntimeException {
    protected AcademicDomainException(String message) {
        super(message);
    }

    protected AcademicDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
