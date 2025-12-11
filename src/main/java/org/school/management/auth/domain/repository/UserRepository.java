package org.school.management.auth.domain.repository;

import org.school.management.auth.domain.model.User;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.shared.person.domain.valueobject.DNI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

    // CRUD Básico
    User save(User user);
    Optional<User> findById(UserId id);

    // ============================================
    // NUEVOS MÉTODOS CON DNI
    // ============================================
    Optional<User> findByDni(DNI dni);
    boolean existsByDni(DNI dni);
    void deleteByDni(DNI dni);

    // Consultas específicas del dominio
    List<User> findByRole(String roleName);
    List<User> findActiveUsers();
    List<User> findInactiveUsers();

    // Para reportes y auditoría
    List<User> findUsersCreatedAfter(LocalDateTime date);
    List<User> findUsersLastLoginAfter(LocalDateTime date);
    long countByRole(String roleName);
    long countActiveUsers();
}
