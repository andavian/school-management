package org.school.management.students.personal.application.dto;

import jakarta.validation.constraints.Pattern;

import java.util.UUID;

/**
 * Request DTO: Buscar estudiante
 */
public record SearchStudentRequest(

        @Pattern(regexp = "^\\d{7,8}$", message = "DNI must be 7 or 8 digits")
        String dni,

        String fullName,

        UUID residencePlaceId

) {
    /**
     * Valida que al menos un criterio de búsqueda esté presente
     */
    public SearchStudentRequest {
        if ((dni == null || dni.isBlank()) &&
                (fullName == null || fullName.isBlank()) &&
                residencePlaceId == null) {
            throw new IllegalArgumentException(
                    "At least one search criteria must be provided"
            );
        }
    }
}
