package org.school.management.auth.application.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.school.management.auth.application.dto.requests.CreateStudentRequest;
import org.school.management.auth.application.dto.requests.CreateTeacherRequest;
import org.school.management.auth.application.dto.responses.LoginResponse;
import org.school.management.auth.application.dto.responses.UserResponse;
import org.school.management.auth.domain.model.User;
import org.school.management.auth.domain.valueobject.HashedPassword;
import org.school.management.auth.domain.valueobject.PlainPassword;
import org.school.management.auth.domain.valueobject.RoleName;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.shared.domain.valueobjects.DNI;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface AuthApplicationMapper {

    // User → UserResponse - ACTUALIZADO
    @Mapping(source = "userId.value", target = "userId")
    @Mapping(source = "dni.value", target = "dni")                              // ← NUEVO
    @Mapping(source = "roles", target = "roles", qualifiedByName = "rolesToStrings")
    UserResponse toUserResponse(User user);

    // User + Token → LoginResponse
    @Mapping(source = "user", target = "user")
    @Mapping(source = "token", target = "token")
    @Mapping(source = "refreshToken", target = "refreshToken")
    @Mapping(constant = "3600L", target = "expiresIn")
    @Mapping(constant = "Bearer", target = "tokenType")
    LoginResponse toLoginResponse(User user, String token, String refreshToken);

    // String conversions para Use Cases - ACTUALIZADOS
    default DNI toDni(String dni) {                                             // ← NUEVO
        return DNI.of(dni);
    }

    default PlainPassword toPlainPassword(String password) {
        return PlainPassword.of(password);
    }

    default Set<RoleName> toRoleNames(Set<String> roles) {
        return roles.stream()
                .map(RoleName::of)
                .collect(Collectors.toSet());
    }

    default UserId toUserId(String userId) {
        return UserId.from(userId);
    }

    // Helper methods
    @Named("rolesToStrings")
    default Set<String> rolesToStrings(Set<RoleName> roles) {
        return roles.stream()
                .map(RoleName::getName)
                .collect(Collectors.toSet());
    }

    // Factory methods para User - ACTUALIZADOS
    default User createTeacherFromRequest(CreateTeacherRequest request,
                                          PlainPassword password,
                                          HashedPassword.PasswordEncoder encoder) {
        DNI dni = DNI.of(request.dni());
        Set<RoleName> roles = Set.of(RoleName.teacher());

        User teacher = User.create(dni, password, roles, encoder);
        teacher.deactivate(); // Inician inactivos hasta confirmar email

        return teacher;
    }

    default User createStudentFromRequest(CreateStudentRequest request,
                                          PlainPassword password,
                                          HashedPassword.PasswordEncoder encoder) {
        DNI dni = DNI.of(request.dni());
        Set<RoleName> roles = Set.of(RoleName.student());


        return User.create(dni, password, roles, encoder);

        }
    }
