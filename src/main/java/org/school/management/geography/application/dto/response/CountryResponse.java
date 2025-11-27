package org.school.management.geography.application.dto.response;

import lombok.Builder;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CountryResponse(
        UUID countryId,
        String name,
        String isoCode,
        String phoneCode,
        LocalDateTime createdAt
) {}

