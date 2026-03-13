package org.school.management.students.parents.application.dto.response;

import org.school.management.students.parents.domain.valueobject.ParentRelationship;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO para StudentParent.
 * Incluye los datos del vínculo + datos del padre embebidos.
 */
public record StudentParentResponse(

        UUID studentParentId,
        UUID studentId,
        UUID parentId,

        // Tipo de relación
        ParentRelationship relationship,
        String relationshipDisplay,

        // Flags operativos
        boolean primaryContact,
        boolean authorizedPickup,
        boolean emergencyContact,

        String notes,

        // Datos del padre embebidos — evita un segundo request
        ParentResponse parent,

        // Auditoría
        LocalDateTime createdAt
) {}