package org.school.management.resources.application.dto.request;

import org.school.management.resources.domain.valueobject.ResourceType;

// UpdateResourceRequest.java
public record UpdateResourceRequest(
        String name,
        String description,
        ResourceType resourceType,
        String location
) {
}
