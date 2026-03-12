package org.school.management.students.enrollment.application.dto.response;

import org.school.management.students.enrollment.domain.valueobject.EnrollmentStatus;
import org.school.management.students.enrollment.domain.valueobject.EnrollmentType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO para StudentEnrollment.
 * Expone datos de la inscripción activa o histórica de un estudiante.
 */
public record EnrollmentResponse(

        UUID enrollmentId,
        UUID studentId,
        UUID academicYearId,
        UUID gradeLevelId,

        LocalDate enrollmentDate,
        EnrollmentType enrollmentType,
        EnrollmentStatus status,

        // Origen
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