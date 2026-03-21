package org.school.management.auth.infra.web.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.school.management.auth.application.dto.requests.ChangePasswordRequest;
import org.school.management.auth.application.dto.requests.LoginRequest;
import org.school.management.auth.application.dto.responses.LoginResponse;
import org.school.management.auth.application.dto.responses.UserResponse;
import org.school.management.auth.infra.web.dto.requests.ChangePasswordApiRequest;
import org.school.management.auth.infra.web.dto.requests.LoginApiRequest;
import org.school.management.auth.infra.web.dto.response.ErrorApiResponse;
import org.school.management.auth.infra.web.dto.response.LoginApiResponse;
import org.school.management.auth.infra.web.dto.response.SuccessApiResponse;
import org.school.management.auth.infra.web.dto.response.UserApiResponse;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Mapper web de {@code auth/}.
 *
 * <p>Responsabilidad exclusiva: traducir entre DTOs web y DTOs de aplicación
 * para login, perfil y cambio de password.</p>
 *
 * <p>Los endpoints de creación de teachers y students fueron eliminados del
 * {@code AdminController} y delegados a sus respectivos BCs. Los mappings
 * {@code CreateTeacherApiRequest}, {@code CreateStudentApiRequest} y sus
 * responses asociados fueron eliminados en consecuencia.</p>
 */
@Mapper(componentModel = "spring")
public interface AuthWebMapper {

    LoginRequest toApplicationDto(LoginApiRequest apiRequest);

    @Mapping(target = "userId", ignore = true)
    ChangePasswordRequest toApplicationDto(ChangePasswordApiRequest apiRequest);

    @Mapping(target = "isActive", source = "active")
    UserApiResponse toApiResponse(UserResponse userResponse);

    @Mapping(source = "token", target = "accessToken")
    @Mapping(constant = "Bearer", target = "tokenType")
    LoginApiResponse toApiResponse(LoginResponse loginResponse);

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
                List.of()
        );
    }
}