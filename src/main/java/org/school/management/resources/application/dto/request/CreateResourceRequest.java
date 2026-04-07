// CreateResourceRequest.java
package org.school.management.resources.application.dto.request;

import org.school.management.resources.domain.valueobject.ResourceType;

public record CreateResourceRequest(
        String name,
        String description,
        ResourceType resourceType,
        String location
) {}


