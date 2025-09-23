package org.school.management.auth.application.mapper;

import org.school.management.auth.domain.model.*;
import org.school.management.auth.application.dto.*;
import org.school.management.auth.domain.valueobject.*;
import org.mapstruct.*;
import org.school.management.shared.domain.valueobjects.Email;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface AuthApplicationMapper {
    // User → UserResponse
    @Mapping(source = "id.value", target = "id")
    @Mapping(source = "email.value", target = "email")
    @Mapping(source = "roles", target = "roles", qualifiedByName = "rolesToStrings")
    UserResponse toUserResponse(User user);

    // User + Token → LoginResponse
    @Mapping(source = "user", target = "user")
    @Mapping(source = "token", target = "token")
    @Mapping(source = "refreshToken", target = "refreshToken")
    @Mapping(constant = "3600L", target = "expiresIn")
    LoginResponse toLoginResponse(User user, String token, String refreshToken);

    // String → Email
    default Email toEmail(String email) {
        return Email.of(email);
    }

    // String → PlainPassword
    default PlainPassword toPlainPassword(String password) {
        return PlainPassword.of(password);
    }

    // Set<String> → Set<RoleName>
    default Set<RoleName> toRoleNames(Set<String> roles) {
        return roles.stream()
                .map(RoleName::of)
                .collect(Collectors.toSet());
    }

    // Set<RoleName> → Set<String>
    @Named("rolesToStrings")
    default Set<String> rolesToStrings(Set<RoleName> roles) {
        return roles.stream()
                .map(RoleName::getName)
                .collect(Collectors.toSet());
    }

    // UUID → String
    default String map(java.util.UUID uuid) {
        return uuid.toString();
    }
}
