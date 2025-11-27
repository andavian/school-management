package org.school.management.auth.infra.persistence.repository;

import org.school.management.auth.infra.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {

    @EntityGraph(attributePaths = "roles")
    Optional<UserEntity> findByDni(String dni);
    boolean existsByDni(String dni);
    void deleteByDni(String dni);

    @Query("SELECT u FROM UserEntity u JOIN u.roles r WHERE r.name = :roleName")
    List<UserEntity> findByRoleName(@Param("roleName") String roleName);

    @Query("SELECT COUNT(u) FROM UserEntity u JOIN u.roles r WHERE r.name = :roleName")
    long countByRoleName(@Param("roleName") String roleName);

    // ✅ Usa "Active" en nombres de métodos (sin "Is")
    List<UserEntity> findByActiveTrue();
    List<UserEntity> findByActiveFalse();
    long countByActiveTrue();

    List<UserEntity> findByCreatedAtAfter(LocalDateTime date);
    List<UserEntity> findByLastLoginAtAfter(LocalDateTime date);

    // ✅ Usa "u.active" en queries (sin "is")
    @Query("SELECT u FROM UserEntity u JOIN u.roles r WHERE u.active = true AND r.name = :roleName ORDER BY u.createdAt DESC")
    List<UserEntity> findActiveUsersByRoleName(@Param("roleName") String roleName);

    @Query("SELECT u FROM UserEntity u WHERE u.lastLoginAt IS NULL AND u.createdAt < :date")
    List<UserEntity> findUsersNeverLoggedInBefore(@Param("date") LocalDateTime date);

    @Query("SELECT u FROM UserEntity u WHERE u.active = false AND u.lastLoginAt < :date")
    List<UserEntity> findInactiveUsersSince(@Param("date") LocalDateTime date);

    @Query("SELECT u FROM UserEntity u WHERE u.dni LIKE %:dniPart%")
    List<UserEntity> findByDniContaining(@Param("dniPart") String dniPart);

    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.roles WHERE u.dni = :dni")
    Optional<UserEntity> findByDniWithRoles(@Param("dni") String dni);
}