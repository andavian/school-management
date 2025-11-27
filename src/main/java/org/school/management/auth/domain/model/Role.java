package org.school.management.auth.domain.model;

import lombok.*;
import org.school.management.auth.domain.valueobject.RoleId;
import org.school.management.auth.domain.valueobject.RoleName;

import java.time.LocalDateTime;

@Getter
@Setter(AccessLevel.PRIVATE)
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Role {

    @EqualsAndHashCode.Include
    private RoleId roleId;

    private RoleName name;

    private LocalDateTime createdAt;

    // ============================================
    // Factory Methods
    // ============================================
    public static Role create(RoleName name) {
        return new Role(
                RoleId.generate(),
                name,
                LocalDateTime.now()
        );
    }

    public static Role reconstruct(RoleId id, RoleName name, LocalDateTime createdAt) {
        return new Role(id, name, createdAt);
    }

    // ============================================
    // Private Constructor for Lombok Builder
    // ============================================
    private Role(RoleId roleId, RoleName name, LocalDateTime createdAt) {
        this.roleId = roleId;
        this.name = name;
        this.createdAt = createdAt;
    }
}