// src/main/java/org/school/management/resources/application/dto/request/CreateResourceRequest.java
package org.school.management.resources.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.school.management.resources.domain.valueobject.ResourceType;

public record CreateResourceRequest(
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