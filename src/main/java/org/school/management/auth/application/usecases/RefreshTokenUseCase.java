package org.school.management.auth.application.usecases;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.domain.exception.UnauthorizedException;
import org.school.management.auth.domain.model.RefreshToken;
import org.school.management.auth.domain.repository.RefreshTokenRepository;
import org.school.management.auth.infra.security.jwt.JwtTokenProvider;
import org.school.management.auth.infra.security.token.TokenHasher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenUseCase {

    private final RefreshTokenRepository repository;
    private final TokenHasher tokenHasher;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthResponse execute(String rawRefreshToken) {

        String hash = tokenHasher.hash(rawRefreshToken);

        RefreshToken token = repository.findByTokenHash(hash)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        // 🚨 REUSE DETECTION
        if (token.isRevoked()) {
            log.warn("Refresh token reuse detected for DNI: {}", token.getUserDni().value());

            repository.revokeAllByUserDni(token.getUserDni());

            throw new UnauthorizedException("Token reuse detected. Session compromised.");
        }

        if (token.isExpired()) {
            throw new UnauthorizedException("Refresh token expired");
        }

        // 🔁 ROTATION
        String newRawToken = generateNewRefreshToken(token);

        // generar access token
        String accessToken = jwtTokenProvider.generateToken(token.getUserDni().value());

        return new AuthResponse(accessToken, newRawToken);
    }

    private String generateNewRefreshToken(RefreshToken oldToken) {

        String newRaw = tokenGenerator.generate();
        String newHash = tokenHasher.hash(newRaw);

        oldToken.revoke(newHash);

        RefreshToken newToken = RefreshToken.builder()
                .id(RefreshTokenId.generate())
                .userDni(oldToken.getUserDni())
                .tokenHash(newHash)
                .issuedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();

        repository.save(oldToken);
        repository.save(newToken);

        return newRaw;
    }
}