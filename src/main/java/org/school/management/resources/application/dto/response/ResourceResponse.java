package org.school.management.resources.application.dto.response;

import org.school.management.resources.domain.valueobject.ResourceType;
import java.util.UUID;

public record ResourceResponse(
        UUID resourceId,
        String name,
        String code,
        ResourceType resourceType,
        String description,
        String location,
        boolean reservable,
        String notes,
        boolean active
) {}