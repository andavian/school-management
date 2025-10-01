package org.school.management.auth.application.dto.responses;

public record LoginResponse(
        String token,
        String refreshToken,
        UserResponse user,
        Long expiresIn,
        String tokenType
) {}
