package org.school.management.auth.domain.repository;

import org.school.management.auth.domain.model.BlacklistedToken;
import org.school.management.auth.domain.valueobject.BlacklistedTokenId;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BlacklistedTokenRepository {

    BlacklistedToken save(BlacklistedToken blacklistedToken);

    Optional<BlacklistedToken> findById(BlacklistedTokenId id);

    Optional<BlacklistedToken> findByTokenHash(String tokenHash);

    boolean existsByTokenHash(String tokenHash);

    List<BlacklistedToken> findByUserEmail(String userEmail);

    void deleteExpiredTokens();

    void deleteByUserEmailAndTokenType(String userEmail, String tokenType);

    long countByUserEmailAndBlacklistedAtAfter(String userEmail, LocalDateTime after);

    // Para limpieza automática
    List<BlacklistedToken> findExpiredTokens();
}
