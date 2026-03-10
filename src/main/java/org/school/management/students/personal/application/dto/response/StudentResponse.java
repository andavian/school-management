package org.school.management.students.personal.application.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de respuesta completo para un estudiante.
 * Usado en: GET /students/{id}, POST /students (tras creación)
 */
public record StudentResponse(

        UUID studentId,
        UUID userId,

        // Identidad civil
        String dni,
        String cuil,           // formato XX-XXXXXXXX-X (via Cuil.formatted())
        String firstName,
        String lastName,
        String fullName,       // firstName + " " + lastName — calculado en mapper
        LocalDate birthDate,
        int age,               // calculado en mapper via StudentPersonalData.calculateAge()
        boolean isAdult,       // calculado via StudentPersonalData.isAdult()
        String gender,
        String nationality,

        // Contacto
        String phone,
        String email,

        // Domicilio
        AddressResponse address,

        // Lugares
        PlaceResponse birthPlace,
        PlaceResponse residencePlace,

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
            UUID residencePlaceId,
            String formatted    // via Address.toStringFormatted(localityName)
    ) {}

    public record PlaceResponse(
            UUID placeId,
            String placeName,
            String provinceName,
            String countryName
    ) {}
}