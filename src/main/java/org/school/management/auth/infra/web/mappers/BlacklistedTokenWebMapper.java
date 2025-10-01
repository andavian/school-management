package org.school.management.auth.infra.web.mappers;

import org.school.management.auth.application.dto.requests.BlacklistTokenRequest;
import org.school.management.auth.application.dto.responses.BlacklistedTokenResponse;
import org.mapstruct.*;
import org.school.management.auth.infra.web.dto.requests.BlacklistTokenApiRequest;
import org.school.management.auth.infra.web.dto.response.BlacklistedTokenApiResponse;

@Mapper(componentModel = "spring")
public interface BlacklistedTokenWebMapper {

    BlacklistTokenRequest toApplicationDto(BlacklistTokenApiRequest apiRequest);

    BlacklistedTokenApiResponse toApiResponse(BlacklistedTokenResponse response);
}
