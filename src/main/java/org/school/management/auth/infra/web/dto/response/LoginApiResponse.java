package org.school.management.auth.infra.web.dto.response;

public record LoginApiResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Long expiresIn,
        UserApiResponse user
) {}