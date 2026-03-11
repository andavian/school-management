package org.school.management.students.health.application.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record HealthRecordResponse(
        UUID healthRecordId,
        UUID studentId,

        // Datos médicos
        String bloodType,
        String healthInsurance,
        String healthInsuranceNumber,
        String allergies,
        String chronicConditions,
        String medications,
        String medicalObservations,

        // Contacto de emergencia
        String emergencyContactFirstName,
        String emergencyContactLastName,
        String emergencyContactFullName,
        String emergencyContactPhone,

        // Flags calculados del dominio
        boolean hasCompleteHealthInfo,
        boolean hasAllergies,
        boolean hasChronicConditions,
        boolean requiresMedication,

        // Auditoría
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}