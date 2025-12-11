package org.school.management.academic.infra.persistence.adapters;

import lombok.RequiredArgsConstructor;
import org.school.management.academic.domain.model.AcademicYear;
import org.school.management.academic.domain.repository.AcademicYearRepository;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.enums.AcademicYearStatus;
import org.school.management.academic.infra.persistence.entity.AcademicYearEntity;
import org.school.management.academic.infra.persistence.mappers.AcademicYearMapper;
import org.school.management.academic.infra.persistence.repository.AcademicYearJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AcademicYearRepositoryAdapter implements AcademicYearRepository {

    private final AcademicYearJpaRepository jpaRepository;
    private final AcademicYearMapper mapper;

    @Override
    @Transactional
    public AcademicYear save(AcademicYear academicYear) {
        AcademicYearEntity entity = mapper.toEntity(academicYear);
        AcademicYearEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<AcademicYear> findById(AcademicYearId id) {
        return jpaRepository.findById(id.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<AcademicYear> findByYear(int year) {
        // BUG RESUELTO: Antes retornaba empty
        return jpaRepository.findByYear(year)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<AcademicYear> findCurrentYear() {
        return jpaRepository.findByStatus(AcademicYearStatus.ACTIVE)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<AcademicYear> findByDate(LocalDate date) {
        // Asumiendo que busca el año escolar que contiene esta fecha
        return jpaRepository.findByDate(date)
                .map(mapper::toDomain);
    }

    @Override
    public List<AcademicYear> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AcademicYear> findFromYearOnwards(int startYear) {
        return jpaRepository.findByYearGreaterThanEqual(startYear).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AcademicYear> findExpiredYears(LocalDate date) {
        return jpaRepository.findByEndDateBefore(date).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AcademicYear> findByStatus(AcademicYearStatus status) {

        return jpaRepository.findAllByStatus(status).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByYear(int year) {

        return jpaRepository.existsByYear(year);
    }

    @Override
    public boolean existsByStatus(AcademicYearStatus status) {

        return jpaRepository.existsByStatus(status);
    }

    @Override
    @Transactional
    public void deactivateAllCurrent() {
        jpaRepository.deactivateAllCurrent();
    }

    @Override
    @Transactional
    public void delete(AcademicYearId id) {

        if (jpaRepository.existsById(id.getValue())) {
            jpaRepository.deleteById(id.getValue());
        }
    }

    @Override
    public long count() {

        return jpaRepository.count();
    }
}