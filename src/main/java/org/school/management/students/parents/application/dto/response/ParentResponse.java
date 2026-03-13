package org.school.management.students.parents.application.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO para Parent.
 * Incluye datos personales, contacto e información laboral.
 */
public record ParentResponse(

        UUID parentId,
        UUID userId,

        // Datos personales
        String dni,
        String firstName,
        String lastName,
        String fullName,
        LocalDate birthDate,
        String gender,
        String nationality,

        // Contacto
        String email,
        String phone,
        String phoneAlt,

        // Domicilio
        String addressStreet,
        String addressNumber,
        String addressFloor,
        String addressApartment,
        UUID residencePlaceId,
        String postalCode,

        // Información laboral
        String occupation,
        String workplace,
        String workplacePhone,

        // Estado
        boolean active,

        // Auditoría
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}