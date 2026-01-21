package org.school.management.students.enrollment.domain.exception;

import org.school.management.shared.domain.exception.DomainException;

public class InvalidEnrollmentCompletionException extends DomainException {
    public InvalidEnrollmentCompletionException(String message) {
        super(message);
    }
}