package org.school.management.geography.application.dto.response;

import lombok.Builder;

@Builder
public record GeographyStatisticsResponse(
        long totalCountries,
        long totalProvinces,
        long totalPlaces,
        long citiesCount,
        long localitiesCount
) {}
