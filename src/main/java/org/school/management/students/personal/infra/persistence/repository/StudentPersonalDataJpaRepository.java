package org.school.management.students.personal.infra.persistence.repository;

import org.school.management.students.personal.infra.persistence.entity.StudentPersonalDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentPersonalDataJpaRepository extends JpaRepository<StudentPersonalDataEntity, UUID> {

    /**
     * Busca un estudiante por su ID de usuario (relación 1:1)
     */
    Optional<StudentPersonalDataEntity> findByUserId(UUID userId);

    /**
     * Busca un estudiante por su DNI (único en el sistema)
     */
    Optional<StudentPersonalDataEntity> findByDni(String dni);

    /**
     * Verifica si ya existe un estudiante con el DNI dado
     */
    boolean existsByDni(String dni);

    /**
     * Busca un estudiante por su CUIL (único en el sistema)
     */
    Optional<StudentPersonalDataEntity> findByCuil(String cuil);

    /**
     * Verifica si ya existe un estudiante con el CUIL dado
     */
    boolean existsByCuil(String cuil);

    /**
     * Busca estudiantes por apellido (para búsquedas rápidas)
     */
    java.util.List<StudentPersonalDataEntity> findByLastNameContainingIgnoreCase(String lastName);

    /**
     * Busca estudiantes por nombre o apellido (búsqueda parcial)
     */
    @Query("SELECT s FROM StudentPersonalDataEntity s " +
            "WHERE LOWER(s.firstName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(s.lastName) LIKE LOWER(CONCAT('%', :query, '%'))")
    java.util.List<StudentPersonalDataEntity> searchByFullName(@Param("query") String query);
}