package org.school.management.auth.infra.web.mappers;

import org.school.management.auth.infra.web.dto.*;
import org.school.management.auth.application.dto.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BlacklistedTokenWebMapper {

    BlacklistTokenRequest toApplicationDto(BlacklistTokenApiRequest apiRequest);

    BlacklistedTokenApiResponse toApiResponse(BlacklistedTokenResponse response);
}
