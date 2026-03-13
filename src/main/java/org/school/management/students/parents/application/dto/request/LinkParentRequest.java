package org.school.management.students.parents.application.dto.request;

import jakarta.validation.constraints.*;

/**
 * Request para vincular un padre existente a un estudiante.
 * Se usa cuando el padre ya existe en el sistema (ya tiene User).
 */
public record LinkParentRequest(

        @NotBlank(message = "El DNI del padre es obligatorio")
        @Pattern(regexp = "^\\d{8}$", message = "El DNI debe tener exactamente 8 dígitos")
        String parentDni,

        @NotBlank(message = "La relación es obligatoria")
        @Pattern(
                regexp = "FATHER|MOTHER|GUARDIAN|GRANDPARENT|SIBLING|OTHER",
                message = "Relación inválida"
        )
        String relationship,

        Boolean isPrimaryContact,
        Boolean isAuthorizedPickup,
        Boolean isEmergencyContact,

        @Size(max = 500)
        String notes
) {}