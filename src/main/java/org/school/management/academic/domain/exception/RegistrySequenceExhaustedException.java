package org.school.management.academic.domain.exception;

import org.school.management.shared.domain.exception.DomainException;

public class RegistrySequenceExhaustedException extends DomainException {
    public RegistrySequenceExhaustedException(String message) {
        super(message);
    }
}