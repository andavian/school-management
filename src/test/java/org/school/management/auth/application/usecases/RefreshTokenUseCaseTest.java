package org.school.management.auth.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.auth.application.dto.responses.RefreshTokenResponse;
import org.school.management.auth.domain.exception.UnauthorizedException;
import org.school.management.auth.domain.model.RefreshToken;
import org.school.management.auth.domain.model.Role;
import org.school.management.auth.domain.model.User;
import org.school.management.auth.domain.repository.RefreshTokenRepository;
import org.school.management.auth.domain.repository.UserRepository;
import org.school.management.auth.domain.valueobject.HashedPassword;
import org.school.management.auth.domain.valueobject.RefreshTokenId;
import org.school.management.auth.domain.valueobject.RoleId;
import org.school.management.auth.domain.valueobject.RoleName;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.auth.infra.security.jwt.JwtTokenProvider;
import org.school.management.auth.infra.security.token.SecureTokenGenerator;
import org.school.management.auth.infra.security.token.TokenHasher;
import org.school.management.shared.person.domain.valueobject.Dni;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("RefreshTokenUseCase")
class RefreshTokenUseCaseTest {

    @Mock private RefreshTokenRepository repository;
    @Mock private TokenHasher tokenHasher;
    @Mock private SecureTokenGenerator tokenGenerator;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private UserRepository userRepository;

    @InjectMocks private RefreshTokenUseCase useCase;

    // ── constantes ────────────────────────────────────────────────────────

    private static final String DNI = "20345676";
    private static final String RAW_REFRESH_TOKEN = "raw-refresh-token";
    private static final String REFRESH_TOKEN_HASH = "sha256-hash-refresh";
    private static final String NEW_RAW_TOKEN = "new-raw-refresh-token";
    private static final String NEW_TOKEN_HASH = "sha256-hash-new";
    private static final String ACCESS_TOKEN = "eyJhbG.ciOiJ.newAccessToken";
    private static final String IP = "192.168.1.1";
    private static final String USER_AGENT = "Mozilla/5.0";

    // ── helpers ───────────────────────────────────────────────────────────

    private RefreshToken buildValidToken() {
        return RefreshToken.builder()
                .id(RefreshTokenId.generate())
                .userDni(Dni.of(DNI))
                .tokenHash(REFRESH_TOKEN_HASH)
                .issuedAt(LocalDateTime.now().minusHours(1))
                .expiresAt(LocalDateTime.now().plusDays(6))
                .deviceInfo("browser")
                .ipAddress(IP)
                .userAgent(USER_AGENT)
                .build();
    }

    private RefreshToken buildExpiredToken() {
        return RefreshToken.builder()
                .id(RefreshTokenId.generate())
                .userDni(Dni.of(DNI))
                .tokenHash(REFRESH_TOKEN_HASH)
                .issuedAt(LocalDateTime.now().minusDays(10))
                .expiresAt(LocalDateTime.now().minusDays(3))
                .deviceInfo("browser")
                .ipAddress(IP)
                .userAgent(USER_AGENT)
                .build();
    }

    private RefreshToken buildRevokedToken() {
        RefreshToken token = buildValidToken();
        token.revoke(null);
        return token;
    }

    private User buildActiveUser() {
        Role role = Role.reconstruct(RoleId.generate(), RoleName.of("TEACHER"), LocalDateTime.now().minusDays(30));
        return User.reconstruct(
                UserId.generate(),
                Dni.of(DNI),
                HashedPassword.of("$2a$10$hashedPassword"),
                Set.of(role),
                true,
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1)
        );
    }

    private User buildInactiveUser() {
        Role role = Role.reconstruct(RoleId.generate(), RoleName.of("TEACHER"), LocalDateTime.now().minusDays(30));
        return User.reconstruct(
                UserId.generate(),
                Dni.of(DNI),
                HashedPassword.of("$2a$10$hashedPassword"),
                Set.of(role),
                false,
                LocalDateTime.now().minusDays(30),
                null,
                LocalDateTime.now().minusDays(30)
        );
    }

    // ── tests — flujo feliz ───────────────────────────────────────────────

    @Test
    @DisplayName("execute — flujo feliz — rota refresh token y genera nuevo access token")
    void execute_happyPath_rotatesTokenAndReturnsNewAccessToken() {
        // given
        RefreshToken oldToken = buildValidToken();
        User user = buildActiveUser();

        when(tokenHasher.hash(RAW_REFRESH_TOKEN)).thenReturn(REFRESH_TOKEN_HASH);
        when(repository.findByTokenHash(REFRESH_TOKEN_HASH)).thenReturn(Optional.of(oldToken));
        when(userRepository.findByDni(any(Dni.class))).thenReturn(Optional.of(user));
        when(tokenGenerator.generate()).thenReturn(NEW_RAW_TOKEN);
        when(tokenHasher.hash(NEW_RAW_TOKEN)).thenReturn(NEW_TOKEN_HASH);
        when(jwtTokenProvider.generateAccessToken(any())).thenReturn(ACCESS_TOKEN);

        // when
        RefreshTokenResponse result = useCase.execute(RAW_REFRESH_TOKEN, IP, USER_AGENT);

        // then
        assertThat(result).isNotNull();
        assertThat(result.accessToken()).isEqualTo(ACCESS_TOKEN);
        assertThat(result.refreshToken()).isEqualTo(NEW_RAW_TOKEN);

        // Verifica rotación: revoca el viejo y guarda el nuevo
        verify(repository).revoke(oldToken);
        verify(repository).save(any(RefreshToken.class));
        verify(jwtTokenProvider).generateAccessToken(any());
    }

    @Test
    @DisplayName("execute — flujo feliz — el nuevo refresh token hereda IP y deviceInfo del viejo")
    void execute_happyPath_newTokenInheritsDeviceInfo() {
        // given
        RefreshToken oldToken = buildValidToken();
        User user = buildActiveUser();

        when(tokenHasher.hash(RAW_REFRESH_TOKEN)).thenReturn(REFRESH_TOKEN_HASH);
        when(repository.findByTokenHash(REFRESH_TOKEN_HASH)).thenReturn(Optional.of(oldToken));
        when(userRepository.findByDni(any())).thenReturn(Optional.of(user));
        when(tokenGenerator.generate()).thenReturn(NEW_RAW_TOKEN);
        when(tokenHasher.hash(NEW_RAW_TOKEN)).thenReturn(NEW_TOKEN_HASH);
        when(jwtTokenProvider.generateAccessToken(any())).thenReturn(ACCESS_TOKEN);

        // when
        useCase.execute(RAW_REFRESH_TOKEN, IP, USER_AGENT);

        // then
        verify(repository).save(argThat(newToken ->
                newToken.getIpAddress().equals(IP)
                        && newToken.getUserAgent().equals(USER_AGENT)
                        && newToken.getDeviceInfo().equals("browser")
        ));
    }

    // ── tests — token no encontrado ───────────────────────────────────────

    @Test
    @DisplayName("execute — token no encontrado — lanza UnauthorizedException")
    void execute_tokenNotFound_throwsUnauthorizedException() {
        // given
        when(tokenHasher.hash(RAW_REFRESH_TOKEN)).thenReturn(REFRESH_TOKEN_HASH);
        when(repository.findByTokenHash(REFRESH_TOKEN_HASH)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> useCase.execute(RAW_REFRESH_TOKEN, IP, USER_AGENT))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Invalid refresh token");

        verifyNoInteractions(jwtTokenProvider, userRepository, tokenGenerator);
    }

    // ── tests — token expirado ────────────────────────────────────────────

    @Test
    @DisplayName("execute — token expirado — lanza UnauthorizedException")
    void execute_tokenExpired_throwsUnauthorizedException() {
        // given
        RefreshToken expiredToken = buildExpiredToken();

        when(tokenHasher.hash(RAW_REFRESH_TOKEN)).thenReturn(REFRESH_TOKEN_HASH);
        when(repository.findByTokenHash(REFRESH_TOKEN_HASH)).thenReturn(Optional.of(expiredToken));

        // when / then
        assertThatThrownBy(() -> useCase.execute(RAW_REFRESH_TOKEN, IP, USER_AGENT))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("expired");

        verifyNoInteractions(jwtTokenProvider, userRepository);
    }

    // ── tests — reuse detection ───────────────────────────────────────────

    @Test
    @DisplayName("execute — token ya revocado (reuse detection) — revoca todos y lanza excepción")
    void execute_tokenReused_revokesAllAndThrowsUnauthorizedException() {
        // given
        RefreshToken revokedToken = buildRevokedToken();

        when(tokenHasher.hash(RAW_REFRESH_TOKEN)).thenReturn(REFRESH_TOKEN_HASH);
        when(repository.findByTokenHash(REFRESH_TOKEN_HASH)).thenReturn(Optional.of(revokedToken));

        // when / then
        assertThatThrownBy(() -> useCase.execute(RAW_REFRESH_TOKEN, IP, USER_AGENT))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("compromised");

        verify(repository).revokeAllByUserDni(eq(Dni.of(DNI)));
        verifyNoInteractions(jwtTokenProvider);
    }

    // ── tests — usuario no encontrado ─────────────────────────────────────

    @Test
    @DisplayName("execute — token válido pero usuario no existe — lanza UnauthorizedException")
    void execute_userNotFound_throwsUnauthorizedException() {
        // given
        RefreshToken token = buildValidToken();

        when(tokenHasher.hash(RAW_REFRESH_TOKEN)).thenReturn(REFRESH_TOKEN_HASH);
        when(repository.findByTokenHash(REFRESH_TOKEN_HASH)).thenReturn(Optional.of(token));
        when(userRepository.findByDni(any(Dni.class))).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> useCase.execute(RAW_REFRESH_TOKEN, IP, USER_AGENT))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("User not found");

        verifyNoInteractions(jwtTokenProvider);
    }

    // ── tests — usuario deshabilitado ─────────────────────────────────────

    @Test
    @DisplayName("execute — token válido pero usuario inactivo — lanza UnauthorizedException")
    void execute_userDisabled_throwsUnauthorizedException() {
        // given
        RefreshToken token = buildValidToken();
        User inactiveUser = buildInactiveUser();

        when(tokenHasher.hash(RAW_REFRESH_TOKEN)).thenReturn(REFRESH_TOKEN_HASH);
        when(repository.findByTokenHash(REFRESH_TOKEN_HASH)).thenReturn(Optional.of(token));
        when(userRepository.findByDni(any(Dni.class))).thenReturn(Optional.of(inactiveUser));

        // when / then
        assertThatThrownBy(() -> useCase.execute(RAW_REFRESH_TOKEN, IP, USER_AGENT))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("disabled");

        verifyNoInteractions(jwtTokenProvider);
    }

    // ── tests — IP mismatch ───────────────────────────────────────────────

    @Test
    @DisplayName("execute — IP diferente — no falla, solo loguea warning")
    void execute_ipMismatch_stillSucceeds() {
        // given
        RefreshToken token = buildValidToken();
        User user = buildActiveUser();

        when(tokenHasher.hash(RAW_REFRESH_TOKEN)).thenReturn(REFRESH_TOKEN_HASH);
        when(repository.findByTokenHash(REFRESH_TOKEN_HASH)).thenReturn(Optional.of(token));
        when(userRepository.findByDni(any())).thenReturn(Optional.of(user));
        when(tokenGenerator.generate()).thenReturn(NEW_RAW_TOKEN);
        when(tokenHasher.hash(NEW_RAW_TOKEN)).thenReturn(NEW_TOKEN_HASH);
        when(jwtTokenProvider.generateAccessToken(any())).thenReturn(ACCESS_TOKEN);

        // when — IP diferente a la original
        RefreshTokenResponse result = useCase.execute(RAW_REFRESH_TOKEN, "10.0.0.1", USER_AGENT);

        // then — no falla
        assertThat(result).isNotNull();
        assertThat(result.accessToken()).isEqualTo(ACCESS_TOKEN);
    }

    @Test
    @DisplayName("execute — User-Agent diferente — no falla, solo loguea warning")
    void execute_userAgentMismatch_stillSucceeds() {
        // given
        RefreshToken token = buildValidToken();
        User user = buildActiveUser();

        when(tokenHasher.hash(RAW_REFRESH_TOKEN)).thenReturn(REFRESH_TOKEN_HASH);
        when(repository.findByTokenHash(REFRESH_TOKEN_HASH)).thenReturn(Optional.of(token));
        when(userRepository.findByDni(any())).thenReturn(Optional.of(user));
        when(tokenGenerator.generate()).thenReturn(NEW_RAW_TOKEN);
        when(tokenHasher.hash(NEW_RAW_TOKEN)).thenReturn(NEW_TOKEN_HASH);
        when(jwtTokenProvider.generateAccessToken(any())).thenReturn(ACCESS_TOKEN);

        // when — User-Agent diferente
        RefreshTokenResponse result = useCase.execute(RAW_REFRESH_TOKEN, IP, "Chrome/120.0");

        // then — no falla
        assertThat(result).isNotNull();
    }

    // ── tests — integridad de rotación ────────────────────────────────────

    @Test
    @DisplayName("execute — el token viejo se revoca con el hash del nuevo (family link)")
    void execute_rotation_oldTokenRevokedWithNewHash() {
        // given
        RefreshToken oldToken = buildValidToken();
        User user = buildActiveUser();

        when(tokenHasher.hash(RAW_REFRESH_TOKEN)).thenReturn(REFRESH_TOKEN_HASH);
        when(repository.findByTokenHash(REFRESH_TOKEN_HASH)).thenReturn(Optional.of(oldToken));
        when(userRepository.findByDni(any())).thenReturn(Optional.of(user));
        when(tokenGenerator.generate()).thenReturn(NEW_RAW_TOKEN);
        when(tokenHasher.hash(NEW_RAW_TOKEN)).thenReturn(NEW_TOKEN_HASH);
        when(jwtTokenProvider.generateAccessToken(any())).thenReturn(ACCESS_TOKEN);

        // when
        useCase.execute(RAW_REFRESH_TOKEN, IP, USER_AGENT);

        // then — el token viejo se revoca con referencia al nuevo
        verify(repository).revoke(oldToken);
        assertThat(oldToken.isRevoked()).isTrue();
    }
}
