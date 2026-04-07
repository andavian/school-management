package org.school.management.resources.application.dto.request;

import jakarta.validation.constraints.*;
import org.school.management.resources.domain.valueobject.ConditionStatus;
import java.util.UUID;

public record CreateResourceUnitRequest(
        @NotNull(message = "El ID del recurso es obligatorio") UUID resourceId,
        @NotBlank(message = "El código de unidad es obligatorio") @Size(max = 50) String unitCode,
        @Size(max = 100, message = "El número de serie no puede superar los 100 caracteres") String serialNumber,
        ConditionStatus conditionStatus
) {}