package org.school.management.auth.infra.web.dto.requests;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenApiRequest(
        @NotBlank(message = "Refresh token es requerido")
        String refreshToken
) {}