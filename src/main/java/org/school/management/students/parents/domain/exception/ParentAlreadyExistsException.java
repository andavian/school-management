package org.school.management.students.parents.domain.exception;

import org.school.management.shared.domain.exception.DomainException;

public class ParentAlreadyExistsException extends DomainException {

    public ParentAlreadyExistsException(String message) {
        super(message);
    }

    public static ParentAlreadyExistsException withDni(String dni) {
        return new ParentAlreadyExistsException(
                "Parent already exists with DNI: " + dni
        );
    }

    public static ParentAlreadyExistsException withEmail(String email) {
        return new ParentAlreadyExistsException(
                "Parent already exists with email: " + email
        );
    }

    public static ParentAlreadyExistsException withCuil(String cuil) {
        return new ParentAlreadyExistsException(
                "A parent already exists with CUIL: " + cuil
        );
    }
}