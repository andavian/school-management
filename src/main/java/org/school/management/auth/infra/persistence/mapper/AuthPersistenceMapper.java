package org.school.management.auth.infra.persistence.mapper;

import org.school.management.auth.domain.model.User;
import org.school.management.auth.domain.valueobject.*;
import org.school.management.auth.infra.persistence.entity.UserEntity;
import org.mapstruct.*;
import org.school.management.shared.domain.valueobjects.Email;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface AuthPersistenceMapper {
    // Domain → Entity (para save)
    @Mapping(source = "id.value", target = "id")
    @Mapping(source = "email.value", target = "email")
    @Mapping(source = "password.value", target = "password")
    @Mapping(source = "roles", target = "roles", qualifiedByName = "rolesToString")
    UserEntity toEntity(User user);

    // Entity → Domain (para queries)
    @Mapping(source = "id", target = "id", qualifiedByName = "uuidToUserId")
    @Mapping(source = "email", target = "email", qualifiedByName = "stringToEmail")
    @Mapping(source = "password", target = "password", qualifiedByName = "stringToHashedPassword")
    @Mapping(source = "roles", target = "roles", qualifiedByName = "stringToRoles")
    User toDomain(UserEntity entity);

    // Helper methods para conversiones complejas
    @Named("rolesToString")
    default String rolesToString(Set<RoleName> roles) {
        return roles.stream()
                .map(RoleName::getValue)
                .collect(Collectors.joining(","));
    }

    @Named("stringToRoles")
    default Set<RoleName> stringToRoles(String rolesString) {
        if (rolesString == null || rolesString.trim().isEmpty()) {
            return Set.of();
        }

        return Set.of(rolesString.split(","))
                .stream()
                .map(String::trim)
                .map(RoleName::of)
                .collect(Collectors.toSet());
    }

    @Named("uuidToUserId")
    default UserId uuidToUserId(UUID uuid) {
        return UserId.from(uuid);
    }

    @Named("stringToEmail")
    default Email stringToEmail(String email) {
        return Email.of(email);
    }

    @Named("stringToHashedPassword")
    default HashedPassword stringToHashedPassword(String hashedPassword) {
        return HashedPassword.of(hashedPassword);
    }
}

