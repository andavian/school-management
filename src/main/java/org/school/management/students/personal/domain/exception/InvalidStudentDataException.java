package org.school.management.students.personal.domain.exception;

import org.school.management.shared.domain.exception.DomainException;

/**
 * Excepción: Datos inválidos del estudiante
 */
public class InvalidStudentDataException extends DomainException {

    public InvalidStudentDataException(String message) {
        super(message);
    }

    public InvalidStudentDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
