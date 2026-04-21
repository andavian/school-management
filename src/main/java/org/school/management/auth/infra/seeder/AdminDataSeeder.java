package org.school.management.auth.infra.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.domain.valueobject.RoleName;
import org.school.management.auth.infra.persistence.entity.RoleEntity;
import org.school.management.auth.infra.persistence.entity.UserEntity;
import org.school.management.auth.infra.persistence.repository.RoleJpaRepository;
import org.school.management.auth.infra.persistence.repository.UserJpaRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Seeder para usuarios de gestión (Super Admin, Admin, Principal).
 * * @Order(1) - Se ejecuta primero para que otros seeders encuentren al admin.
 */
@Component
@Profile("dev")
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class AdminDataSeeder implements ApplicationRunner {

    private final UserJpaRepository userRepository;
    private final RoleJpaRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // UUIDs fijos para consistencia entre inicios
    public static final UUID SUPER_ADMIN_ID = UUID.fromString("a0000000-0000-0000-0000-000000000001");
    public static final UUID ADMIN_ID       = UUID.fromString("a0000000-0000-0000-0000-000000000002");
    public static final UUID PRINCIPAL_ID   = UUID.fromString("a0000000-0000-0000-0000-000000000003");

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("=".repeat(80));
        log.info("Starting Administrative Users Seeder...");
        log.info("=".repeat(80));

        try {
            // Aseguramos que los roles existan (si no los crea Flyway)
            RoleEntity superRole = resolveOrCreateRole(RoleName.superAdmin());
            RoleEntity adminRole = resolveOrCreateRole(RoleName.admin());
            RoleEntity principalRole = resolveOrCreateRole(RoleName.principal());

            seedAdminUsers(superRole, adminRole, principalRole);

            log.info("Administrative Users Seeder completed successfully!");
            log.info("=".repeat(80));
        } catch (Exception e) {
            log.error("Error seeding administrative users", e);
            throw e;
        }
    }

    private void seedAdminUsers(RoleEntity superRole, RoleEntity adminRole, RoleEntity principalRole) {

        List<AdminUserData> users = List.of(
                new AdminUserData(SUPER_ADMIN_ID, "10000001", "Admin123!", Set.of(superRole, adminRole), "SUPER ADMIN"),
                new AdminUserData(ADMIN_ID, "10000002", "Admin123!", Set.of(adminRole), "ADMINISTRATIVE"),
                new AdminUserData(PRINCIPAL_ID, "10000003", "Principal123!", Set.of(principalRole), "DIRECTOR/PRINCIPAL")
        );

        for (AdminUserData data : users) {
            if (!userRepository.existsByDni(data.dni())) {
                UserEntity user = UserEntity.builder()
                        .userId(data.id())
                        .dni(data.dni())
                        .password(passwordEncoder.encode(data.password()))
                        .roles(data.roles())
                        .active(true)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

                userRepository.save(user);
                log.info("  ✓ Created {} User - DNI: {}", data.label(), data.dni());
            } else {
                log.info("  ○ {} User (DNI {}) already exists. Skipping.", data.label(), data.dni());
            }
        }
    }

    private RoleEntity resolveOrCreateRole(RoleName roleName) {
        String dbName = roleName.toDbName(); // Ahora devuelve "ADMIN", "SUPER_ADMIN", etc.

        return roleRepository.findByName(dbName)
                .orElseGet(() -> {
                    log.info("  ! Role {} not found in DB, creating it...", dbName);
                    RoleEntity newRole = new RoleEntity();
                    // Si tu RoleEntity usa UUID, asegúrate de generarlo si no lo hace la DB
                    newRole.setRoleId(UUID.randomUUID());
                    newRole.setName(dbName);
                    newRole.setCreatedAt(LocalDateTime.now());
                    return roleRepository.save(newRole);
                });
    }

    private record AdminUserData(
            UUID id,
            String dni,
            String password,
            Set<RoleEntity> roles,
            String label
    ) {}
}