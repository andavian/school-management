package org.school.management.auth.domain.repository;

import org.school.management.auth.domain.model.ConfirmationToken;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ConfirmationTokenRepository {

    ConfirmationToken save(ConfirmationToken token);

    Optional<ConfirmationToken> findByTokenHash(String hash);

    void deleteExpired(LocalDateTime now);
}
