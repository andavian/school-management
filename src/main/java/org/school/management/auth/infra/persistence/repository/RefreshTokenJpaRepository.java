package org.school.management.auth.infra.persistence.repository;

import org.school.management.auth.infra.persistence.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, UUID> {

    Optional<RefreshTokenEntity> findByTokenHash(String tokenHash);

    List<RefreshTokenEntity> findByUserDni(String userDni);

    @Modifying
    @Query("""
        UPDATE RefreshTokenEntity t
        SET t.revokedAt = :now
        WHERE t.userDni = :userDni
          AND t.revokedAt IS NULL
    """)
    void revokeAllActiveByUserDni(String userDni, LocalDateTime now);
}