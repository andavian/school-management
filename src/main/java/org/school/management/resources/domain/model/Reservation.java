package org.school.management.resources.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.resources.domain.valueobject.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Reservation {

    @EqualsAndHashCode.Include
    private final ReservationId reservationId;
    private final ResourceId resourceId;
    private final UserId requesterId;
    private final String requesterName;

    private final LocalDate reservationDate;
    private final LocalTime startTime;
    private final LocalTime endTime;

    private final int quantityRequested;
    private final String purpose;
    private final String gradeLevelInfo;

    private ReservationStatus status;
    private String cancellationReason;
    private UserId cancelledBy;
    private String returnObservations;
    private LocalDateTime returnedAt;

    @Builder.Default
    private final List<ReservationUnit> assignedUnits = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Reservation create(ResourceId resourceId, UserId requesterId, String requesterName,
                                     LocalDate reservationDate, LocalTime startTime, LocalTime endTime,
                                     int quantityRequested, String purpose, String gradeLevelInfo) {
        if (resourceId == null) throw new IllegalArgumentException("ResourceId is required");
        if (requesterId == null) throw new IllegalArgumentException("RequesterId is required");
        if (requesterName == null || requesterName.isBlank()) throw new IllegalArgumentException("RequesterName is required");
        if (reservationDate == null) throw new IllegalArgumentException("ReservationDate is required");
        if (startTime == null || endTime == null) throw new IllegalArgumentException("Time range is required");
        if (!endTime.isAfter(startTime)) throw new IllegalArgumentException("End time must be after start time");
        if (quantityRequested < 1) throw new IllegalArgumentException("Quantity must be at least 1");
        if (purpose == null || purpose.isBlank()) throw new IllegalArgumentException("Purpose is required");

        LocalDateTime now = LocalDateTime.now();
        return Reservation.builder()
                .reservationId(ReservationId.generate())
                .resourceId(resourceId)
                .requesterId(requesterId)
                .requesterName(requesterName.trim())
                .reservationDate(reservationDate)
                .startTime(startTime)
                .endTime(endTime)
                .quantityRequested(quantityRequested)
                .purpose(purpose.trim())
                .gradeLevelInfo(gradeLevelInfo != null ? gradeLevelInfo.trim() : null)
                .status(ReservationStatus.CONFIRMED)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    // ─── Lifecycle Transitions ─────────────────────────────────────────────
    public void markAsInUse() {
        assertStatusIs(ReservationStatus.CONFIRMED, "mark as IN_USE");
        this.status = ReservationStatus.IN_USE;
        touch();
    }

    public void markAsReturned(String observations) {
        assertStatusIs(ReservationStatus.IN_USE, "mark as RETURNED");
        this.status = ReservationStatus.RETURNED;
        this.returnObservations = observations;
        this.returnedAt = LocalDateTime.now();
        touch();
    }

    public void cancel(UserId cancelledBy, String reason) {
        if (!getStatus().isCancelable()) {
            throw new IllegalStateException(
                    String.format("Cannot cancel reservation %s: current status is %s",
                            this.reservationId, this.status));
        }
        this.status = ReservationStatus.CANCELLED;
        this.cancellationReason = reason;
        this.cancelledBy = cancelledBy;
        touch();
    }

    // ─── Unit Assignment (called by ReservationDomainService) ──────────────
    public void assignUnit(ResourceUnit unit) {
        if (this.status != ReservationStatus.CONFIRMED && this.status != ReservationStatus.IN_USE) {
            throw new IllegalStateException("Cannot assign units to reservation in status: " + this.status);
        }
        if (!unit.getResourceId().equals(this.resourceId)) {
            throw new IllegalArgumentException("Unit does not belong to the same resource type");
        }
        if (this.assignedUnits.size() >= this.quantityRequested) {
            throw new IllegalStateException("Maximum quantity requested already assigned");
        }
        // Crear la relación ReservationUnit
        ReservationUnit ru = ReservationUnit.create(ReservationUnitId.generate(), this.reservationId, unit.getUnitId());
        this.assignedUnits.add(ru);
        touch();
    }

    public boolean isFullyAssigned() {
        return this.assignedUnits.size() >= this.quantityRequested;
    }

    public boolean overlapsWith(LocalDate otherDate, LocalTime otherStart, LocalTime otherEnd) {
        if (!this.reservationDate.isEqual(otherDate)) return false;
        return !this.endTime.isBefore(otherStart) && !this.startTime.isAfter(otherEnd);
    }

    // ─── Helpers ───────────────────────────────────────────────────────
    private void assertStatusIs(ReservationStatus expected, String action) {
        if (this.status != expected) {
            throw new IllegalStateException(
                    String.format("Cannot %s reservation %s: current status is %s, expected %s",
                            action, this.reservationId, this.status, expected));
        }
    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return this.status == ReservationStatus.CONFIRMED || this.status == ReservationStatus.IN_USE;
    }
}