package org.school.management.resources.application.dto.response;
import org.school.management.resources.domain.valueobject.ConditionStatus;
import org.school.management.resources.domain.valueobject.UnitStatus;
import java.util.UUID;

public record ResourceUnitResponse(
        UUID unitId, UUID resourceId, String unitCode, String serialNumber,
        ConditionStatus conditionStatus, UnitStatus unitStatus, String notes
) {}