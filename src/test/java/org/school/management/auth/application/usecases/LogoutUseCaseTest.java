package org.school.management.auth.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.auth.domain.exception.UnauthorizedException;
import org.school.management.auth.domain.model.RefreshToken;
import org.school.management.auth.domain.repository.RefreshTokenRepository;
import org.school.management.auth.domain.valueobject.RefreshTokenId;
import org.school.management.auth.infra.security.jwt.JwtTokenProvider;
import org.school.management.auth.infra.security.token.TokenHasher;
import org.school.management.shared.person.domain.valueobject.Dni;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("LogoutUseCase")
class LogoutUseCaseTest {

    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private TokenHasher tokenHasher;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private BlacklistTokenUseCase blacklistTokenUseCase;

    @InjectMocks private LogoutUseCase useCase;

    // ── constantes ────────────────────────────────────────────────────────

    private static final String DNI = "20345676";
    private static final String RAW_REFRESH_TOKEN = "raw-refresh-token";
    private static final String REFRESH_TOKEN_HASH = "sha256-hash-refresh";
    private static final String RAW_ACCESS_TOKEN = "eyJhbG.ciOiJ.accesstoken";
    private static final String IP = "192.168.1.1";

    // ── helpers ───────────────────────────────────────────────────────────

    private RefreshToken buildValidRefreshToken() {
        return RefreshToken.builder()
                .id(RefreshTokenId.generate())
                .userDni(Dni.of(DNI))
                .tokenHash(REFRESH_TOKEN_HASH)
                .issuedAt(LocalDateTime.now().minusDays(1))
                .expiresAt(LocalDateTime.now().plusDays(6))
                .deviceInfo("browser")
                .ipAddress(IP)
                .userAgent("Mozilla/5.0")
                .build();
    }

    private RefreshToken buildRevokedRefreshToken() {
        RefreshToken token = buildValidRefreshToken();
        token.revoke(null);
        return token;
    }

    // ── tests — logout con access token ───────────────────────────────────

    @Test
    @DisplayName("logout — flujo feliz — revoca refresh token y blacklistea access token")
    void logout_withAccessToken_revokesRefreshAndBlacklistsAccess() {
        // given
        RefreshToken token = buildValidRefreshToken();

        when(tokenHasher.hash(RAW_REFRESH_TOKEN)).thenReturn(REFRESH_TOKEN_HASH);
        when(refreshTokenRepository.findByTokenHash(REFRESH_TOKEN_HASH)).thenReturn(Optional.of(token));
        when(jwtTokenProvider.getExpiration(RAW_ACCESS_TOKEN)).thenReturn(new Date(System.currentTimeMillis() + 3600000));

        // when
        assertThatCode(() -> useCase.logout(RAW_REFRESH_TOKEN, RAW_ACCESS_TOKEN))
                .doesNotThrowAnyException();

        // then
        verify(refreshTokenRepository).revoke(token);
        verify(blacklistTokenUseCase).execute(any());
    }

    // ── tests — logout sin access token ───────────────────────────────────

    @Test
    @DisplayName("logout — sin access token (null) — solo revoca refresh token")
    void logout_withoutAccessTokenNull_onlyRevokesRefreshToken() {
        // given
        RefreshToken token = buildValidRefreshToken();

        when(tokenHasher.hash(RAW_REFRESH_TOKEN)).thenReturn(REFRESH_TOKEN_HASH);
        when(refreshTokenRepository.findByTokenHash(REFRESH_TOKEN_HASH)).thenReturn(Optional.of(token));

        // when
        assertThatCode(() -> useCase.logout(RAW_REFRESH_TOKEN, null))
                .doesNotThrowAnyException();

        // then
        verify(refreshTokenRepository).revoke(token);
        verifyNoInteractions(blacklistTokenUseCase, jwtTokenProvider);
    }

    @Test
    @DisplayName("logout — access token en blanco — solo revoca refresh token")
    void logout_withBlankAccessToken_onlyRevokesRefreshToken() {
        // given
        RefreshToken token = buildValidRefreshToken();

        when(tokenHasher.hash(RAW_REFRESH_TOKEN)).thenReturn(REFRESH_TOKEN_HASH);
        when(refreshTokenRepository.findByTokenHash(REFRESH_TOKEN_HASH)).thenReturn(Optional.of(token));

        // when
        assertThatCode(() -> useCase.logout(RAW_REFRESH_TOKEN, "   "))
                .doesNotThrowAnyException();

        // then
        verify(refreshTokenRepository).revoke(token);
        verifyNoInteractions(blacklistTokenUseCase, jwtTokenProvider);
    }

    // ── tests — refresh token inválido ────────────────────────────────────

    @Test
    @DisplayName("logout — refresh token inválido — lanza UnauthorizedException")
    void logout_invalidRefreshToken_throwsUnauthorizedException() {
        // given
        when(tokenHasher.hash(RAW_REFRESH_TOKEN)).thenReturn(REFRESH_TOKEN_HASH);
        when(refreshTokenRepository.findByTokenHash(REFRESH_TOKEN_HASH)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> useCase.logout(RAW_REFRESH_TOKEN, RAW_ACCESS_TOKEN))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Invalid refresh token");

        verify(refreshTokenRepository, never()).revoke(any());
        verifyNoInteractions(blacklistTokenUseCase);
    }

    // ── tests — refresh token ya revocado ─────────────────────────────────

    @Test
    @DisplayName("logout — refresh token ya revocado — no lanza excepción ni revoca otra vez")
    void logout_alreadyRevokedRefreshToken_noExceptionAndNoRevoke() {
        // given
        RefreshToken revokedToken = buildRevokedRefreshToken();

        when(tokenHasher.hash(RAW_REFRESH_TOKEN)).thenReturn(REFRESH_TOKEN_HASH);
        when(refreshTokenRepository.findByTokenHash(REFRESH_TOKEN_HASH)).thenReturn(Optional.of(revokedToken));

        // when
        assertThatCode(() -> useCase.logout(RAW_REFRESH_TOKEN, RAW_ACCESS_TOKEN))
                .doesNotThrowAnyException();

        // then — no debe llamar a revoke() porque ya está revocado
        verify(refreshTokenRepository, never()).revoke(any(RefreshToken.class));
    }

    // ── tests — logoutAllDevices ──────────────────────────────────────────

    @Test
    @DisplayName("logoutAllDevices — flujo feliz — revoca todos los refresh tokens del usuario")
    void logoutAllDevices_validToken_revokesAllUserTokens() {
        // given
        RefreshToken token = buildValidRefreshToken();

        when(tokenHasher.hash(RAW_REFRESH_TOKEN)).thenReturn(REFRESH_TOKEN_HASH);
        when(refreshTokenRepository.findByTokenHash(REFRESH_TOKEN_HASH)).thenReturn(Optional.of(token));

        // when
        assertThatCode(() -> useCase.logoutAllDevices(RAW_REFRESH_TOKEN))
                .doesNotThrowAnyException();

        // then
        verify(refreshTokenRepository).revokeAllByUserDni(eq(Dni.of(DNI)));
    }

    @Test
    @DisplayName("logoutAllDevices — refresh token inválido — lanza UnauthorizedException")
    void logoutAllDevices_invalidToken_throwsUnauthorizedException() {
        // given
        when(tokenHasher.hash(RAW_REFRESH_TOKEN)).thenReturn(REFRESH_TOKEN_HASH);
        when(refreshTokenRepository.findByTokenHash(REFRESH_TOKEN_HASH)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> useCase.logoutAllDevices(RAW_REFRESH_TOKEN))
                .isInstanceOf(UnauthorizedException.class);

        verify(refreshTokenRepository, never()).revokeAllByUserDni(any());
    }

    // ── tests — blacklist falla silenciosamente ───────────────────────────

    @Test
    @DisplayName("logout — si el blacklist del access token falla, no interrumpe el logout")
    void logout_blacklistFails_logoutStillSucceeds() {
        // given
        RefreshToken token = buildValidRefreshToken();

        when(tokenHasher.hash(RAW_REFRESH_TOKEN)).thenReturn(REFRESH_TOKEN_HASH);
        when(refreshTokenRepository.findByTokenHash(REFRESH_TOKEN_HASH)).thenReturn(Optional.of(token));
        when(jwtTokenProvider.getExpiration(RAW_ACCESS_TOKEN)).thenThrow(new RuntimeException("JWT expired"));

        // when — no debe lanzar excepción
        assertThatCode(() -> useCase.logout(RAW_REFRESH_TOKEN, RAW_ACCESS_TOKEN))
                .doesNotThrowAnyException();

        // then — el refresh token sí se revocó
        verify(refreshTokenRepository).revoke(token);
    }
}
