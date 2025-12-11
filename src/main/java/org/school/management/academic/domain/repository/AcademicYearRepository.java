package org.school.management.academic.domain.repository;

import org.school.management.academic.domain.model.*;
import org.school.management.academic.domain.valueobject.enums.AcademicYearStatus;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AcademicYearRepository {
    AcademicYear save(AcademicYear academicYear);
    Optional<AcademicYear> findById(AcademicYearId academicYearId);
    Optional<AcademicYear> findByYear(int year);
    Optional<AcademicYear> findCurrentYear();
    Optional<AcademicYear> findByDate(LocalDate date);
    List<AcademicYear> findAll();
    List<AcademicYear> findFromYearOnwards(int startYear);
    List<AcademicYear> findExpiredYears(LocalDate date);
    List<AcademicYear> findByStatus(AcademicYearStatus status);
    boolean existsByYear(int year);
    boolean existsByStatus(AcademicYearStatus status);
    void deactivateAllCurrent();
    void delete(AcademicYearId id);
    long count();
}


