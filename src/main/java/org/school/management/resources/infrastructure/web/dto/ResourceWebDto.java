// src/main/java/org/school/management/resources/infrastructure/web/dto/ResourceWebDto.java
package org.school.management.resources.infrastructure.web.dto;

import jakarta.validation.constraints.*;
import org.school.management.resources.domain.valueobject.ConditionStatus;
import org.school.management.resources.domain.valueobject.ResourceType;
import org.school.management.resources.domain.valueobject.UnitStatus;

import java.util.List;
import java.util.UUID;

/**
 * Contenedor Web DTO para el BC resources/.
 * Sigue el estándar del proyecto.
 */
public final class ResourceWebDto {
    private ResourceWebDto() {}

    // ─── REQUESTS ────────────────────────────────────────────────────────

    public record CreateResourceWebRequest(
            @NotBlank(message = "El código es obligatorio")
            @Size(max = 30, message = "El código no puede superar los 30 caracteres")
            String code,

            @NotBlank(message = "El nombre es obligatorio")
            @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
            String name,

            @NotNull(message = "El tipo de recurso es obligatorio")
            ResourceType resourceType,

            @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
            String description,

            @Size(max = 200, message = "La ubicación no puede superar los 200 caracteres")
            String location,

            boolean reservable,

            @Size(max = 500, message = "Las notas no pueden superar los 500 caracteres")
            String notes
    ) {}

    public record UpdateResourceWebRequest(
            @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
            String name,

            @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
            String description,

            @Size(max = 200, message = "La ubicación no puede superar los 200 caracteres")
            String location,

            Boolean reservable,

            @Size(max = 500, message = "Las notas no pueden superar los 500 caracteres")
            String notes
    ) {
        public boolean hasUpdates() {
            return name != null || description != null || location != null ||
                    reservable != null || notes != null;
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

    // ─── RESPONSES ───────────────────────────────────────────────────────

    public record ResourceWebResponse(
            UUID resourceId,
            String name,
            String code,
            ResourceType resourceType,
            String description,
            String location,
            boolean reservable,
            String notes,
            boolean isActive,
            List<ResourceUnitWebResponse> units
    ) {}

    public record ResourceUnitWebResponse(
            UUID unitId,
            String unitCode,
            String serialNumber,
            ConditionStatus conditionStatus,
            UnitStatus unitStatus,
            String notes
    ) {}
}