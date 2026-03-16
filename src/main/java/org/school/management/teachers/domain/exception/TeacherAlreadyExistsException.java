package org.school.management.teachers.domain.exception;

import org.school.management.shared.domain.exception.DomainException;

public class TeacherAlreadyExistsException extends DomainException {

    public TeacherAlreadyExistsException(String message) {
        super(message);
    }

    public static TeacherAlreadyExistsException withDni(String dni) {
        return new TeacherAlreadyExistsException("A teacher already exists with DNI: " + dni);
    }

    public static TeacherAlreadyExistsException withCuil(String cuil) {
        return new TeacherAlreadyExistsException("A teacher already exists with CUIL: " + cuil);
    }
}