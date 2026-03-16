package org.school.management.teachers.application.dto.response;

import org.school.management.teachers.domain.valueobject.EmploymentStatus;
import org.school.management.teachers.domain.valueobject.EmploymentType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record TeacherResponse(

        UUID teacherId,
        UUID userId,

        // Datos personales
        String firstName,
        String lastName,
        String fullName,
        String dni,
        String cuil,
        String email,
        LocalDate birthDate,
        String gender,
        String nationality,

        // Contacto
        String phone,
        AddressResponse address,

        // Lugares
        PlaceResponse birthPlace,
        PlaceResponse residencePlace,

        // Profesional
        String specialization,
        String teachingLicense,
        LocalDate hireDate,
        EmploymentStatus employmentStatus,
        EmploymentType employmentType,

        // Estado
        boolean active,
        boolean pendingActivation,
        LocalDateTime activatedAt,

        // Auditoría
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public record AddressResponse(
            String street,
            String number,
            String floor,
            String apartment,
            String postalCode,
            UUID placeId
    ) {}

    public record PlaceResponse(
            UUID placeId,
            String placeName,
            String provinceName,
            String countryName
    ) {}
}