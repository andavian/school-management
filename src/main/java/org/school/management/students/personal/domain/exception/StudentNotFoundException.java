package org.school.management.students.personal.domain.exception;

import org.school.management.shared.domain.exception.DomainException;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;

import java.util.UUID;

public class StudentNotFoundException extends DomainException {

    public StudentNotFoundException(String message) {
        super(message);
    }

    public StudentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public static StudentNotFoundException byId(StudentPersonalDataId id) {
        return new StudentNotFoundException("Student not found with id: " + id.value());
    }

    public static StudentNotFoundException byId(UUID id) {
        return new StudentNotFoundException("Student not found with id: " + id);
    }

    public static StudentNotFoundException byDni(String dni) {
        return new StudentNotFoundException("Student not found with DNI: " + dni);
    }
}