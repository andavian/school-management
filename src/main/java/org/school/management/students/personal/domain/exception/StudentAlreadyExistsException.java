package org.school.management.students.personal.domain.exception;

import org.school.management.shared.domain.exception.DomainException;

/**
 * Excepción: El estudiante ya existe (DNI duplicado)
 */
public class StudentAlreadyExistsException extends DomainException {

    public StudentAlreadyExistsException(String message) {
        super(message);
    }

    public StudentAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}

