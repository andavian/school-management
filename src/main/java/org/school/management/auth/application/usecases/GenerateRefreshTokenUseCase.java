package org.school.management.auth.application.usecases;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.school.management.auth.domain.model.RefreshToken;
import org.school.management.auth.domain.repository.RefreshTokenRepository;
import org.school.management.auth.domain.valueobject.RefreshTokenId;
import org.school.management.auth.infra.security.token.SecureTokenGenerator;
import org.school.management.auth.infra.security.token.TokenHasher;
import org.school.management.shared.person.domain.valueobject.Dni;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GenerateRefreshTokenUseCase {

    private final RefreshTokenRepository repository;
    private final SecureTokenGenerator tokenGenerator;
    private final TokenHasher tokenHasher;

    private final Duration TTL = Duration.ofDays(7);

    @Transactional
    public String execute(Dni dni, String deviceInfo, String ip, String userAgent) {

        String rawToken = tokenGenerator.generate();
        String hash = tokenHasher.hash(rawToken);

        RefreshToken token = RefreshToken.builder()
                .id(RefreshTokenId.generate())
                .userDni(dni)
                .tokenHash(hash)
                .issuedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plus(TTL))
                .deviceInfo(deviceInfo)
                .ipAddress(ip)
                .userAgent(userAgent)
                .build();

        repository.save(token);

        return rawToken;
    }
}