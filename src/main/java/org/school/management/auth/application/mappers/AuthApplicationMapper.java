package org.school.management.auth.application.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.school.management.auth.application.dto.responses.LoginResponse;
import org.school.management.auth.application.dto.responses.UserResponse;
import org.school.management.auth.domain.model.Role;
import org.school.management.auth.domain.model.User;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.shared.person.domain.valueobject.Dni;
import org.school.management.auth.domain.valueobject.PlainPassword;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper de la capa de aplicación de {@code auth/}.
 *
 * <p>Responsabilidad exclusiva: mapeos relacionados con autenticación,
 * perfil y sesión. La creación de usuarios ({@code teachers/}, {@code students/},
 * {@code parents/}) es responsabilidad de cada BC — sus use cases llaman
 * directamente a {@code CreateUserUseCase} sin pasar por este mapper.</p>
 */
@Mapper(componentModel = "spring")
public interface AuthApplicationMapper {

    @Mapping(source = "userId.value", target = "userId")
    @Mapping(source = "dni.value", target = "dni")
    @Mapping(source = "roles", target = "roles", qualifiedByName = "rolesToStrings")
    UserResponse toUserResponse(User user);

    @Mapping(source = "user", target = "user")
    @Mapping(source = "token", target = "token")
    @Mapping(source = "refreshToken", target = "refreshToken")
    @Mapping(constant = "3600L", target = "expiresIn")
    @Mapping(constant = "Bearer", target = "tokenType")
    LoginResponse toLoginResponse(User user, String token, String refreshToken);

    default Dni toDni(String dni) {
        return Dni.of(dni);
    }

    default PlainPassword toPlainPassword(String password) {
        return PlainPassword.of(password);
    }

    default UserId toUserId(String userId) {
        return UserId.from(userId);
    }

    @Named("rolesToStrings")
    default Set<String> rolesToStrings(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return Set.of();
        }
        return roles.stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
    }
}