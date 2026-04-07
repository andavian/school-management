package org.school.management.resources.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.school.management.resources.domain.valueobject.ReservationStatus;
import org.school.management.shared.infrastructure.persistence.converter.UuidBinaryConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor
@Entity @Table(name = "reservations")
public class ReservationEntity {

    @Id
    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "reservation_id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID reservationId;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "resource_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID resourceId;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "requester_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID requesterId;

    @Column(name = "requester_name", length = 200, nullable = false)
    private String requesterName;

    @Column(name = "reservation_date", nullable = false)
    private LocalDate reservationDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "quantity_requested", nullable = false)
    private int quantityRequested;

    @Column(name = "purpose", length = 500, nullable = false)
    private String purpose;

    @Column(name = "grade_level_info", length = 100)
    private String gradeLevelInfo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private ReservationStatus status = ReservationStatus.CONFIRMED;

    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "cancelled_by", columnDefinition = "BINARY(16)")
    private UUID cancelledBy;

    @Column(name = "return_observations", columnDefinition = "TEXT")
    private String returnObservations;

    @Column(name = "returned_at")
    private LocalDateTime returnedAt;

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