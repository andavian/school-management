package org.school.management.auth.infra.persistence.repository;


import org.school.management.auth.infra.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {

    // ============================================
    // QUERIES POR DNI
    // ============================================
    Optional<UserEntity> findByDni(String dni);

    boolean existsByDni(String dni);

    void deleteByDni(String dni);

    // ============================================
    // QUERIES POR ROLES
    // ============================================
    @Query("SELECT u FROM UserEntity u WHERE u.roles LIKE %:role%")
    List<UserEntity> findByRoleContaining(@Param("role") String role);

    @Query("SELECT COUNT(u) FROM UserEntity u WHERE u.roles LIKE %:role%")
    long countByRoleContaining(@Param("role") String role);

    // ============================================
    // QUERIES POR ESTADO ACTIVO
    // ============================================
    List<UserEntity> findByIsActiveTrue();

    List<UserEntity> findByIsActiveFalse();

    long countByIsActiveTrue();

    // ============================================
    // QUERIES POR FECHAS
    // ============================================
    List<UserEntity> findByCreatedAtAfter(LocalDateTime date);

    List<UserEntity> findByLastLoginAtAfter(LocalDateTime date);

    // ============================================
    // QUERIES COMPLEJAS ÚTILES
    // ============================================
    @Query("SELECT u FROM UserEntity u WHERE u.isActive = true AND u.roles LIKE %:role% ORDER BY u.createdAt DESC")
    List<UserEntity> findActiveUsersByRole(@Param("role") String role);

    @Query("SELECT u FROM UserEntity u WHERE u.lastLoginAt IS NULL AND u.createdAt < :date")
    List<UserEntity> findUsersNeverLoggedInBefore(@Param("date") LocalDateTime date);

    // ============================================
    // QUERIES PARA ADMINISTRACIÓN
    // ============================================
    @Query("SELECT u FROM UserEntity u WHERE u.isActive = false AND u.lastLoginAt < :date")
    List<UserEntity> findInactiveUsersSince(@Param("date") LocalDateTime date);

    // Buscar usuarios por DNI parcial (útil para búsquedas)
    @Query("SELECT u FROM UserEntity u WHERE u.dni LIKE %:dniPart%")
    List<UserEntity> findByDniContaining(@Param("dniPart") String dniPart);
}
