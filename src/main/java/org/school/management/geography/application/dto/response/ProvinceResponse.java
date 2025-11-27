package org.school.management.geography.application.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ProvinceResponse(
        UUID provinceId,
        UUID countryId,
        String countryName,
        String name,
        String code,
        LocalDateTime createdAt
) {}
