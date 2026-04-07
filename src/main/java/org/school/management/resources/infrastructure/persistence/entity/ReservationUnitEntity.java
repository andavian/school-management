package org.school.management.resources.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.school.management.shared.infrastructure.persistence.converter.UuidBinaryConverter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor
@Entity @Table(name = "reservation_units")
public class ReservationUnitEntity {

    @Id
    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "reservation_unit_id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID reservationUnitId;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "reservation_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID reservationId;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "unit_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID unitId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onPrePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}