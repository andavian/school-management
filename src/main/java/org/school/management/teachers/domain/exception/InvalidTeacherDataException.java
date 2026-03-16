package org.school.management.teachers.domain.exception;

import org.school.management.shared.domain.exception.DomainException;

public class InvalidTeacherDataException extends DomainException {

    public InvalidTeacherDataException(String message) {
        super(message);
    }

    public static InvalidTeacherDataException withReason(String reason) {
        return new InvalidTeacherDataException("Invalid teacher data: " + reason);
    }
}
