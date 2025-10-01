package org.school.management.auth.application.mappers;

import org.school.management.auth.application.dto.requests.CreateStudentRequest;
import org.school.management.auth.application.dto.requests.CreateTeacherRequest;
import org.school.management.auth.application.dto.responses.LoginResponse;
import org.school.management.auth.application.dto.responses.UserResponse;
import org.school.management.auth.domain.model.User;
import org.school.management.auth.domain.valueobject.*;
import org.school.management.shared.domain.valueobjects.Email;
import org.mapstruct.*;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface AuthApplicationMapper {

    // User → UserResponse (para consultas)
    @Mapping(source = "userId.value", target = "userId")
    @Mapping(source = "email.value", target = "email")
    @Mapping(source = "roles", target = "roles", qualifiedByName = "rolesToStrings")
    UserResponse toUserResponse(User user);

    // User + Token → LoginResponse (para login)
    @Mapping(source = "user", target = "user")
    @Mapping(source = "token", target = "token")
    @Mapping(source = "refreshToken", target = "refreshToken")
    @Mapping(constant = "3600L", target = "expiresIn")
    @Mapping(constant = "Bearer", target = "tokenType")
    LoginResponse toLoginResponse(User user, String token, String refreshToken);

    // Strings → Domain Value Objects (para Use Cases)
    default Email toEmail(String email) {
        return Email.of(email);
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

    default User createTeacherFromRequest(CreateTeacherRequest request,
                                          PlainPassword password,
                                          HashedPassword.PasswordEncoder encoder) {
        Email email = Email.of(request.email()); // Usar método accessor del record
        Set<RoleName> roles = Set.of(RoleName.teacher());

        User teacher = User.create(email, password, roles, encoder);
        teacher.deactivate(); // Los profesores inician inactivos

        return teacher;
    }

    default User createStudentFromRequest(CreateStudentRequest request,
                                          PlainPassword password,
                                          HashedPassword.PasswordEncoder encoder) {
        Email email = Email.of(request.email()); // Usar método accessor del record
        Set<RoleName> roles = Set.of(RoleName.student());

        return User.create(email, password, roles, encoder);
    }

    // Helper methods
    @Named("rolesToStrings")
    default Set<String> rolesToStrings(Set<RoleName> roles) {
        return roles.stream()
                .map(RoleName::getName)
                .collect(Collectors.toSet());
    }
}