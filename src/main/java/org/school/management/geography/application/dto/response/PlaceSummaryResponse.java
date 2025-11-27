package org.school.management.geography.application.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PlaceSummaryResponse(
        UUID placeId,
        String name,
        String type,
        String postalCode
) {}
