package org.school.management.resources.application.dto.request;

import jakarta.validation.constraints.Size;
import org.school.management.resources.domain.valueobject.ConditionStatus;
import org.school.management.resources.domain.valueobject.UnitStatus;

public record UpdateUnitStatusRequest(
        UnitStatus unitStatus,
        ConditionStatus conditionStatus,
        @Size(max = 500, message = "Las notas no pueden superar los 500 caracteres") String notes
) {
    public boolean hasUpdates() {
        return unitStatus != null || conditionStatus != null || notes != null;
    }
}