package org.school.management.auth.infra.persistence.repository;

import lombok.RequiredArgsConstructor;
import org.school.management.auth.domain.model.BlacklistedToken;
import org.school.management.auth.domain.repository.BlacklistedTokenRepository;
import org.school.management.auth.domain.valueobject.BlacklistedTokenId;
import org.school.management.auth.infra.persistence.entity.BlacklistedTokenEntity;
import org.school.management.auth.infra.persistence.mappers.BlacklistedTokenPersistenceMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


// JPA Repository Interface
public interface BlacklistedTokenJpaRepository extends JpaRepository<BlacklistedTokenEntity, UUID> {

    Optional<BlacklistedTokenEntity> findByTokenHash(String tokenHash);

    boolean existsByTokenHash(String tokenHash);

    List<BlacklistedTokenEntity> findByUserDni(String userDni);

    @Query("SELECT bt FROM BlacklistedTokenEntity bt WHERE bt.expiresAt < :now")
    List<BlacklistedTokenEntity> findExpiredTokens(@Param("now") LocalDateTime now);

    @Modifying
    @Query("DELETE FROM BlacklistedTokenEntity bt WHERE bt.expiresAt < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);

    @Modifying
    @Query("DELETE FROM BlacklistedTokenEntity bt WHERE bt.userDni = :userDni AND bt.tokenType = :tokenType")
    void deleteByUserDniAndTokenType(@Param("userDni") String userEmail,
                                       @Param("tokenType") BlacklistedTokenEntity.TokenType tokenType);

    long countByUserDniAndBlacklistedAtAfter(String userDni, LocalDateTime after);
}

