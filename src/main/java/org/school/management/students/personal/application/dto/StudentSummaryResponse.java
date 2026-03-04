package org.school.management.students.personal.application.dto;

import java.util.UUID;

/**
 * Response DTO: Resumen simple del estudiante
 * Para listas y búsquedas
 */
public record StudentSummaryResponse(
        UUID studentId,
        String dni,
        String fullName,
        int age,
        String email,
        String phone
) {
}
