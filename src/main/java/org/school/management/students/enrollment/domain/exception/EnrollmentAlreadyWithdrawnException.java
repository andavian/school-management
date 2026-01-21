package org.school.management.students.enrollment.domain.exception;

import org.school.management.shared.domain.exception.DomainException;

public class EnrollmentAlreadyWithdrawnException extends DomainException {
    public EnrollmentAlreadyWithdrawnException(String message) {
        super(message);
    }
}