package org.school.management.auth.infra.persistence.adapters;

import lombok.RequiredArgsConstructor;
import org.school.management.auth.domain.model.BlacklistedToken;
import org.school.management.auth.domain.repository.BlacklistedTokenRepository;
import org.school.management.auth.domain.valueobject.BlacklistedTokenId;
import org.school.management.auth.infra.persistence.entity.BlacklistedTokenEntity;
import org.school.management.auth.infra.persistence.mappers.BlacklistedTokenPersistenceMapper;
import org.school.management.auth.infra.persistence.repository.BlacklistedTokenJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Repository Implementation
@Repository
@RequiredArgsConstructor
public class BlacklistedTokenRepositoryImpl implements BlacklistedTokenRepository {

    private final BlacklistedTokenJpaRepository jpaRepository;
    private final BlacklistedTokenPersistenceMapper mapper;

    @Override
    public BlacklistedToken save(BlacklistedToken blacklistedToken) {
        BlacklistedTokenEntity entity = mapper.toEntity(blacklistedToken);
        BlacklistedTokenEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<BlacklistedToken> findById(BlacklistedTokenId id) {
        return jpaRepository.findById(id.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<BlacklistedToken> findByTokenHash(String tokenHash) {
        return jpaRepository.findByTokenHash(tokenHash)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByTokenHash(String tokenHash) {
        return jpaRepository.existsByTokenHash(tokenHash);
    }

    @Override
    public List<BlacklistedToken> findByUserDni(String userDni) {
        return jpaRepository.findByUserDni(userDni)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteExpiredTokens() {
        jpaRepository.deleteExpiredTokens(LocalDateTime.now());
    }

    @Override
    @Transactional
    public void deleteByUserDniAndTokenType(String userEmail, String tokenType) {
        BlacklistedTokenEntity.TokenType type = BlacklistedTokenEntity.TokenType.valueOf(tokenType);
        jpaRepository.deleteByUserDniAndTokenType(userEmail, type);
    }

    @Override
    public long countByUserDniAndBlacklistedAtAfter(String userDni, LocalDateTime after) {
        return jpaRepository.countByUserDniAndBlacklistedAtAfter(userDni, after);
    }

    @Override
    public List<BlacklistedToken> findExpiredTokens() {
        return jpaRepository.findExpiredTokens(LocalDateTime.now())
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
