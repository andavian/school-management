package org.school.management.students.personal.domain.exception;

import org.school.management.shared.domain.exception.DomainException;

public class StudentAlreadyExistsException extends DomainException {

    public StudentAlreadyExistsException(String message) {
        super(message);
    }

    public StudentAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public static StudentAlreadyExistsException withDni(String dni) {
        return new StudentAlreadyExistsException("Student already exists with DNI: " + dni);
    }

    public static StudentAlreadyExistsException withCuil(String cuil) {
        return new StudentAlreadyExistsException("Student already exists with CUIL: " + cuil);
    }
}