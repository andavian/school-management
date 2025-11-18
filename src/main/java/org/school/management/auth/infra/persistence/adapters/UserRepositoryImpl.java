package org.school.management.auth.infra.persistence.adapters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.domain.model.User;
import org.school.management.auth.domain.repository.UserRepository;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.auth.infra.persistence.entity.UserEntity;
import org.school.management.auth.infra.persistence.mappers.AuthPersistenceMapper;
import org.school.management.auth.infra.persistence.repository.UserJpaRepository;
import org.school.management.shared.domain.valueobjects.DNI;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository jpaRepository;
    private final AuthPersistenceMapper mapper;

    // ============================================
    // CRUD BÁSICO
    // ============================================

    @Override
    @Transactional
    public User save(User user) {
        log.debug("Guardando usuario con DNI: {}", user.getDni().getValue());

        try {
            UserEntity entity = mapper.toEntity(user);
            UserEntity savedEntity = jpaRepository.save(entity);
            User savedUser = mapper.toDomain(savedEntity);

            log.debug("Usuario guardado exitosamente. ID: {}, DNI: {}",
                    savedUser.getUserId().asString(),
                    savedUser.getDni().getValue());

            return savedUser;

        } catch (Exception e) {
            log.error("Error guardando usuario con DNI {}: {}",
                    user.getDni().getValue(), e.getMessage(), e);
            throw new UserRepositoryException("Error guardando usuario", e);
        }
    }

    @Override
    public Optional<User> findById(UserId id) {
        log.debug("Buscando usuario por ID: {}", id.asString());

        try {
            return jpaRepository.findById(id.getValue())
                    .map(entity -> {
                        log.debug("Usuario encontrado: DNI {}", entity.getDni());
                        return mapper.toDomain(entity);
                    });

        } catch (Exception e) {
            log.error("Error buscando usuario por ID {}: {}", id.asString(), e.getMessage());
            return Optional.empty();
        }
    }

    // ============================================
    // QUERIES POR DNI
    // ============================================

    @Override
    public Optional<User> findByDni(DNI dni) {
        log.debug("Buscando usuario por DNI: {}", dni.getValue());

        try {
            return jpaRepository.findByDni(dni.getValue())
                    .map(entity -> {
                        log.debug("Usuario encontrado por DNI: {} - Roles: {}",
                                dni.getValue(), entity.getRoles());
                        return mapper.toDomain(entity);
                    });

        } catch (Exception e) {
            log.error("Error buscando usuario por DNI {}: {}",
                    dni.getValue(), e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public boolean existsByDni(DNI dni) {
        log.debug("Verificando existencia de usuario con DNI: {}", dni.getValue());

        try {
            boolean exists = jpaRepository.existsByDni(dni.getValue());
            log.debug("Usuario con DNI {} existe: {}", dni.getValue(), exists);
            return exists;

        } catch (Exception e) {
            log.error("Error verificando existencia de DNI {}: {}",
                    dni.getValue(), e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public void deleteByDni(DNI dni) {
        log.info("Eliminando usuario por DNI: {}", dni.getValue());

        try {
            jpaRepository.deleteByDni(dni.getValue());
            log.info("Usuario con DNI {} eliminado exitosamente", dni.getValue());

        } catch (Exception e) {
            log.error("Error eliminando usuario con DNI {}: {}",
                    dni.getValue(), e.getMessage(), e);
            throw new UserRepositoryException("Error eliminando usuario", e);
        }
    }

    // ============================================
    // QUERIES POR ROLES
    // ============================================

    @Override
    public List<User> findByRole(String roleName) {
        log.debug("Buscando usuarios por rol: {}", roleName);

        try {
            List<UserEntity> entities = jpaRepository.findByRoleContaining(roleName);
            List<User> users = entities.stream()
                    .map(mapper::toDomain)
                    .collect(Collectors.toList());

            log.debug("Encontrados {} usuarios con rol {}", users.size(), roleName);
            return users;

        } catch (Exception e) {
            log.error("Error buscando usuarios por rol {}: {}", roleName, e.getMessage());
            return List.of();
        }
    }

    @Override
    public long countByRole(String roleName) {
        log.debug("Contando usuarios por rol: {}", roleName);

        try {
            long count = jpaRepository.countByRoleContaining(roleName);
            log.debug("Total de usuarios con rol {}: {}", roleName, count);
            return count;

        } catch (Exception e) {
            log.error("Error contando usuarios por rol {}: {}", roleName, e.getMessage());
            return 0;
        }
    }

    // ============================================
    // QUERIES POR ESTADO ACTIVO
    // ============================================

    @Override
    public List<User> findActiveUsers() {
        log.debug("Buscando usuarios activos");

        try {
            List<UserEntity> entities = jpaRepository.findByIsActiveTrue();
            List<User> users = entities.stream()
                    .map(mapper::toDomain)
                    .collect(Collectors.toList());

            log.debug("Encontrados {} usuarios activos", users.size());
            return users;

        } catch (Exception e) {
            log.error("Error buscando usuarios activos: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<User> findInactiveUsers() {
        log.debug("Buscando usuarios inactivos");

        try {
            List<UserEntity> entities = jpaRepository.findByIsActiveFalse();
            List<User> users = entities.stream()
                    .map(mapper::toDomain)
                    .collect(Collectors.toList());

            log.debug("Encontrados {} usuarios inactivos", users.size());
            return users;

        } catch (Exception e) {
            log.error("Error buscando usuarios inactivos: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public long countActiveUsers() {
        log.debug("Contando usuarios activos");

        try {
            long count = jpaRepository.countByIsActiveTrue();
            log.debug("Total de usuarios activos: {}", count);
            return count;

        } catch (Exception e) {
            log.error("Error contando usuarios activos: {}", e.getMessage());
            return 0;
        }
    }

    // ============================================
    // QUERIES POR FECHAS (AUDITORÍA)
    // ============================================

    @Override
    public List<User> findUsersCreatedAfter(LocalDateTime date) {
        log.debug("Buscando usuarios creados después de: {}", date);

        try {
            List<UserEntity> entities = jpaRepository.findByCreatedAtAfter(date);
            List<User> users = entities.stream()
                    .map(mapper::toDomain)
                    .collect(Collectors.toList());

            log.debug("Encontrados {} usuarios creados después de {}", users.size(), date);
            return users;

        } catch (Exception e) {
            log.error("Error buscando usuarios creados después de {}: {}",
                    date, e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<User> findUsersLastLoginAfter(LocalDateTime date) {
        log.debug("Buscando usuarios con último login después de: {}", date);

        try {
            List<UserEntity> entities = jpaRepository.findByLastLoginAtAfter(date);
            List<User> users = entities.stream()
                    .map(mapper::toDomain)
                    .collect(Collectors.toList());

            log.debug("Encontrados {} usuarios con login después de {}", users.size(), date);
            return users;

        } catch (Exception e) {
            log.error("Error buscando usuarios con login después de {}: {}",
                    date, e.getMessage());
            return List.of();
        }
    }

    // ============================================
    // MÉTODOS ADICIONALES ÚTILES (BONUS)
    // ============================================

    public List<User> findActiveUsersByRole(String roleName) {
        log.debug("Buscando usuarios activos por rol: {}", roleName);

        try {
            List<UserEntity> entities = jpaRepository.findActiveUsersByRole(roleName);
            return entities.stream()
                    .map(mapper::toDomain)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error buscando usuarios activos por rol {}: {}",
                    roleName, e.getMessage());
            return List.of();
        }
    }

    public List<User> findUsersNeverLoggedIn(LocalDateTime beforeDate) {
        log.debug("Buscando usuarios que nunca iniciaron sesión antes de: {}", beforeDate);

        try {
            List<UserEntity> entities = jpaRepository.findUsersNeverLoggedInBefore(beforeDate);
            return entities.stream()
                    .map(mapper::toDomain)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error buscando usuarios sin login: {}", e.getMessage());
            return List.of();
        }
    }

    public List<User> findInactiveUsersSince(LocalDateTime date) {
        log.debug("Buscando usuarios inactivos desde: {}", date);

        try {
            List<UserEntity> entities = jpaRepository.findInactiveUsersSince(date);
            return entities.stream()
                    .map(mapper::toDomain)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error buscando usuarios inactivos desde {}: {}",
                    date, e.getMessage());
            return List.of();
        }
    }

    // ============================================
    // EXCEPTION PERSONALIZADA
    // ============================================

    public static class UserRepositoryException extends RuntimeException {
        public UserRepositoryException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
