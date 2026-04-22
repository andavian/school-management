package org.school.management.auth.application.usecases;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.application.dto.responses.RefreshTokenResponse;
import org.school.management.auth.domain.exception.UnauthorizedException;
import org.school.management.auth.domain.model.RefreshToken;
import org.school.management.auth.domain.repository.RefreshTokenRepository;
import org.school.management.auth.domain.repository.UserRepository;
import org.school.management.auth.domain.valueobject.RefreshTokenId;
import org.school.management.auth.domain.model.User;
import org.school.management.auth.infra.security.UserPrincipal;
import org.school.management.auth.infra.security.jwt.JwtTokenProvider;
import org.school.management.auth.infra.security.token.TokenHasher;
import org.school.management.auth.infra.security.token.SecureTokenGenerator;
import org.school.management.shared.person.domain.valueobject.Dni;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenUseCase {

    private final RefreshTokenRepository repository;
    private final TokenHasher tokenHasher;
    private final SecureTokenGenerator tokenGenerator;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Transactional
    public RefreshTokenResponse execute(String rawRefreshToken, String ip, String userAgent) {

        String hash = tokenHasher.hash(rawRefreshToken);

        RefreshToken token = repository.findByTokenHash(hash)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (token.getIpAddress() != null && !token.getIpAddress().equals(ip)) {
            log.warn("⚠️ IP mismatch for DNI {} | expected={} actual={}",
                    token.getUserDni().value(),
                    token.getIpAddress(),
                    ip);
        }

        if (token.getUserAgent() != null && !token.getUserAgent().equals(userAgent)) {
            log.warn("⚠️ User-Agent mismatch for DNI {}",
                    token.getUserDni().value());
        }

        Dni dni = token.getUserDni();

        // REUSE DETECTION
        if (token.isRevoked()) {
            log.warn("🚨 Refresh token reuse detected | dni={} ip={} device={}",
                    dni.value(),
                    token.getIpAddress(),
                    token.getDeviceInfo());

            repository.revokeAllByUserDni(dni);

            throw new UnauthorizedException("Session compromised. Please login again.");
        }

        // EXPIRATION
        if (token.isExpired()) {
            throw new UnauthorizedException("Refresh token expired");
        }

        // USER VALIDATION
        User user = userRepository.findByDni(dni)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        if (!user.getActive()) {
            throw new UnauthorizedException("User disabled");
        }

        // 🔁 ROTATION (ATÓMICA)
        RotationResult rotation = rotateRefreshToken(token);

        // 🔐 ACCESS TOKEN
        UserPrincipal userPrincipal = new UserPrincipal(user);
        String accessToken = jwtTokenProvider.generateAccessToken(userPrincipal);

        return new RefreshTokenResponse(accessToken, rotation.newRawToken());
    }

    private RotationResult rotateRefreshToken(RefreshToken oldToken) {

        LocalDateTime now = LocalDateTime.now();

        String newRaw = tokenGenerator.generate();
        String newHash = tokenHasher.hash(newRaw);

        // ⚠️ IMPORTANTE: revocar ANTES de persistir el nuevo
        oldToken.revoke(newHash);

        RefreshToken newToken = RefreshToken.builder()
                .id(RefreshTokenId.generate())
                .userDni(oldToken.getUserDni())
                .tokenHash(newHash)
                .issuedAt(now)
                .expiresAt(now.plusDays(7))
                .deviceInfo(oldToken.getDeviceInfo())
                .ipAddress(oldToken.getIpAddress())
                .userAgent(oldToken.getUserAgent())
                .build();

        repository.save(oldToken);
        repository.save(newToken);

        return new RotationResult(newRaw);
    }

    private record RotationResult(String newRawToken) {}
}