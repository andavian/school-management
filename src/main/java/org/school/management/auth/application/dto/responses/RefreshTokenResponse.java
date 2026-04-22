package org.school.management.auth.application.dto.responses;

public record RefreshTokenResponse(
        String accessToken,
        String refreshToken
) {}
