package org.school.management.auth.infra.web.mappers;

import org.school.management.auth.infra.web.dto.*;
import org.school.management.auth.application.dto.*;
import org.mapstruct.*;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface AuthWebMapper {

    // API Requests → Application Requests
    CreateUserRequest toApplicationDto(CreateUserApiRequest apiRequest);

    LoginRequest toApplicationDto(LoginApiRequest apiRequest);

    @Mapping(source = "currentPassword", target = "currentPassword")
    @Mapping(source = "newPassword", target = "newPassword")
    @Mapping(target = "userId", ignore = true) // Se setea en el controller desde el token
    ChangePasswordRequest toApplicationDto(ChangePasswordApiRequest apiRequest);

    // Application Responses → API Responses
    UserApiResponse toApiResponse(UserResponse userResponse);

    @Mapping(source = "token", target = "accessToken")
    @Mapping(constant = "Bearer", target = "tokenType")
    LoginApiResponse toApiResponse(LoginResponse loginResponse);

    // Utility methods
    @Mapping(constant = "true", target = "success")
    @Mapping(expression = "java(java.time.LocalDateTime.now().toString())", target = "timestamp")
    SuccessApiResponse toSuccessResponse(String message);

    default SuccessApiResponse createSuccessResponse(String message) {
        return SuccessApiResponse.builder()
                .success(true)
                .message(message)
                .timestamp(LocalDateTime.now().toString())
                .build();
    }
}
