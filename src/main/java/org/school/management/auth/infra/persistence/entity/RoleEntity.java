package org.school.management.auth.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "roles", indexes = {
        @Index(name = "idx_role_name", columnList = "name", unique = true)
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RoleEntity {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "role_id", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID roleId;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name; // "ADMIN", "TEACHER", "STUDENT", etc.

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ============================================
    // RELACIÓN INVERSA (OPCIONAL pero recomendada)
    // ============================================
    @ManyToMany(mappedBy = "roles")
    @Builder.Default
    private Set<UserEntity> users = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (roleId == null) {
            roleId = UUID.randomUUID();
        }
    }
}