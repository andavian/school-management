package org.school.management.students.personal.application.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;


public record StudentResponse(
        UUID studentId,
        UUID userId,
        String dni,
        String cuil,
        String fullName,
        String firstName,
        String lastName,
        LocalDate birthDate,
        int age,
        boolean isAdult,
        String gender,
        String nationality,
        String phone,
        String email,
        AddressDto address,
        PlaceDto birthPlace,
        PlaceDto residencePlace,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    /**
     * Nested record: Dirección (según Address del Shared Kernel)
     */
    public record AddressDto(
            String street,
            String number,
            String floor,
            String apartment,
            UUID residencePlaceId,
            String localityName,        // Nombre de la localidad (desde Geography)
            String postalCode,
            String formatted            // Formato completo: "Av. Colón 1234, Piso 5, Depto A, Córdoba, CP X5000"
    ) {}

    /**
     * Nested record: Lugar (birth place o residence place)
     */
    public record PlaceDto(
            UUID placeId,
            String placeName,           // "Córdoba Capital"
            String provinceName,        // "Córdoba"
            String countryName          // "Argentina"
    ) {}
}

