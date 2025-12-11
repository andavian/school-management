package org.school.management.academic.domain.exception;

import org.school.management.academic.domain.valueobject.ids.RegistryId;

public class RegistryNotFoundException extends RuntimeException {
    public RegistryNotFoundException(RegistryId id) {
        super("Qualification registry not found: " + id);
    }

    public RegistryNotFoundException(String registryNumber) {
        super("Qualification registry not found: " + registryNumber);
    }
}
