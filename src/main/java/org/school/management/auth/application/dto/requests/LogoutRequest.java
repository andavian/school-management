package org.school.management.auth.application.dto.requests;

/**
 * Request para cerrar sesión.
 *
 * <p>{@code refreshToken} es obligatorio — se revoca en BD.</p>
 * <p>{@code accessToken} es opcional — si se envía, se blacklistea para
 * invalidación inmediata. Si no se envía, expirará naturalmente.</p>
 */
public record LogoutRequest(
        String refreshToken,
        String accessToken   // opcional
) {
    /**
     * Factory method para logout solo con refresh token.
     */
    public static LogoutRequest withRefreshOnly(String refreshToken) {
        return new LogoutRequest(refreshToken, null);
    }
}