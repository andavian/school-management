/*
package org.school.management.auth.infra.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.infra.persistence.entity.RoleEntity;
import org.school.management.auth.infra.persistence.repository.RoleJpaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RoleSeederConfig {

    private final RoleJpaRepository roleRepository;

    @Bean
    @Profile("dev")
    public CommandLineRunner seedRoles() {
        return args -> {
            log.info("🔧 Iniciando seed de roles...");

            createRoleIfNotExists("ADMIN");
            createRoleIfNotExists("TEACHER");
            createRoleIfNotExists("STUDENT");
            createRoleIfNotExists("PARENT");
            createRoleIfNotExists("STAFF");

            log.info("✅ Seed de roles completado");
        };
    }

    private void createRoleIfNotExists(String roleName) {
        if (!roleRepository.existsByName(roleName)) {
            RoleEntity role = RoleEntity.builder()
                    .roleId(UUID.randomUUID())
                    .name(roleName)
                    .createdAt(LocalDateTime.now())
                    .build();

            roleRepository.save(role);
            log.info("✓ Rol creado: {}", roleName);
        } else {
            log.debug("Rol ya existe: {}", roleName);
        }
    }
}

*/
