package org.school.management.auth.application.usecases;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.school.management.auth.domain.exception.UnauthorizedException;
import org.school.management.auth.domain.model.RefreshToken;
import org.school.management.auth.domain.repository.RefreshTokenRepository;
import org.school.management.auth.infra.security.token.TokenHasher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutUseCase {

    private final RefreshTokenRepository repository;
    private final TokenHasher tokenHasher;

    @Transactional
    public void logout(String rawRefreshToken) {

        String hash = tokenHasher.hash(rawRefreshToken);

        RefreshToken token = repository.findByTokenHash(hash)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (!token.isRevoked()) {
            token.revoke(null);
            repository.save(token);
        }
    }

    @Transactional
    public void logoutAllDevices(String rawRefreshToken) {

        String hash = tokenHasher.hash(rawRefreshToken);

        RefreshToken token = repository.findByTokenHash(hash)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        repository.revokeAllByUserDni(token.getUserDni());
    }
}