package org.school.management.auth.application.mappers;

import org.school.management.auth.domain.model.BlacklistedToken;
import org.school.management.auth.domain.valueobject.BlacklistedTokenId;
import org.school.management.auth.application.dto.*;
import org.mapstruct.*;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface BlacklistedTokenApplicationMapper {

    @Mapping(source = "id.value", target = "id")
    @Mapping(expression = "java(token.isExpired())", target = "isExpired")
    @Mapping(expression = "java(token.isActive())", target = "isActive")
    BlacklistedTokenResponse toResponse(BlacklistedToken token);

    default BlacklistedToken toDomain(BlacklistTokenRequest request) {
        return BlacklistedToken.create(
                request.getTokenHash(),
                request.getTokenType(),
                request.getExpiresAt(),
                request.getReason(),
                request.getUserEmail()
        );
    }
}