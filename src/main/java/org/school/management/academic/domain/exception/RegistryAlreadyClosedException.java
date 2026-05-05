package org.school.management.academic.domain.exception;

import org.school.management.shared.domain.exception.DomainException;

public class RegistryAlreadyClosedException extends DomainException {
    public RegistryAlreadyClosedException(String registryId) {
        super("Registry is already closed: " + registryId);
    }
}