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
    // NUEVOS MÉTODOS CON DNI
    // ============================================
    Optional<UserEntity> findByDni(String dni);
    boolean existsByDni(String dni);
    void deleteByDni(String dni);

    // Consultas por roles
    @Query("SELECT u FROM UserEntity u WHERE u.roles LIKE %:role%")
    List<UserEntity> findByRoleContaining(@Param("role") String role);

    List<UserEntity> findByIsActiveTrue();
    List<UserEntity> findByIsActiveFalse();

    // Consultas por fechas
    List<UserEntity> findByCreatedAtAfter(LocalDateTime date);
    List<UserEntity> findByLastLoginAtAfter(LocalDateTime date);

    @Query("SELECT COUNT(u) FROM UserEntity u WHERE u.roles LIKE %:role%")
    long countByRoleContaining(@Param("role") String role);

    long countByIsActiveTrue();

    // Consultas útiles para el colegio
    @Query("SELECT u FROM UserEntity u WHERE u.isActive = true AND u.roles LIKE %:role% ORDER BY u.createdAt DESC")
    List<UserEntity> findActiveUsersByRole(@Param("role") String role);

    @Query("SELECT u FROM UserEntity u WHERE u.lastLoginAt IS NULL AND u.createdAt < :date")
    List<UserEntity> findUsersNeverLoggedInBefore(@Param("date") LocalDateTime date);
}