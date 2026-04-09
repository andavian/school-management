// src/main/java/org/school/management/resources/domain/model/Resource.java
package org.school.management.resources.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.school.management.resources.domain.valueobject.ResourceId;
import org.school.management.resources.domain.valueobject.ResourceType;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Resource {

    @EqualsAndHashCode.Include
    private final ResourceId resourceId;

    private String name;
    private String code;
    private ResourceType resourceType;
    private String description;
    private String location;
    private boolean reservable;
    private String notes;
    private boolean active;
    private final UUID createdBy;           // ← Agregado (obligatorio en BD)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Resource create(ResourceId resourceId, String name, String code,
                                  ResourceType resourceType, String description,
                                  String location, boolean reservable, String notes,
                                  UUID createdBy) {

        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name is required");
        if (code == null || code.isBlank())
            throw new IllegalArgumentException("Code is required");
        if (resourceType == null)
            throw new IllegalArgumentException("ResourceType is required");
        if (createdBy == null)
            throw new IllegalArgumentException("CreatedBy is required");

        LocalDateTime now = LocalDateTime.now();

        return Resource.builder()
                .resourceId(resourceId)
                .name(name.trim())
                .code(code.toUpperCase().trim())
                .resourceType(resourceType)
                .description(description != null ? description.trim() : null)
                .location(location != null ? location.trim() : null)
                .reservable(reservable)
                .notes(notes != null ? notes.trim() : null)
                .active(true)
                .createdBy(createdBy)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public void updateMetadata(String name, String description, String location,
                               boolean reservable, String notes) {
        if (name != null && !name.isBlank()) this.name = name.trim();
        if (description != null) this.description = description.trim();
        if (location != null) this.location = location.trim();
        this.reservable = reservable;
        if (notes != null) this.notes = notes.trim();
        touch();
    }

    public void deactivate() {
        this.active = false;
        touch();
    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }
}