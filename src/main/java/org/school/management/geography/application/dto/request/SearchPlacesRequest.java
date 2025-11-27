package org.school.management.geography.application.dto.request;

import java.util.UUID;

public record SearchPlacesRequest(
        String query,
        UUID provinceId  // Opcional: null = búsqueda global
) {}
