package org.school.management.auth.infra.persistence.mappers;

import org.school.management.auth.domain.model.BlacklistedToken;
import org.school.management.auth.domain.valueobject.BlacklistedTokenId;
import org.school.management.auth.infra.persistence.entity.BlacklistedTokenEntity;
import org.mapstruct.*;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface BlacklistedTokenPersistenceMapper {

    @Mapping(source = "id.value", target = "id")
    @Mapping(source = "tokenType", target = "tokenType", qualifiedByName = "stringToTokenType")
    BlacklistedTokenEntity toEntity(BlacklistedToken domain);

    @Mapping(source = "id", target = "id", qualifiedByName = "uuidToBlacklistedTokenId")
    @Mapping(source = "tokenType", target = "tokenType", qualifiedByName = "tokenTypeToString")
    BlacklistedToken toDomain(BlacklistedTokenEntity entity);

    @Named("stringToTokenType")
    default BlacklistedTokenEntity.TokenType stringToTokenType(String tokenType) {
        return BlacklistedTokenEntity.TokenType.valueOf(tokenType);
    }

    @Named("tokenTypeToString")
    default String tokenTypeToString(BlacklistedTokenEntity.TokenType tokenType) {
        return tokenType.name();
    }

    @Named("uuidToBlacklistedTokenId")
    default BlacklistedTokenId uuidToBlacklistedTokenId(UUID uuid) {
        return BlacklistedTokenId.from(uuid);
    }
}