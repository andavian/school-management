package org.school.management.students.health.infrastructure.web.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Contenedor de Web DTOs para el módulo students/health.
 * Todos los records de request/response web están aquí.
 */
public final class HealthRecordWebDto {

    private HealthRecordWebDto() {}

    public record UpdateHealthRecordWebRequest(
            String bloodType,
            String healthInsurance,
            String healthInsuranceNumber,
            String allergies,
            String chronicConditions,
            String medications,
            String medicalObservations,
            String emergencyContactFirstName,
            String emergencyContactLastName,
            String emergencyContactPhone
    ) {}

    public record HealthRecordWebResponse(
            UUID healthRecordId,
            UUID studentId,
            String bloodType,
            String healthInsurance,
            String healthInsuranceNumber,
            String allergies,
            String chronicConditions,
            String medications,
            String medicalObservations,
            String emergencyContactFirstName,
            String emergencyContactLastName,
            String emergencyContactFullName,
            String emergencyContactPhone,
            boolean hasCompleteHealthInfo,
            boolean hasAllergies,
            boolean hasChronicConditions,
            boolean requiresMedication,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}
}