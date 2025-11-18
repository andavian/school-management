package org.school.management.auth.domain.exception;

public class DniAlreadyExistsException extends RuntimeException {
    public DniAlreadyExistsException(String message) {
        super(message);
    }
}