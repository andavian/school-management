package org.school.management.geography.application.dto.request;

import java.util.UUID;

public record CreatePlaceRequest(
        String name,
        UUID provinceId,
        String type,  // CIUDAD, LOCALIDAD, etc.
        String postalCode  // Opcional
) {}
