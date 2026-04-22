package org.school.management.auth.infra.persistence.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.school.management.auth.domain.model.RefreshToken;
import org.school.management.auth.domain.valueobject.RefreshTokenId;
import org.school.management.auth.infra.persistence.entity.RefreshTokenEntity;
import org.school.management.shared.person.domain.valueobject.Dni;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RefreshTokenPersistenceMapper {

    default RefreshTokenEntity toEntity(RefreshToken domain) {
        RefreshTokenEntity entity = new RefreshTokenEntity();

        entity.setId(domain.getId().value());
        entity.setUserDni(domain.getUserDni().value());
        entity.setTokenHash(domain.getTokenHash());
        entity.setIssuedAt(domain.getIssuedAt());
        entity.setExpiresAt(domain.getExpiresAt());
        entity.setRevokedAt(domain.getRevokedAt());
        entity.setReplacedByTokenHash(domain.getReplacedByTokenHash());
        entity.setDeviceInfo(domain.getDeviceInfo());
        entity.setIpAddress(domain.getIpAddress());
        entity.setUserAgent(domain.getUserAgent());

        return entity;
    }

    default RefreshToken toDomain(RefreshTokenEntity entity) {
        return RefreshToken.builder()
                .id(RefreshTokenId.of(entity.getId()))
                .userDni(Dni.of(entity.getUserDni()))
                .tokenHash(entity.getTokenHash())
                .issuedAt(entity.getIssuedAt())
                .expiresAt(entity.getExpiresAt())
                .revokedAt(entity.getRevokedAt())
                .replacedByTokenHash(entity.getReplacedByTokenHash())
                .deviceInfo(entity.getDeviceInfo())
                .ipAddress(entity.getIpAddress())
                .userAgent(entity.getUserAgent())
                .build();
    }
}