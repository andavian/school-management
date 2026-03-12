package org.school.management.students.enrollment.application.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Request PATCH para actualizar una inscripción.
 *
 * Soporta dos operaciones de negocio:
 *  - Cierre de ciclo: completar con promedio final (finalAverage + passed)
 *  - Baja del estudiante: registrar retiro (withdrawalReasonId + withdrawalObservations)
 *
 * Campos null conservan su valor actual (semántica PATCH).
 */
public record UpdateEnrollmentRequest(

        // ── Cierre de ciclo ───────────────────────────────────────────────
        @DecimalMin(value = "1.0", message = "El promedio final no puede ser menor a 1")
        @DecimalMax(value = "10.0", message = "El promedio final no puede ser mayor a 10")
        @Digits(integer = 2, fraction = 2, message = "Formato de promedio inválido")
        BigDecimal finalAverage,

        Boolean passed,

        // ── Baja ──────────────────────────────────────────────────────────
        UUID withdrawalReasonId,

        @Size(max = 500, message = "Las observaciones no pueden superar los 500 caracteres")
        String withdrawalObservations
) {}