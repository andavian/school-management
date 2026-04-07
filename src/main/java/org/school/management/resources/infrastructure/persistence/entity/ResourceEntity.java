package org.school.management.resources.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.school.management.resources.domain.valueobject.ResourceType;
import org.school.management.shared.infrastructure.persistence.converter.UuidBinaryConverter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor
@Entity @Table(name = "resources")
public class ResourceEntity {

    @Id
    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "resource_id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID resourceId;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "code", length = 30, nullable = false, unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type", length = 30, nullable = false)
    private ResourceType resourceType;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "location", length = 200)
    private String location;

    @Column(name = "is_reservable", nullable = false)
    private boolean isReservable = true;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "created_by", columnDefinition = "BINARY(16)", nullable = false)
    @Convert(converter = UuidBinaryConverter.class)
    private UUID createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onPrePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
    }

    @PreUpdate
    protected void onPreUpdate() {
        updatedAt = LocalDateTime.now();
    }
}