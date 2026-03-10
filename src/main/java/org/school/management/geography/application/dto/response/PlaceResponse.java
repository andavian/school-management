package org.school.management.geography.application.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;


public record PlaceResponse(
        UUID placeId,
        UUID provinceId,
        String name,
        String type,
        String typeDisplayName,
        String postalCode,
        LocalDateTime createdAt,

        // Jerarquía completa
        String provinceName,
        String provinceCode,
        String countryName,
        String countryIsoCode,

        // Descripciones formateadas
        String fullAddress,
        String fullDescription
) {}
