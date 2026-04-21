package org.school.management.auth.infra.persistence.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.school.management.auth.domain.model.ConfirmationToken;
import org.school.management.auth.domain.valueobject.ConfirmationTokenId;
import org.school.management.auth.infra.persistence.entity.ConfirmationTokenEntity;
import org.school.management.shared.person.domain.valueobject.Dni;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ConfirmationTokenPersistenceMapper {

    default ConfirmationTokenEntity toEntity(ConfirmationToken domain) {
        ConfirmationTokenEntity entity = new ConfirmationTokenEntity();

        entity.setId(domain.getId().value());
        entity.setUserDni(domain.getUserDni().value());
        entity.setTokenHash(domain.getTokenHash());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setExpiresAt(domain.getExpiresAt());
        entity.setUsedAt(domain.getUsedAt());

        return entity;
    }

    default ConfirmationToken toDomain(ConfirmationTokenEntity entity) {
        return ConfirmationToken.builder()
                .id(ConfirmationTokenId.of(entity.getId()))
                .userDni(Dni.of(entity.getUserDni()))
                .tokenHash(entity.getTokenHash())
                .createdAt(entity.getCreatedAt())
                .expiresAt(entity.getExpiresAt())
                .usedAt(entity.getUsedAt())
                .build();
    }
}