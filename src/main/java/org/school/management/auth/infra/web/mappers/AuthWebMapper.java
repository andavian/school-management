package org.school.management.auth.infra.web.mappers;

import org.school.management.auth.application.dto.requests.*;
import org.school.management.auth.application.dto.responses.CreateStudentResponse;
import org.school.management.auth.application.dto.responses.CreateTeacherResponse;
import org.school.management.auth.application.dto.responses.LoginResponse;
import org.school.management.auth.application.dto.responses.UserResponse;
import org.mapstruct.*;
import org.school.management.auth.infra.web.dto.requests.*;
import org.school.management.auth.infra.web.dto.response.*;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface AuthWebMapper {

    // API Requests → Application Requests
    CreateUserRequest toApplicationDto(CreateUserApiRequest apiRequest);

    LoginRequest toApplicationDto(LoginApiRequest apiRequest);

    CreateTeacherRequest toApplicationDto(CreateTeacherApiRequest apiRequest);

    CreateStudentRequest toApplicationDto(CreateStudentApiRequest apiRequest);

    @Mapping(target = "userId", ignore = true)
    ChangePasswordRequest toApplicationDto(ChangePasswordApiRequest apiRequest);

    UserApiResponse toApiResponse(UserResponse userResponse);

    @Mapping(source = "token", target = "accessToken")
    @Mapping(constant = "Bearer", target = "tokenType")
    LoginApiResponse toApiResponse(LoginResponse loginResponse);

    CreateTeacherApiResponse toApiResponse(CreateTeacherResponse response);

    CreateStudentApiResponse toApiResponse(CreateStudentResponse response);

    // Utility methods para records
    default SuccessApiResponse createSuccessResponse(String message) {
        return new SuccessApiResponse(
                true,
                message,
                LocalDateTime.now().toString()
        );
    }

    default ErrorApiResponse createErrorResponse(String message, String errorCode, String path) {
        return new ErrorApiResponse(
                false,
                message,
                errorCode,
                LocalDateTime.now(),
                path,
                java.util.List.of()
        );
    }
}

