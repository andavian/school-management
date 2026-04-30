package org.school.management.academic.infra.persistence.adapters;

import lombok.RequiredArgsConstructor;
import org.school.management.academic.domain.model.EvaluationPeriod;
import org.school.management.academic.domain.repository.EvaluationPeriodRepository;
import org.school.management.academic.domain.valueobject.enums.PeriodStatus;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.PeriodId;
import org.school.management.academic.infra.persistence.entity.EvaluationPeriodEntity;
import org.school.management.academic.infra.persistence.mappers.EvaluationPeriodMapper;
import org.school.management.academic.infra.persistence.repository.EvaluationPeriodJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EvaluationPeriodRepositoryAdapter implements EvaluationPeriodRepository {

    private final EvaluationPeriodJpaRepository jpaRepository;
    private final EvaluationPeriodMapper mapper;

    @Override
    @Transactional
    public EvaluationPeriod save(EvaluationPeriod period) {
        EvaluationPeriodEntity entity = mapper.toEntity(period);
        EvaluationPeriodEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<EvaluationPeriod> findById(PeriodId id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    public List<EvaluationPeriod> findByAcademicYear(AcademicYearId academicYearId) {
        return jpaRepository.findByAcademicYearIdOrderByPeriodNumberAsc(academicYearId.value()).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<EvaluationPeriod> findByAcademicYearAndNumber(AcademicYearId academicYearId, int periodNumber) {
        return jpaRepository.findByAcademicYearIdAndPeriodNumber(academicYearId.value(), periodNumber)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<EvaluationPeriod> findByAcademicYearAndPeriodNumber(AcademicYearId academicYearId, int periodNumber) {
        return findByAcademicYearAndNumber(academicYearId, periodNumber);
    }

    @Override
    public Optional<EvaluationPeriod> findCurrentPeriod(AcademicYearId academicYearId) {
        return jpaRepository.findCurrentPeriod(academicYearId.value())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<EvaluationPeriod> findCurrentPeriod(AcademicYearId academicYearId, LocalDate date) {
        return jpaRepository.findByDate(academicYearId.value(), date)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<EvaluationPeriod> findCurrentPeriodInCurrentYear(LocalDate date) {
        return jpaRepository.findCurrentPeriodInCurrentYear(date)
                .map(mapper::toDomain);
    }

    @Override
    public List<EvaluationPeriod> findUpcomingPeriods(AcademicYearId academicYearId, LocalDate today) {
        return jpaRepository.findUpcomingPeriods(academicYearId.value(), today).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<EvaluationPeriod> findByDate(AcademicYearId academicYearId, LocalDate date) {
        return jpaRepository.findByDate(academicYearId.value(), date)
                .map(mapper::toDomain);
    }

    @Override
    public List<EvaluationPeriod> findByStatus(PeriodStatus status) {
        return jpaRepository.findByStatus(status.name()).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByAcademicYearAndPeriodNumber(AcademicYearId academicYearId, int periodNumber) {
        return jpaRepository.existsByAcademicYearIdAndPeriodNumber(
                academicYearId.value(),
                periodNumber
        );
    }

    @Override
    public int getMaxPeriodNumber(AcademicYearId academicYearId) {
        Integer max = jpaRepository.findMaxPeriodNumber(academicYearId.value());
        return max != null ? max : 0;
    }
}