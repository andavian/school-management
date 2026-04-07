package org.school.management.resources.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.school.management.resources.domain.valueobject.ResourceId;
import org.school.management.resources.domain.valueobject.ResourceType;

import java.time.LocalDateTime;

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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Resource create(ResourceId resourceId, String name, String code, ResourceType resourceType,
                                  String description, String location, boolean reservable, String notes) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name is required");
        if (code == null || code.isBlank()) throw new IllegalArgumentException("Code is required");
        if (resourceType == null) throw new IllegalArgumentException("ResourceType is required");

        LocalDateTime now = LocalDateTime.now();
        return Resource.builder()
                .resourceId(resourceId)
                .name(name)
                .code(code.toUpperCase().trim())
                .resourceType(resourceType)
                .description(description)
                .location(location)
                .reservable(reservable)
                .notes(notes)
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public void updateMetadata(String name, String description, String location, boolean reservable, String notes) {
        if (name != null && !name.isBlank()) this.name = name;
        if (description != null) this.description = description;
        if (location != null) this.location = location;
        this.reservable = reservable;
        if (notes != null) this.notes = notes;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }
}