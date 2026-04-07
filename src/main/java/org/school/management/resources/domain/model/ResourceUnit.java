package org.school.management.resources.domain.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.school.management.resources.domain.valueobject.ConditionStatus;
import org.school.management.resources.domain.valueobject.ResourceId;
import org.school.management.resources.domain.valueobject.UnitId;
import org.school.management.resources.domain.valueobject.UnitStatus;

import java.time.LocalDateTime;

@Getter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ResourceUnit {

    @EqualsAndHashCode.Include
    private final UnitId unitId;
    private final ResourceId resourceId;
    private String unitCode;
    private String serialNumber;
    private ConditionStatus conditionStatus;
    private UnitStatus unitStatus;
    private String notes;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ResourceUnit create(UnitId unitId, ResourceId resourceId, String unitCode,
                                      String serialNumber, ConditionStatus conditionStatus) {
        if (resourceId == null) throw new IllegalArgumentException("ResourceId is required");
        if (unitCode == null || unitCode.isBlank()) throw new IllegalArgumentException("UnitCode is required");

        return ResourceUnit.builder()
                .unitId(unitId)
                .resourceId(resourceId)
                .unitCode(unitCode.toUpperCase().trim())
                .serialNumber(serialNumber)
                .conditionStatus(conditionStatus != null ? conditionStatus : ConditionStatus.GOOD)
                .unitStatus(UnitStatus.AVAILABLE)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // ─── State Transitions ─────────────────────────────────────────────
    public void assignToReservation() {
        assertStatusIs(UnitStatus.AVAILABLE, "assign");
        this.unitStatus = UnitStatus.IN_USE;
        touch();
    }

    public void returnFromReservation() {
        assertStatusIs(UnitStatus.IN_USE, "return");
        this.unitStatus = UnitStatus.AVAILABLE;
        touch();
    }

    public void markForMaintenance() {
        assertStatusIs(UnitStatus.AVAILABLE, "start maintenance");
        this.unitStatus = UnitStatus.MAINTENANCE;
        touch();
    }

    public void completeMaintenance() {
        assertStatusIs(UnitStatus.MAINTENANCE, "complete maintenance");
        this.unitStatus = UnitStatus.AVAILABLE;
        touch();
    }

    public void markAsOnLoan() {
        assertStatusIs(UnitStatus.AVAILABLE, "start loan");
        this.unitStatus = UnitStatus.ON_LOAN;
        touch();
    }

    public void returnFromLoan() {
        assertStatusIs(UnitStatus.ON_LOAN, "end loan");
        this.unitStatus = UnitStatus.AVAILABLE;
        touch();
    }

    public void retire() {
        if (this.unitStatus == UnitStatus.RETIRED) return;
        this.unitStatus = UnitStatus.RETIRED;
        this.active = false;
        touch();
    }

    public void updateCondition(ConditionStatus condition) {
        if (condition == null) return;
        this.conditionStatus = condition;
        touch();
    }

    public void updateNotes(String notes) {
        this.notes = notes;
        touch();
    }

    // ─── Helpers ───────────────────────────────────────────────────────
    private void assertStatusIs(UnitStatus expected, String action) {
        if (this.unitStatus != expected) {
            throw new IllegalStateException(
                    String.format("Cannot %s unit %s: current status is %s, expected %s",
                            action, this.unitCode, this.unitStatus, expected));
        }
    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isAvailableForReservation() {
        return this.unitStatus == UnitStatus.AVAILABLE;
    }
}