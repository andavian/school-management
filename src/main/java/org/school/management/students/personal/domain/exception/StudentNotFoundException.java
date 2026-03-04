package org.school.management.students.personal.domain.exception;

import org.school.management.shared.domain.exception.DomainException;

/**
 * Excepción: El estudiante no fue encontrado
 */
public class StudentNotFoundException extends DomainException {

    public StudentNotFoundException(String message) {
        super(message);
    }

    public StudentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
