package org.school.management.auth.infra.persistence.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.school.management.auth.domain.model.Role;
import org.school.management.auth.domain.model.User;
import org.school.management.auth.domain.valueobject.HashedPassword;
import org.school.management.auth.domain.valueobject.RoleId;
import org.school.management.auth.domain.valueobject.RoleName;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.auth.infra.persistence.entity.RoleEntity;
import org.school.management.auth.infra.persistence.entity.UserEntity;
import org.school.management.shared.person.domain.valueobject.Dni;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class AuthPersistenceMapper {

    // 1. HELPERS BÁSICOS (@Named)
    @Named("userIdToUuid")
    protected UUID userIdToUuid(UserId userId) {
        return userId != null ? userId.getValue() : null;
    }

    @Named("dniToString")
    protected String dniToString(Dni dni) {
        return dni != null ? dni.value() : null;
    }

    @Named("hashedPasswordToString")
    protected String hashedPasswordToString(HashedPassword password) {
        return password != null ? password.getValue() : null;
    }

    // 2. HELPERS PARA COLECCIONES
    @Named("rolesToRoleEntities")
    protected Set<RoleEntity> rolesToRoleEntities(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return Set.of();
        }
        return roles.stream()
                .map(this::roleToRoleEntity)
                .collect(Collectors.toSet());
    }

    // 3. MÉTODO ABSTRACTO (MapStruct lo genera)
    @Mapping(source = "userId", target = "userId", qualifiedByName = "userIdToUuid")
    @Mapping(source = "dni", target = "dni", qualifiedByName = "dniToString")
    @Mapping(source = "password", target = "password", qualifiedByName = "hashedPasswordToString")
    @Mapping(source = "roles", target = "roles", qualifiedByName = "rolesToRoleEntities")
    public abstract UserEntity toEntity(User user);

    // 4. MÉTODOS MANUALES (tú implementas)

    public User toDomain(UserEntity entity) {
        if (entity == null) return null;

        return User.reconstruct(
                uuidToUserId(entity.getUserId()),
                stringToDni(entity.getDni()),
                stringToHashedPassword(entity.getPassword()),
                roleEntitiesToRoles(entity.getRoles()),
                entity.getActive(),
                entity.getCreatedAt(),
                entity.getLastLoginAt(),
                entity.getUpdatedAt()
        );
    }

    // ✅ NOMBRE CORREGIDO: roleToRoleDomain (no toRoleDomain)
    public Role roleToRoleDomain(RoleEntity entity) {
        if (entity == null) return null;
        return Role.reconstruct(
                uuidToRoleId(entity.getRoleId()),
                stringToRoleName(entity.getName()),
                entity.getCreatedAt()
        );
    }

    public RoleEntity roleToRoleEntity(Role role) {
        if (role == null) return null;
        RoleEntity entity = new RoleEntity();
        entity.setRoleId(role.getRoleId().getValue());
        entity.setName(role.getName().getName());
        entity.setCreatedAt(role.getCreatedAt());
        return entity;
    }

    // 5. HELPERS INVERSOS (sin @Named)
    protected Set<Role> roleEntitiesToRoles(Set<RoleEntity> roleEntities) {
        if (roleEntities == null || roleEntities.isEmpty()) {
            return Set.of();
        }
        return roleEntities.stream()
                .map(this::roleToRoleDomain) // Usa el método corregido
                .collect(Collectors.toSet());
    }

    protected UserId uuidToUserId(UUID uuid) {
        return uuid != null ? UserId.from(uuid) : null;
    }

    protected Dni stringToDni(String dni) {
        return dni != null ? Dni.of(dni) : null;
    }

    protected HashedPassword stringToHashedPassword(String hashedPassword) {
        return hashedPassword != null ? HashedPassword.of(hashedPassword) : null;
    }

    protected RoleId uuidToRoleId(UUID uuid) {
        return uuid != null ? RoleId.from(uuid) : null;
    }

    protected RoleName stringToRoleName(String name) {
        return name != null ? RoleName.of(name) : null;
    }
}
/*
@Mapper(componentModel = "spring")
public interface AuthPersistenceMapper {

    // --- Mapeo de User <-> UserEntity ---

    @Mapping(source = "userId.value", target = "userId")
    @Mapping(source = "dni.value", target = "dni")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "roles", target = "roles", qualifiedByName = "rolesToRoleEntities")
    UserEntity toEntity(User user);

    @Mapping(source = "userId", target = "userId", qualifiedByName = "uuidToUserId")
    @Mapping(source = "dni", target = "dni", qualifiedByName = "stringToDni")
    @Mapping(source = "password", target = "password", qualifiedByName = "stringToHashedPassword")
    @Mapping(source = "roles", target = "roles", qualifiedByName = "roleEntitiesToRoles")
    User toDomain(UserEntity entity);

    // --- Mapeo de Roles ---

    @Named("roleEntitiesToRoles")
    default Set<Role> roleEntitiesToRoles(Set<RoleEntity> roleEntities) {
        if (roleEntities == null || roleEntities.isEmpty()) {
            return Set.of();
        }
        return roleEntities.stream()
                .map(entity -> Role.reconstruct(
                        RoleId.from(entity.getRoleId()),
                        RoleName.of(entity.getName()),
                        entity.getCreatedAt()
                ))
                .collect(Collectors.toSet());
    }

    @Named("rolesToRoleEntities")
    default Set<RoleEntity> rolesToRoleEntities(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return Set.of();
        }
        return roles.stream()
                .map(role -> RoleEntity.builder()
                        .roleId(role.getRoleId().getValue())
                        .name(roleNameToString(role.getName()))
                        .createdAt(role.getCreatedAt())
                        .build())
                .collect(Collectors.toSet());
    }

    // --- Mapeo Individual de Role <-> RoleEntity ---

    @Mapping(source = "roleId.value", target = "roleId")
    @Mapping(source = "name", target = "name", qualifiedByName = "roleNameToString") // FIX #1
    RoleEntity roleToRoleEntity(Role role);

    @Mapping(source = "roleId", target = "roleId", qualifiedByName = "uuidToRoleId")
    @Mapping(source = "name", target = "name", qualifiedByName = "stringToRoleName") // FIX #2
    Role roleToRoleDomain(RoleEntity entity);

    // --- Qualifiers ---

    @Named("roleNameToString")
    default String roleNameToString(RoleName roleName) {
        return roleName != null ? roleName.getName() : null;
    }

    @Named("stringToRoleName")
    default RoleName stringToRoleName(String name) {
        return name != null ? RoleName.of(name) : null;
    }


    @Named("stringToDni")
    default DNI stringToDni(String dni) {
        return dni != null ? DNI.of(dni) : null;
    }

    @Named("uuidToUserId")
    default UserId uuidToUserId(UUID uuid) {
        return UserId.from(uuid);
    }

    @Named("uuidToRoleId")
    default RoleId uuidToRoleId(UUID uuid) {
        return RoleId.from(uuid);
    }

    @Named("stringToHashedPassword")
    default HashedPassword stringToHashedPassword(String hashedPassword) {
        return HashedPassword.of(hashedPassword);
    }
}*/
