package org.school.management.academic.infra.persistence.repository;

import org.school.management.academic.infra.persistence.entity.EvaluationPeriodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EvaluationPeriodJpaRepository extends JpaRepository<EvaluationPeriodEntity, UUID> {

    // Se eliminan findByAcademicYearId y findByAcademicYearIdOrderByPeriodNumber por redundancia
    List<EvaluationPeriodEntity> findByAcademicYearIdOrderByPeriodNumberAsc(UUID academicYearId);

    Optional<EvaluationPeriodEntity> findByAcademicYearIdAndPeriodNumber(
            UUID academicYearId, Integer periodNumber
    );

    // Consulta que busca el periodo marcado como 'current' en un año específico
    @Query("""
            SELECT ep FROM EvaluationPeriodEntity ep
            WHERE ep.academicYearId = :academicYearId
            AND ep.isCurrent = true
            """)
    Optional<EvaluationPeriodEntity> findCurrentPeriod(@Param("academicYearId") UUID academicYearId);

    // Consulta para encontrar el periodo actual en el AÑO ACADÉMICO MARCADO COMO ACTUAL
    @Query("SELECT ep FROM EvaluationPeriodEntity ep, AcademicYearEntity ay " + // Simplificando el JOIN
            "WHERE ep.academicYearId = ay.academicYearId " +
            "AND ay.status = 'ACTIVE' AND :date BETWEEN ep.startDate AND ep.endDate")
    Optional<EvaluationPeriodEntity> findCurrentPeriodInCurrentYear(@Param("date") LocalDate date);

    // Consulta para encontrar periodos futuros (deadlines posteriores a hoy y activo)
    @Query("SELECT ep FROM EvaluationPeriodEntity ep WHERE ep.academicYearId = :academicYearId " +
            "AND ep.endDate >= :today " + // Asumo que upcoming significa que el periodo aún no ha terminado
            "AND ep.status <> 'CLOSED' " + // Mejor filtrar por estado que por isCurrent/isCurrent (más flexible)
            "ORDER BY ep.periodNumber")
    List<EvaluationPeriodEntity> findUpcomingPeriods(
            @Param("academicYearId") UUID academicYearId,
            @Param("today") LocalDate today
    );

    // Consulta para encontrar el periodo que contiene una fecha
    @Query("""
            SELECT ep FROM EvaluationPeriodEntity ep
            WHERE ep.academicYearId = :academicYearId
            AND :date BETWEEN ep.startDate AND ep.endDate
            """)
    Optional<EvaluationPeriodEntity> findByDate(
            @Param("academicYearId") UUID academicYearId,
            @Param("date") LocalDate date
    );

    // En la Entidad el Status es String, por eso el parámetro es String
    List<EvaluationPeriodEntity> findByStatus(String status);

    boolean existsByAcademicYearIdAndPeriodNumber(UUID academicYearId, Integer periodNumber);

    long countByAcademicYearId(UUID academicYearId);

    @Query("SELECT MAX(ep.periodNumber) FROM EvaluationPeriodEntity ep WHERE ep.academicYearId = :academicYearId")
    Integer findMaxPeriodNumber(@Param("academicYearId") UUID academicYearId);
}