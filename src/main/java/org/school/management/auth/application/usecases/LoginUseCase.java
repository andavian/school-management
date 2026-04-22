package org.school.management.auth.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.application.dto.requests.LoginRequest;
import org.school.management.auth.application.dto.responses.LoginResponse;
import org.school.management.auth.application.mappers.AuthApplicationMapper;
import org.school.management.auth.application.security.LoginAttemptService;
import org.school.management.auth.domain.exception.InvalidPasswordException;
import org.school.management.auth.domain.exception.UserNotActiveException;
import org.school.management.auth.domain.model.RefreshToken;
import org.school.management.auth.domain.model.User;
import org.school.management.auth.domain.repository.RefreshTokenRepository;
import org.school.management.auth.domain.repository.UserRepository;
import org.school.management.auth.domain.valueobject.PlainPassword;
import org.school.management.auth.domain.valueobject.HashedPassword;
import org.school.management.auth.domain.valueobject.RefreshTokenId;
import org.school.management.auth.infra.security.jwt.JwtTokenProvider;
import org.school.management.auth.infra.security.UserPrincipal;
import org.school.management.auth.infra.security.token.SecureTokenGenerator;
import org.school.management.auth.infra.security.token.TokenHasher;
import org.school.management.shared.person.domain.valueobject.Dni;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginUseCase {

    private final UserRepository userRepository;
    private final AuthApplicationMapper mapper;
    private final HashedPassword.PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenHasher tokenHasher;
    private final SecureTokenGenerator tokenGenerator;
    private final LoginAttemptService loginAttemptService;

    public LoginResponse execute(LoginRequest request, String ip, String userAgent) {

        log.info("Intento de login con DNI: {}", request.dni());

        Dni dni = mapper.toDni(request.dni());
        PlainPassword plainPassword = mapper.toPlainPassword(request.password());

        loginAttemptService.checkAttempts(dni, ip);

        User user = userRepository.findByDni(dni)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado con DNI: {}", request.dni());
                    loginAttemptService.recordFailedAttempt(dni, ip);
                    return new InvalidPasswordException("Credenciales inválidas");
                });

        try {
            boolean authenticated = user.authenticate(plainPassword, passwordEncoder);

            if (!authenticated) {
                log.warn("Password incorrecto para DNI: {}", request.dni());
                throw new InvalidPasswordException("Credenciales inválidas");
            }

            loginAttemptService.recordSuccess(dni, ip);

        } catch (UserNotActiveException e) {
            log.warn("Usuario inactivo intentó hacer login. DNI: {}", request.dni());
            throw new UserNotActiveException("Cuenta inactiva. Contacte al administrador.");
        }

        // 🔐 ACCESS TOKEN (JWT)
        UserDetails userPrincipal = new UserPrincipal(user);
        String accessToken = jwtTokenProvider.generateAccessToken(userPrincipal);

        // 🔁 REFRESH TOKEN (CORRECTO)
        String rawRefreshToken = tokenGenerator.generate();
        String hash = tokenHasher.hash(rawRefreshToken);

        LocalDateTime now = LocalDateTime.now();

        RefreshToken refreshToken = RefreshToken.builder()
                .id(RefreshTokenId.generate())
                .userDni(dni)
                .tokenHash(hash)
                .issuedAt(now)
                .expiresAt(now.plusDays(7))
                .ipAddress(ip)
                .userAgent(userAgent)
                .deviceInfo(extractDeviceInfo(userAgent))
                .build();

        refreshTokenRepository.save(refreshToken);

        // (opcional) actualizar last login
        User updatedUser = userRepository.save(user);

        log.info("Login exitoso para DNI: {} con roles: {}",
                request.dni(),
                user.getRoles().stream().map(r -> r.getName().name()).toList());

        return mapper.toLoginResponse(updatedUser, accessToken, rawRefreshToken);
    }

    // ─────────────────────────────────────────────

    private String extractDeviceInfo(String userAgent) {
        if (userAgent == null) return "unknown";

        if (userAgent.contains("Mobile")) return "mobile";
        if (userAgent.contains("Postman")) return "api-client";

        return "browser";
    }
}