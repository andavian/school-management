package org.school.management.students.personal.application.dto.response;

import java.util.UUID;

/**
 * DTO de respuesta resumido para listas y búsquedas.
 * Usado en: GET /students?dni=...&fullName=...
 * No expone datos sensibles como domicilio completo.
 */
public record StudentSummaryResponse(
        UUID studentId,
        String dni,
        String fullName,
        int age,
        String email,   // nullable — menores pueden no tener
        String phone    // nullable
) {}