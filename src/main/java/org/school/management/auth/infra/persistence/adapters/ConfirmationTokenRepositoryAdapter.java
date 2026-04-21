package org.school.management.auth.infra.persistence.adapters;

import lombok.RequiredArgsConstructor;
import org.school.management.auth.domain.model.ConfirmationToken;
import org.school.management.auth.domain.repository.ConfirmationTokenRepository;
import org.school.management.auth.infra.persistence.mappers.ConfirmationTokenPersistenceMapper;
import org.school.management.auth.infra.persistence.repository.ConfirmationTokenJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ConfirmationTokenRepositoryAdapter implements ConfirmationTokenRepository {

    private final ConfirmationTokenJpaRepository jpaRepository;
    private final ConfirmationTokenPersistenceMapper mapper;

    @Override
    public ConfirmationToken save(ConfirmationToken token) {
        var entity = mapper.toEntity(token);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<ConfirmationToken> findByTokenHash(String hash) {
        return jpaRepository.findByTokenHash(hash)
                .map(mapper::toDomain);
    }

    @Override
    public void deleteExpired(LocalDateTime now) {
        jpaRepository.deleteByExpiresAtBefore(now);
    }
}