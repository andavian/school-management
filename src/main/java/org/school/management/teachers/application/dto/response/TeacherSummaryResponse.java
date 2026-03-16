package org.school.management.teachers.application.dto.response;

import org.school.management.teachers.domain.valueobject.EmploymentStatus;
import org.school.management.teachers.domain.valueobject.EmploymentType;

/**
 * Respuesta reducida para listados y búsquedas.
 */
public record TeacherSummaryResponse(
        String teacherId,
        String firstName,
        String lastName,
        String fullName,
        String dni,
        String email,
        String phone,
        String specialization,
        EmploymentStatus employmentStatus,
        EmploymentType employmentType,
        boolean active
) {}