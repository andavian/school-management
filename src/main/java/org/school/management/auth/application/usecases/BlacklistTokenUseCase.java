package org.school.management.auth.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.application.dto.requests.BlacklistTokenRequest;
import org.school.management.auth.application.dto.responses.BlacklistedTokenResponse;
import org.school.management.auth.application.mappers.BlacklistedTokenApplicationMapper;
import org.school.management.auth.domain.model.BlacklistedToken;
import org.school.management.auth.domain.repository.BlacklistedTokenRepository;
import org.school.management.auth.infra.security.util.TokenHashUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlacklistTokenUseCase {

    private final BlacklistedTokenRepository repository;
    private final BlacklistedTokenApplicationMapper mapper;

    @Transactional
    public BlacklistedTokenResponse execute(BlacklistTokenRequest request) {
        // Hash del token por seguridad
        String tokenHash = TokenHashUtil.hashToken(request.token());

        // Verificar si ya está en blacklist
        if (repository.existsByTokenHash(tokenHash)) {
            log.warn("Token ya está en blacklist para usuario: {}", request.userDni());
            throw new TokenAlreadyBlacklistedException("Token is already blacklisted");
        }

        // Crear y guardar
        BlacklistedToken blacklistedToken = BlacklistedToken.create(
                tokenHash,
                request.tokenType(),
                request.expiresAt(),
                request.reason(),
                request.userDni()
        );

        BlacklistedToken saved = repository.save(blacklistedToken);

        log.info("Token agregado al blacklist para usuario: {}, razón: {}",
                request.userDni(), request.reason());

        return mapper.toResponse(saved);
    }

    public static class TokenAlreadyBlacklistedException extends RuntimeException {
        public TokenAlreadyBlacklistedException(String message) {
            super(message);
        }
    }
}