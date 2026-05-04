package org.school.management.auth.infra.web.dto.requests;

import jakarta.validation.constraints.NotBlank;

/**
 * Web DTO para el endpoint POST /api/auth/logout.
 *
 * <p>{@code refreshToken} es obligatorio — se revoca en BD.</p>
 * <p>{@code accessToken} es opcional — si se envía, se blacklistea
 * inmediatamente. Si no, expirará de forma natural.</p>
 */
public record LogoutApiRequest(

        @NotBlank(message = "El refresh token es obligatorio")
        String refreshToken,

        String accessToken   // opcional
) {}