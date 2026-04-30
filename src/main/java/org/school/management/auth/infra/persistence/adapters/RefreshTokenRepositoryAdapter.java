package org.school.management.auth.infra.persistence.adapters;

import lombok.RequiredArgsConstructor;
import org.school.management.auth.domain.model.RefreshToken;
import org.school.management.auth.domain.repository.RefreshTokenRepository;
import org.school.management.auth.infra.persistence.mappers.RefreshTokenPersistenceMapper;
import org.school.management.auth.infra.persistence.repository.RefreshTokenJpaRepository;
import org.school.management.shared.person.domain.valueobject.Dni;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepository {

    private final RefreshTokenJpaRepository jpaRepository;
    private final RefreshTokenPersistenceMapper mapper;

    @Override
    public RefreshToken save(RefreshToken token) {
        return mapper.toDomain(
                jpaRepository.save(mapper.toEntity(token))
        );
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(String hash) {
        return jpaRepository.findByTokenHash(hash)
                .map(mapper::toDomain);
    }

    @Override
    public List<RefreshToken> findAllByUserDni(Dni dni) {
        return jpaRepository.findByUserDni(dni.value())
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void revokeAllByUserDni(Dni dni) {
        jpaRepository.revokeAllActiveByUserDni(
                dni.value(),
                LocalDateTime.now()
        );
    }

    @Override
    public void revoke(RefreshToken token) {
        // Ejecuta un UPDATE directo para evitar problemas de sesión
        jpaRepository.revokeToken(
                token.getId().value(),                // UUID del token
                token.getReplacedByTokenHash(),       // hash del nuevo token
                LocalDateTime.now()                   // momento de revocación
        );
    }
}