package org.school.management.resources.domain.exception;

import org.school.management.shared.domain.exception.DomainException;

// ResourceNotAvailableException.java
public class ResourceNotAvailableException extends DomainException {
    public ResourceNotAvailableException(String message) {
        super(message);
    }

    public static ResourceNotAvailableException becauseStatus(String status) {
        return new ResourceNotAvailableException("Resource is not available (status: " + status + ")");
    }
}
