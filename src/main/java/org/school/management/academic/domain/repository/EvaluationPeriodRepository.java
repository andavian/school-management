package org.school.management.academic.domain.repository;

import org.school.management.academic.domain.model.EvaluationPeriod;
import org.school.management.academic.domain.valueobject.enums.PeriodStatus;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.PeriodId;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EvaluationPeriodRepository {
    EvaluationPeriod save(EvaluationPeriod period);

    Optional<EvaluationPeriod> findById(PeriodId periodId);

    List<EvaluationPeriod> findByAcademicYear(AcademicYearId academicYearId);

    Optional<EvaluationPeriod> findByAcademicYearAndNumber(AcademicYearId academicYearId, int periodNumber);

    Optional<EvaluationPeriod> findByAcademicYearAndPeriodNumber(AcademicYearId academicYearId, int periodNumber);

    Optional<EvaluationPeriod> findCurrentPeriodInCurrentYear(LocalDate date);

    Optional<EvaluationPeriod> findCurrentPeriod(AcademicYearId academicYearId, LocalDate date);

    Optional<EvaluationPeriod> findCurrentPeriod(AcademicYearId academicYearId);

    List<EvaluationPeriod> findUpcomingPeriods(AcademicYearId academicYearId, LocalDate today);

    Optional<EvaluationPeriod> findByDate(AcademicYearId academicYearId, LocalDate date);

    List<EvaluationPeriod> findByStatus(PeriodStatus status);

    boolean existsByAcademicYearAndPeriodNumber(AcademicYearId academicYearId, int periodNumber);

    int getMaxPeriodNumber(AcademicYearId academicYearId);

    void delete(PeriodId id);
}