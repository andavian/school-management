package org.school.management.auth.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.application.dto.BlacklistTokenRequest;
import org.school.management.auth.application.dto.BlacklistedTokenResponse;
import org.school.management.auth.application.mappers.BlacklistedTokenApplicationMapper;
import org.school.management.auth.domain.model.BlacklistedToken;
import org.school.management.auth.domain.repository.BlacklistedTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlacklistTokenUseCase {

    private final BlacklistedTokenRepository repository;
    private final BlacklistedTokenApplicationMapper mapper;

    @Transactional
    public BlacklistedTokenResponse execute(BlacklistTokenRequest request) {
        // Hash del token por seguridad
        String tokenHash = hashToken(request.getTokenHash());

        // Verificar si ya está en blacklist
        if (repository.existsByTokenHash(tokenHash)) {
            log.warn("Token ya está en blacklist para usuario: {}", request.getUserEmail());
            throw new TokenAlreadyBlacklistedException("Token is already blacklisted");
        }

        // Crear y guardar
        BlacklistedToken blacklistedToken = BlacklistedToken.create(
                tokenHash,
                request.getTokenType(),
                request.getExpiresAt(),
                request.getReason(),
                request.getUserEmail()
        );

        BlacklistedToken saved = repository.save(blacklistedToken);

        log.info("Token agregado al blacklist para usuario: {}, razón: {}",
                request.getUserEmail(), request.getReason());

        return mapper.toResponse(saved);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error hashing token", e);
        }
    }

    public static class TokenAlreadyBlacklistedException extends RuntimeException {
        public TokenAlreadyBlacklistedException(String message) {
            super(message);
        }
    }
}