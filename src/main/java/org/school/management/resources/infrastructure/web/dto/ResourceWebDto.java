package org.school.management.resources.infrastructure.web.dto;

import jakarta.validation.constraints.*;
import org.school.management.resources.domain.valueobject.ConditionStatus;
import org.school.management.resources.domain.valueobject.ResourceType;
import org.school.management.resources.domain.valueobject.UnitStatus;

import java.util.List;
import java.util.UUID;

/**
 * Contenedor Web DTO para el BC resources/.
 * Sigue el patrón del proyecto: clase final con records internos para request/response.
 */
public final class ResourceWebDto {
    private ResourceWebDto() {}

    public record CreateResourceWebRequest(
            @NotBlank @Size(max = 100) String name,
            @NotBlank @Size(max = 30) String code,
            @NotNull ResourceType resourceType,
            @Size(max = 500) String description,
            @Size(max = 200) String location,
            boolean reservable,
            @Size(max = 500) String notes
    ) {}

    public record UpdateResourceWebRequest(
            @Size(min = 3, max = 100) String name,
            @Size(max = 500) String description,
            @Size(max = 200) String location,
            Boolean reservable,
            @Size(max = 500) String notes
    ) {
        public boolean hasUpdates() {
            return name != null || description != null || location != null || reservable != null || notes != null;
        }
    }

    public record CreateResourceUnitWebRequest(
            @NotBlank @Size(max = 50) String unitCode,
            @Size(max = 100) String serialNumber,
            ConditionStatus conditionStatus
    ) {}

    public record UpdateUnitStatusWebRequest(
            UnitStatus unitStatus,
            ConditionStatus conditionStatus,
            @Size(max = 500) String notes
    ) {}

    public record ResourceWebResponse(
            UUID resourceId, String name, String code, ResourceType resourceType,
            String description, String location, boolean reservable, String notes,
            boolean isActive, List<ResourceUnitWebResponse> units
    ) {}

    public record ResourceUnitWebResponse(
            UUID unitId, String unitCode, String serialNumber,
            ConditionStatus conditionStatus, UnitStatus unitStatus, String notes
    ) {}
}