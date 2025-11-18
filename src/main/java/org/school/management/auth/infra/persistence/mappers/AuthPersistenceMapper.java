package org.school.management.auth.infra.persistence.mappers;

import org.school.management.auth.domain.model.User;
import org.school.management.auth.domain.valueobject.*;
import org.school.management.auth.infra.persistence.entity.UserEntity;
import org.school.management.shared.domain.valueobjects.Email;
import org.school.management.shared.domain.valueobjects.DNI;
import org.mapstruct.*;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface AuthPersistenceMapper {

    // Domain → Entity
    @Mapping(source = "userId.value", target = "userId")
    @Mapping(source = "dni.value", target = "dni")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "roles", target = "roles", qualifiedByName = "rolesToString")
    UserEntity toEntity(User user);

    // Entity → Domain
    @Mapping(source = "userId", target = "userId", qualifiedByName = "uuidToUserId")
    @Mapping(source = "dni", target = "dni", qualifiedByName = "stringToDni")
    @Mapping(source = "password", target = "password", qualifiedByName = "stringToHashedPassword")
    @Mapping(source = "roles", target = "roles", qualifiedByName = "stringToRoles")
    User toDomain(UserEntity entity);

    // Helper methods
    @Named("stringToDni")
    default DNI stringToDni(String dni) {
        return dni != null ? DNI.of(dni) : null;
    }

    @Named("emailToString")
    default String emailToString(Email email) {
        return email != null ? email.getValue() : null;
    }

    @Named("stringToEmail")
    default Email stringToEmail(String email) {
        return email != null && !email.trim().isEmpty() ? Email.of(email) : null;
    }

    @Named("rolesToString")
    default String rolesToString(Set<RoleName> roles) {
        return roles.stream()
                .map(RoleName::getName)
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

    @Named("stringToHashedPassword")
    default HashedPassword stringToHashedPassword(String hashedPassword) {
        return HashedPassword.of(hashedPassword);
    }
}