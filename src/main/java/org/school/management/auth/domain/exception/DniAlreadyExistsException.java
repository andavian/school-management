package org.school.management.auth.domain.exception;

public static class DniAlreadyExistsException extends RuntimeException {
    public DniAlreadyExistsException(String message) {
        super(message);
    }
}