package org.school.management.auth.infra.api.mapper;

public interface AuthWebMapper {
    // API Request → Application Request (mapeo automático por nombres)
    CreateUserRequest toApplicationDto(CreateUserApiRequest apiRequest);

    LoginRequest toApplicationDto(LoginApiRequest apiRequest);

    ChangePasswordRequest toApplicationDto(ChangePasswordApiRequest apiRequest);

    // Application Response → API Response (mapeo automático)
    UserApiResponse toApiResponse(UserResponse userResponse);

    LoginApiResponse toApiResponse(LoginResponse loginResponse);

    // Custom success response
    @Mapping(constant = "true", target = "success")
    SuccessApiResponse toSuccessResponse(String message);
}
}
