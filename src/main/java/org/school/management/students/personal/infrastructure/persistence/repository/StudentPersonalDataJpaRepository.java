package org.school.management.students.personal.infrastructure.persistence.repository;

import org.school.management.students.personal.infrastructure.persistence.entity.StudentPersonalDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository para StudentPersonalDataEntity.
 *
 * Solo define queries — sin lógica de negocio.
 * La lógica vive en StudentPersonalDataRepositoryAdapter (puerto del dominio).
 */
@Repository
public interface StudentPersonalDataJpaRepository extends JpaRepository<StudentPersonalDataEntity, UUID> {

    // ── Búsquedas por identidad ───────────────────────────────────────────

    Optional<StudentPersonalDataEntity> findByDni(String dni);

    Optional<StudentPersonalDataEntity> findByCuil(String cuil);

    Optional<StudentPersonalDataEntity> findByUserId(UUID userId);

    // ── Verificaciones de unicidad ────────────────────────────────────────

    boolean existsByDni(String dni);

    boolean existsByCuil(String cuil);

    // ── Búsqueda por nombre (para SearchStudentsUseCase) ──────────────────

    /**
     * Búsqueda parcial case-insensitive por firstName o lastName.
     * Soporta: "juan", "pérez", "juan pérez".
     */
    @Query("SELECT s FROM StudentPersonalDataEntity s " +
            "WHERE LOWER(s.firstName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(s.lastName)  LIKE LOWER(CONCAT('%', :query, '%'))")
    List<StudentPersonalDataEntity> searchByFullName(@Param("query") String query);

    // ── Búsqueda por lugar de residencia ──────────────────────────────────

    /**
     * Devuelve todos los estudiantes que residen en una localidad dada.
     * residencePlaceId se persiste como BINARY(16) → el converter lo transforma
     * a UUID automáticamente, pero en JPQL comparamos con el UUID Java directamente.
     */
    List<StudentPersonalDataEntity> findByResidencePlaceId(UUID residencePlaceId);
}