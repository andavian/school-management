package org.school.management.students.parents.domain.exception;

import org.school.management.shared.domain.exception.DomainException;

import java.util.UUID;

public class ParentNotFoundException extends DomainException {

    public ParentNotFoundException(String message) {
        super(message);
    }

    public static ParentNotFoundException byId(UUID parentId) {
        return new ParentNotFoundException(
                "Parent not found with id: " + parentId
        );
    }

    public static ParentNotFoundException byDni(String dni) {
        return new ParentNotFoundException(
                "Parent not found with DNI: " + dni
        );
    }

    public static ParentNotFoundException byUserId(UUID userId) {
        return new ParentNotFoundException(
                "Parent not found with userId: " + userId
        );
    }
}