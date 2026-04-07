// ResourceNotFoundException.java
package org.school.management.resources.domain.exception;

import org.school.management.shared.domain.exception.DomainException;
import java.util.UUID;

public class ResourceNotFoundException extends DomainException {
    public ResourceNotFoundException(String message) { super(message); }
    public static ResourceNotFoundException byId(UUID id) {
        return new ResourceNotFoundException("Resource not found with id: " + id);
    }
}

