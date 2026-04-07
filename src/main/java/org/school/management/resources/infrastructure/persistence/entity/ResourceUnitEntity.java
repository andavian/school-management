package org.school.management.resources.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.school.management.resources.domain.valueobject.ConditionStatus;
import org.school.management.resources.domain.valueobject.UnitStatus;
import org.school.management.shared.infrastructure.persistence.converter.UuidBinaryConverter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor
@Entity @Table(name = "resource_units")
public class ResourceUnitEntity {

    @Id
    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "unit_id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID unitId;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "resource_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID resourceId;

    @Column(name = "unit_code", length = 50, nullable = false, unique = true)
    private String unitCode;

    @Column(name = "serial_number", length = 100)
    private String serialNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition_status", length = 20, nullable = false)
    private ConditionStatus conditionStatus = ConditionStatus.GOOD;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit_status", length = 20, nullable = false)
    private UnitStatus unitStatus = UnitStatus.AVAILABLE;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

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