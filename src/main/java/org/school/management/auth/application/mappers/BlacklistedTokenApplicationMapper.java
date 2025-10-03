package org.school.management.auth.application.mappers;

import org.school.management.auth.application.dto.requests.BlacklistTokenRequest;
import org.school.management.auth.application.dto.responses.BlacklistedTokenResponse;
import org.school.management.auth.domain.model.BlacklistedToken;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BlacklistedTokenApplicationMapper {

    @Mapping(source = "id.value", target = "id")
    @Mapping(expression = "java(token.isExpired())", target = "isExpired")
    @Mapping(expression = "java(token.isActive())", target = "isActive")
    BlacklistedTokenResponse toResponse(BlacklistedToken token);

    default BlacklistedToken toDomain(BlacklistTokenRequest request) {
        return BlacklistedToken.create(
                request.token(),
                request.tokenType(),
                request.expiresAt(),
                request.reason(),
                request.userDni()
        );
    }
}