package org.school.management.academic.domain.exception;

public class RegistryFullException extends RuntimeException {
    public RegistryFullException(String registryNumber) {
        super("Qualification registry is full: " + registryNumber);
    }
}
