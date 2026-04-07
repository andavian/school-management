// ResourceResponse.java
package org.school.management.resources.application.dto.response;

import org.school.management.resources.domain.valueobject.ResourceId;
import org.school.management.resources.domain.valueobject.ResourceStatus;
import org.school.management.resources.domain.valueobject.ResourceType;

import java.time.LocalDateTime;

public record ResourceResponse(
        ResourceId resourceId,
        String name,
        String description,
        ResourceType resourceType,
        ResourceStatus status,
        String location,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}




