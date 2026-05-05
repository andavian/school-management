package org.school.management.academic.domain.exception;

import org.school.management.shared.domain.exception.DomainException;

public class RegistryNotClosedException extends DomainException {
    public RegistryNotClosedException(String message) {
        super(message);
    }
}