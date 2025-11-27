package org.school.management.geography.application.dto.request;

import java.util.UUID;

public record ListPlacesByProvinceRequest(
        UUID provinceId,
        String type  // Opcional: CIUDAD, LOCALIDAD, etc.
) {}
