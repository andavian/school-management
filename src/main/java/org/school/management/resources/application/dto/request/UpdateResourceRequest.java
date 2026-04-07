package org.school.management.resources.application.dto.request;

import jakarta.validation.constraints.*;

public record UpdateResourceRequest(
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
        return name != null || description != null || location != null || reservable != null || notes != null;
    }
}