package org.school.management.teachingmaterials.application.dto.request;

import jakarta.validation.constraints.Size;
import org.school.management.teachingmaterials.domain.valueobject.MaterialType;

/**
 * PATCH semántico — todos los campos son opcionales.
 * null = conservar valor existente.
 * No permite cambiar el archivo — solo metadata y visibilidad.
 */
public record UpdateMaterialRequest(

        @Size(max = 200, message = "Title cannot exceed 200 characters")
        String title,

        @Size(max = 1000, message = "Description cannot exceed 1000 characters")
        String description,

        MaterialType materialType,

        Boolean visibleToStudents
) {}