package org.school.management.students.enrollment.domain.exception;

import org.school.management.shared.domain.exception.DomainException;

public class InvalidEnrollmentException extends DomainException {
    public InvalidEnrollmentException(String message) {
        super(message);
    }
}