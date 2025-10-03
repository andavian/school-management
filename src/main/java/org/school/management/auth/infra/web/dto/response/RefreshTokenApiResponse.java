package org.school.management.auth.infra.web.dto.response;

public record RefreshTokenApiResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Long expiresIn
) {}
