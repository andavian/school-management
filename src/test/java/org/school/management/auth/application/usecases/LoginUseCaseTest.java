package org.school.management.auth.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.auth.application.dto.requests.LoginRequest;
import org.school.management.auth.application.dto.responses.LoginResponse;
import org.school.management.auth.application.dto.responses.UserResponse;
import org.school.management.auth.application.mappers.AuthApplicationMapper;
import org.school.management.auth.application.security.LoginAttemptService;
import org.school.management.auth.domain.exception.InvalidPasswordException;
import org.school.management.auth.domain.exception.UserNotActiveException;
import org.school.management.auth.domain.model.RefreshToken;
import org.school.management.auth.domain.model.Role;
import org.school.management.auth.domain.model.User;
import org.school.management.auth.domain.repository.RefreshTokenRepository;
import org.school.management.auth.domain.repository.UserRepository;
import org.school.management.auth.domain.valueobject.HashedPassword;
import org.school.management.auth.domain.valueobject.PlainPassword;
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
@DisplayName("LoginUseCase")
class LoginUseCaseTest {

    @Mock private UserRepository userRepository;
    @Mock private AuthApplicationMapper mapper;
    @Mock private HashedPassword.PasswordEncoder passwordEncoder;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private TokenHasher tokenHasher;
    @Mock private SecureTokenGenerator tokenGenerator;
    @Mock private LoginAttemptService loginAttemptService;

    @InjectMocks private LoginUseCase useCase;

    // ── constantes ────────────────────────────────────────────────────────

    private static final String DNI = "20345676";
    private static final String RAW_PASSWORD = "Teacher123!";
    private static final String HASHED_PASSWORD = "$2a$10$hashedPasswordValue";
    private static final String ACCESS_TOKEN = "eyJhbG.ciOiJ.accesstoken";
    private static final String RAW_REFRESH_TOKEN = "raw-refresh-token-opaque";
    private static final String REFRESH_TOKEN_HASH = "sha256-hash-of-refresh-token";
    private static final String IP = "192.168.1.1";
    private static final String USER_AGENT = "Mozilla/5.0";

    // ── helpers ───────────────────────────────────────────────────────────

    private LoginRequest buildRequest() {
        return new LoginRequest(DNI, RAW_PASSWORD);
    }

    private User buildActiveUser() {
        Role role = Role.reconstruct(RoleId.generate(), RoleName.of("TEACHER"), LocalDateTime.now().minusDays(30));
        return User.reconstruct(
                UserId.generate(),
                Dni.of(DNI),
                HashedPassword.of(HASHED_PASSWORD),
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
                HashedPassword.of(HASHED_PASSWORD),
                Set.of(role),
                false,
                LocalDateTime.now().minusDays(30),
                null,
                LocalDateTime.now().minusDays(30)
        );
    }

    private LoginResponse buildLoginResponse(User user) {
        UserResponse userResp = new UserResponse(
                user.getUserId().asString(), DNI, Set.of("TEACHER"),
                true,
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1)
        );
        return new LoginResponse(ACCESS_TOKEN, RAW_REFRESH_TOKEN, userResp, 3600L, "Bearer");
    }

    // ── tests — flujo feliz ───────────────────────────────────────────────

    @Test
    @DisplayName("execute — flujo feliz — login exitoso con access token y refresh token")
    void execute_happyPath_returnsLoginResponseWithTokens() {
        // given
        LoginRequest request = buildRequest();
        User user = buildActiveUser();

        when(mapper.toDni(DNI)).thenReturn(Dni.of(DNI));
        when(mapper.toPlainPassword(RAW_PASSWORD)).thenReturn(PlainPassword.of(RAW_PASSWORD));
        doNothing().when(loginAttemptService).checkAttempts(any(Dni.class), eq(IP));
        when(userRepository.findByDni(any(Dni.class))).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(eq(RAW_PASSWORD), eq(HASHED_PASSWORD))).thenReturn(true);
        when(jwtTokenProvider.generateAccessToken(any())).thenReturn(ACCESS_TOKEN);
        when(tokenGenerator.generate()).thenReturn(RAW_REFRESH_TOKEN);
        when(tokenHasher.hash(RAW_REFRESH_TOKEN)).thenReturn(REFRESH_TOKEN_HASH);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(mapper.toLoginResponse(any(User.class), eq(ACCESS_TOKEN), eq(RAW_REFRESH_TOKEN)))
                .thenReturn(buildLoginResponse(user));

        // when
        LoginResponse result = useCase.execute(request, IP, USER_AGENT);

        // then
        assertThat(result).isNotNull();
        assertThat(result.token()).isEqualTo(ACCESS_TOKEN);
        assertThat(result.refreshToken()).isEqualTo(RAW_REFRESH_TOKEN);
        assertThat(result.expiresIn()).isEqualTo(3600L);
        assertThat(result.tokenType()).isEqualTo("Bearer");
        assertThat(result.user()).isNotNull();
        assertThat(result.user().dni()).isEqualTo(DNI);

        verify(loginAttemptService).checkAttempts(any(Dni.class), eq(IP));
        verify(userRepository).findByDni(any(Dni.class));
        verify(loginAttemptService).recordSuccess(any(Dni.class), eq(IP));
        verify(jwtTokenProvider).generateAccessToken(any());
        verify(tokenGenerator).generate();
        verify(tokenHasher).hash(RAW_REFRESH_TOKEN);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("execute — flujo feliz — guarda refresh token con datos del dispositivo")
    void execute_happyPath_savesRefreshTokenWithDeviceInfo() {
        // given
        LoginRequest request = buildRequest();
        User user = buildActiveUser();

        when(mapper.toDni(DNI)).thenReturn(Dni.of(DNI));
        when(mapper.toPlainPassword(RAW_PASSWORD)).thenReturn(PlainPassword.of(RAW_PASSWORD));
        doNothing().when(loginAttemptService).checkAttempts(any(Dni.class), eq(IP));
        when(userRepository.findByDni(any(Dni.class))).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(jwtTokenProvider.generateAccessToken(any())).thenReturn(ACCESS_TOKEN);
        when(tokenGenerator.generate()).thenReturn(RAW_REFRESH_TOKEN);
        when(tokenHasher.hash(RAW_REFRESH_TOKEN)).thenReturn(REFRESH_TOKEN_HASH);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(mapper.toLoginResponse(any(), any(), any())).thenReturn(buildLoginResponse(user));

        // when
        useCase.execute(request, IP, "Mozilla/5.0 Mobile Safari");

        // then
        verify(refreshTokenRepository).save(argThat(token ->
                token.getIpAddress().equals(IP)
                        && token.getUserAgent().equals("Mozilla/5.0 Mobile Safari")
                        && token.getDeviceInfo().equals("mobile")
        ));
    }

    // ── tests — credenciales inválidas ────────────────────────────────────

    @Test
    @DisplayName("execute — usuario no encontrado — lanza InvalidPasswordException y registra fallo")
    void execute_userNotFound_throwsInvalidPasswordException() {
        // given
        LoginRequest request = buildRequest();

        when(mapper.toDni(DNI)).thenReturn(Dni.of(DNI));
        when(mapper.toPlainPassword(RAW_PASSWORD)).thenReturn(PlainPassword.of(RAW_PASSWORD));
        doNothing().when(loginAttemptService).checkAttempts(any(Dni.class), eq(IP));
        when(userRepository.findByDni(any(Dni.class))).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> useCase.execute(request, IP, USER_AGENT))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessageContaining("Credenciales inválidas");

        verify(loginAttemptService).recordFailedAttempt(any(Dni.class), eq(IP));
        verifyNoInteractions(jwtTokenProvider, tokenGenerator, refreshTokenRepository);
    }

    @Test
    @DisplayName("execute — password incorrecto — lanza InvalidPasswordException")
    void execute_wrongPassword_throwsInvalidPasswordException() {
        // given
        LoginRequest request = buildRequest();
        User user = buildActiveUser();

        when(mapper.toDni(DNI)).thenReturn(Dni.of(DNI));
        when(mapper.toPlainPassword(RAW_PASSWORD)).thenReturn(PlainPassword.of(RAW_PASSWORD));
        doNothing().when(loginAttemptService).checkAttempts(any(Dni.class), eq(IP));
        when(userRepository.findByDni(any(Dni.class))).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(eq(RAW_PASSWORD), eq(HASHED_PASSWORD))).thenReturn(false);

        // when / then
        assertThatThrownBy(() -> useCase.execute(request, IP, USER_AGENT))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessageContaining("Credenciales inválidas");

        verify(loginAttemptService, never()).recordSuccess(any(), any());
        verifyNoInteractions(jwtTokenProvider, tokenGenerator);
    }

    // ── tests — usuario inactivo ──────────────────────────────────────────

    @Test
    @DisplayName("execute — usuario inactivo — lanza UserNotActiveException")
    void execute_inactiveUser_throwsUserNotActiveException() {
        // given
        LoginRequest request = buildRequest();
        User inactiveUser = buildInactiveUser();

        when(mapper.toDni(DNI)).thenReturn(Dni.of(DNI));
        when(mapper.toPlainPassword(RAW_PASSWORD)).thenReturn(PlainPassword.of(RAW_PASSWORD));
        doNothing().when(loginAttemptService).checkAttempts(any(Dni.class), eq(IP));
        when(userRepository.findByDni(any(Dni.class))).thenReturn(Optional.of(inactiveUser));

        // when / then — el usuario es inactivo, authenticate() lanza UserNotActiveException
        assertThatThrownBy(() -> useCase.execute(request, IP, USER_AGENT))
                .isInstanceOf(UserNotActiveException.class);

        verifyNoInteractions(jwtTokenProvider, tokenGenerator);
    }

    // ── tests — bloqueo por intentos ──────────────────────────────────────

    @Test
    @DisplayName("execute — IP bloqueada por exceso de intentos — lanza RuntimeException")
    void execute_blockedByRateLimit_throwsRuntimeException() {
        // given
        LoginRequest request = buildRequest();

        when(mapper.toDni(DNI)).thenReturn(Dni.of(DNI));
        when(mapper.toPlainPassword(RAW_PASSWORD)).thenReturn(PlainPassword.of(RAW_PASSWORD));
        doThrow(new RuntimeException("Demasiados intentos fallidos. Intentá nuevamente en 120 segundos."))
                .when(loginAttemptService).checkAttempts(any(Dni.class), eq(IP));

        // when / then
        assertThatThrownBy(() -> useCase.execute(request, IP, USER_AGENT))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Demasiados intentos fallidos");

        verifyNoInteractions(userRepository, jwtTokenProvider, tokenGenerator, refreshTokenRepository);
    }

    // ── tests — device info extraction ────────────────────────────────────

    @Test
    @DisplayName("execute — userAgent nulo → deviceInfo 'unknown'")
    void execute_nullUserAgent_deviceInfoUnknown() {
        // given
        LoginRequest request = buildRequest();
        User user = buildActiveUser();

        when(mapper.toDni(DNI)).thenReturn(Dni.of(DNI));
        when(mapper.toPlainPassword(RAW_PASSWORD)).thenReturn(PlainPassword.of(RAW_PASSWORD));
        doNothing().when(loginAttemptService).checkAttempts(any(Dni.class), any());
        when(userRepository.findByDni(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(jwtTokenProvider.generateAccessToken(any())).thenReturn(ACCESS_TOKEN);
        when(tokenGenerator.generate()).thenReturn(RAW_REFRESH_TOKEN);
        when(tokenHasher.hash(any())).thenReturn(REFRESH_TOKEN_HASH);
        when(refreshTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(userRepository.save(any())).thenReturn(user);
        when(mapper.toLoginResponse(any(), any(), any())).thenReturn(buildLoginResponse(user));

        // when
        useCase.execute(request, IP, null);

        // then
        verify(refreshTokenRepository).save(argThat(token ->
                "unknown".equals(token.getDeviceInfo())
        ));
    }

    @Test
    @DisplayName("execute — userAgent con 'Mobile' → deviceInfo 'mobile'")
    void execute_mobileUserAgent_deviceInfoMobile() {
        // given
        LoginRequest request = buildRequest();
        User user = buildActiveUser();

        when(mapper.toDni(DNI)).thenReturn(Dni.of(DNI));
        when(mapper.toPlainPassword(RAW_PASSWORD)).thenReturn(PlainPassword.of(RAW_PASSWORD));
        doNothing().when(loginAttemptService).checkAttempts(any(Dni.class), any());
        when(userRepository.findByDni(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(jwtTokenProvider.generateAccessToken(any())).thenReturn(ACCESS_TOKEN);
        when(tokenGenerator.generate()).thenReturn(RAW_REFRESH_TOKEN);
        when(tokenHasher.hash(any())).thenReturn(REFRESH_TOKEN_HASH);
        when(refreshTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(userRepository.save(any())).thenReturn(user);
        when(mapper.toLoginResponse(any(), any(), any())).thenReturn(buildLoginResponse(user));

        // when
        useCase.execute(request, IP, "Mozilla/5.0 Mobile");

        // then
        verify(refreshTokenRepository).save(argThat(token ->
                "mobile".equals(token.getDeviceInfo())
        ));
    }

    @Test
    @DisplayName("execute — userAgent con 'Postman' → deviceInfo 'api-client'")
    void execute_postmanUserAgent_deviceInfoApiClient() {
        // given
        LoginRequest request = buildRequest();
        User user = buildActiveUser();

        when(mapper.toDni(DNI)).thenReturn(Dni.of(DNI));
        when(mapper.toPlainPassword(RAW_PASSWORD)).thenReturn(PlainPassword.of(RAW_PASSWORD));
        doNothing().when(loginAttemptService).checkAttempts(any(Dni.class), any());
        when(userRepository.findByDni(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(jwtTokenProvider.generateAccessToken(any())).thenReturn(ACCESS_TOKEN);
        when(tokenGenerator.generate()).thenReturn(RAW_REFRESH_TOKEN);
        when(tokenHasher.hash(any())).thenReturn(REFRESH_TOKEN_HASH);
        when(refreshTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(userRepository.save(any())).thenReturn(user);
        when(mapper.toLoginResponse(any(), any(), any())).thenReturn(buildLoginResponse(user));

        // when
        useCase.execute(request, IP, "PostmanRuntime/7.0");

        // then
        verify(refreshTokenRepository).save(argThat(token ->
                "api-client".equals(token.getDeviceInfo())
        ));
    }
}
