package org.school.management.auth.application.usecases;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.application.dto.requests.BlacklistTokenRequest;
import org.school.management.auth.domain.exception.UnauthorizedException;
import org.school.management.auth.domain.model.RefreshToken;
import org.school.management.auth.domain.repository.RefreshTokenRepository;
import org.school.management.auth.infra.security.token.TokenHasher;
import org.school.management.auth.infra.security.jwt.JwtTokenProvider;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Caso de uso: cerrar sesión del usuario.
 *
 * <p>Dos operaciones independientes y complementarias:</p>
 * <ol>
 *   <li><strong>Refresh token</strong> — siempre se revoca en BD ({@code revoked_at}).</li>
 *   <li><strong>Access token</strong> — opcional, se agrega a la blacklist si se envía.
 *       Esto garantiza que el access token quede inválido inmediatamente sin esperar
 *       a que expire naturalmente.</li>
 * </ol>
 *
 * <p>Si el access token no se envía en el logout (por ejemplo, en clientes que no
 * lo conservan), el sistema sigue siendo seguro porque el refresh token queda
 * revocado — el usuario no podrá renovar la sesión.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutUseCase {

    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenHasher tokenHasher;
    private final JwtTokenProvider jwtTokenProvider;
    private final BlacklistTokenUseCase blacklistTokenUseCase;

    /**
     * Cierra la sesión del usuario.
     *
     * @param rawRefreshToken token opaco de refresh (obligatorio)
     * @param rawAccessToken  JWT de acceso (opcional — si viene, se blacklistea)
     */
    @Transactional
    public void logout(String rawRefreshToken, String rawAccessToken) {

        // 1. Revocar el refresh token en BD
        revokeRefreshToken(rawRefreshToken);

        // 2. Blacklistear el access token si fue enviado
        if (rawAccessToken != null && !rawAccessToken.isBlank()) {
            blacklistAccessToken(rawRefreshToken, rawAccessToken);
        } else {
            log.debug("Logout without access token — refresh token revoked only. " +
                    "Access token will expire naturally.");
        }
    }

    /**
     * Cierra todas las sesiones del usuario en todos los dispositivos.
     * Revoca todos los refresh tokens activos del usuario.
     *
     * @param rawRefreshToken cualquier refresh token válido del usuario para identificarlo
     */
    @Transactional
    public void logoutAllDevices(String rawRefreshToken) {
        String hash = tokenHasher.hash(rawRefreshToken);

        RefreshToken token = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        refreshTokenRepository.revokeAllByUserDni(token.getUserDni());

        log.info("All sessions revoked — DNI: {}", token.getUserDni().value());
    }

    // ── helpers ───────────────────────────────────────────────────────────

    private void revokeRefreshToken(String rawRefreshToken) {
        String hash = tokenHasher.hash(rawRefreshToken);

        RefreshToken token = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (!token.isRevoked()) {
            token.revoke(null);
            refreshTokenRepository.save(token);
            log.info("Refresh token revoked — DNI: {}", token.getUserDni().value());
        } else {
            log.warn("Refresh token already revoked — DNI: {}", token.getUserDni().value());
        }
    }

    private void blacklistAccessToken(String rawRefreshToken, String rawAccessToken) {
        try {
            // Extraer expiración del JWT para saber hasta cuándo debe estar en blacklist
            var expiration = jwtTokenProvider.getExpiration(rawAccessToken);

            // Extraer DNI del usuario desde el refresh token para auditoría
            String hash = tokenHasher.hash(rawRefreshToken);
            RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(hash)
                    .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

            blacklistTokenUseCase.execute(new BlacklistTokenRequest(
                    rawAccessToken,
                    "ACCESS",
                    expiration.toInstant()
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDateTime(),
                    "LOGOUT",
                    refreshToken.getUserDni().value()
            ));

            log.info("Access token blacklisted on logout — DNI: {}",
                    refreshToken.getUserDni().value());

        } catch (Exception e) {
            // No fallar el logout si el access token es inválido o ya expiró
            log.warn("Could not blacklist access token on logout — reason: {}", e.getMessage());
        }
    }
}