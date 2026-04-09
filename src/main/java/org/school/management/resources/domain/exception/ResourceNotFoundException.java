// src/main/java/org/school/management/resources/domain/exception/ResourceNotFoundException.java
package org.school.management.resources.domain.exception;

import org.school.management.shared.domain.exception.DomainException;
import org.school.management.resources.domain.valueobject.ResourceId;

import java.util.UUID;

public class ResourceNotFoundException extends DomainException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException byId(ResourceId id) {
        return new ResourceNotFoundException("Resource not found with id: " + id.value());
    }

    public static ResourceNotFoundException byId(UUID id) {
        return new ResourceNotFoundException("Resource not found with id: " + id);
    }

    public static ResourceNotFoundException byCode(String code) {
        return new ResourceNotFoundException("Resource not found with code: " + code);
    }
}