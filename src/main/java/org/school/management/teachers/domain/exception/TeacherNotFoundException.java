package org.school.management.teachers.domain.exception;

import org.school.management.shared.domain.exception.DomainException;

import java.util.UUID;

public class TeacherNotFoundException extends DomainException {

    public TeacherNotFoundException(String message) {
        super(message);
    }

    public static TeacherNotFoundException byId(UUID id) {
        return new TeacherNotFoundException("Teacher not found with id: " + id);
    }

    public static TeacherNotFoundException byDni(String dni) {
        return new TeacherNotFoundException("Teacher not found with DNI: " + dni);
    }
}