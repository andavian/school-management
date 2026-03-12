package org.school.management.students.enrollment.infrastructure.web.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Clase contenedora de todos los Web DTOs del módulo enrollment.
 * Patrón consistente con StudentWebDto y HealthRecordWebDto.
 */
public final class EnrollmentWebDto {

    private EnrollmentWebDto() {}

    // ── Request ───────────────────────────────────────────────────────────

    public record UpdateEnrollmentWebRequest(

            // Cierre de ciclo
            @DecimalMin(value = "1.0", message = "El promedio final no puede ser menor a 1")
            @DecimalMax(value = "10.0", message = "El promedio final no puede ser mayor a 10")
            @Digits(integer = 2, fraction = 2, message = "Formato de promedio inválido")
            BigDecimal finalAverage,

            Boolean passed,

            // Baja
            UUID withdrawalReasonId,

            @Size(max = 500, message = "Las observaciones no pueden superar los 500 caracteres")
            String withdrawalObservations

    ) {}

    // ── Response ──────────────────────────────────────────────────────────

    public record EnrollmentWebResponse(

            UUID enrollmentId,
            UUID studentId,
            UUID academicYearId,
            UUID gradeLevelId,

            LocalDate enrollmentDate,
            String enrollmentType,
            String status,

            boolean isRepeating,
            String previousSchool,
            LocalDate transferDate,

            // Cierre de ciclo
            BigDecimal finalAverage,
            Boolean passed,
            LocalDate completionDate,

            // Baja
            LocalDate withdrawalDate,
            UUID withdrawalReasonId,
            String withdrawalObservations,

            // Calculados
            boolean active,
            boolean canReceiveGrades,
            long durationInDays,

            // Auditoría
            LocalDateTime createdAt,
            LocalDateTime updatedAt

    ) {}

    public record EnrollmentSummaryWebResponse(
            UUID enrollmentId,
            UUID academicYearId,
            UUID gradeLevelId,
            String status,
            LocalDate enrollmentDate,
            boolean isRepeating,
            BigDecimal finalAverage,
            Boolean passed
    ) {}
}