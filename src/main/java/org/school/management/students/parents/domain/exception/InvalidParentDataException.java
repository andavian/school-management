package org.school.management.students.parents.domain.exception;

import org.school.management.shared.domain.exception.DomainException;

public class InvalidParentDataException extends DomainException {

    public InvalidParentDataException(String message) {
        super(message);
    }
}