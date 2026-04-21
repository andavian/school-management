package org.school.management.auth.infra.persistence.repository;

import org.school.management.auth.infra.persistence.entity.ConfirmationTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface ConfirmationTokenJpaRepository extends JpaRepository<ConfirmationTokenEntity, UUID> {

    Optional<ConfirmationTokenEntity> findByTokenHash(String tokenHash);

    void deleteByExpiresAtBefore(LocalDateTime now);
}