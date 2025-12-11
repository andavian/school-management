package org.school.management.academic.infra.persistence.repository;

import org.school.management.academic.domain.valueobject.enums.AcademicYearStatus;
import org.school.management.academic.infra.persistence.entity.AcademicYearEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AcademicYearJpaRepository extends JpaRepository<AcademicYearEntity, UUID> {

    // Soporte para findByYear
    Optional<AcademicYearEntity> findByYear(int year);


    // Soporte para findByDate (complejo, requiere @Query)
    // Busca un año donde la fecha dada esté entre el inicio y el fin
    @Query("SELECT a FROM AcademicYearEntity a WHERE :date BETWEEN a.startDate AND a.endDate")
    Optional<AcademicYearEntity> findByDate(@Param("date") LocalDate date);

    // Soporte para findFromYearOnwards (refactorizado a nombre standard de JPA)
    List<AcademicYearEntity> findByYearGreaterThanEqual(int year);

    // Soporte para findExpiredYears (refactorizado a nombre standard de JPA)
    List<AcademicYearEntity> findByEndDateBefore(LocalDate date);

    List<AcademicYearEntity> findAllByStatus(AcademicYearStatus status);

    // Soporte para findByStatus
    Optional<AcademicYearEntity> findByStatus(AcademicYearStatus status);

    // Soporte para existsByYear
    boolean existsByYear(int year);

    boolean existsByStatus(AcademicYearStatus status);

    @Modifying
    @Query("UPDATE AcademicYearEntity a SET a.status = org.school.management.academic.domain.valueobject.enums.AcademicYearStatus.CLOSED " +
            "WHERE a.status = org.school.management.academic.domain.valueobject.enums.AcademicYearStatus.ACTIVE")
    void deactivateAllCurrent();
}