package org.school.management.academic.infra.persistence.adapters;

import lombok.RequiredArgsConstructor;
import org.school.management.academic.domain.model.AcademicYear;
import org.school.management.academic.domain.model.QualificationRegistry;
import org.school.management.academic.domain.repository.AcademicYearRepository;
import org.school.management.academic.domain.repository.QualificationRegistryRepository;
import org.school.management.academic.domain.valueobject.enums.RegistryStatus;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.RegistryId;
import org.school.management.academic.infra.persistence.entity.QualificationRegistryEntity;
import org.school.management.academic.infra.persistence.mappers.QualificationRegistryMapper;
import org.school.management.academic.infra.persistence.repository.QualificationRegistryJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QualificationRegistryRepositoryAdapter implements QualificationRegistryRepository {

    private final QualificationRegistryJpaRepository jpaRepository;
    private final AcademicYearRepository academicYearRepository;
    private final QualificationRegistryMapper mapper;

    @Override
    @Transactional
    public QualificationRegistry save(QualificationRegistry registry) {
        QualificationRegistryEntity entity = mapper.toEntity(registry);
        QualificationRegistryEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<QualificationRegistry> findById(RegistryId id) {
        return jpaRepository.findById(id.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<QualificationRegistry> findByRegistryNumber(String registryNumber) {
        return jpaRepository.findByRegistryNumber(registryNumber)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<QualificationRegistry> findActiveRegistryForYear(AcademicYearId academicYearId) {
        return jpaRepository.findActiveByAcademicYear(academicYearId.getValue())
                .map(mapper::toDomain);
    }

   @Override
    public List<QualificationRegistry> findByAcademicYear(AcademicYearId academicYearId) {
        return jpaRepository.findByAcademicYearId(academicYearId.getValue()).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<QualificationRegistry> findByStatus(RegistryStatus status) {
        return jpaRepository.findByStatus(status.name()).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<QualificationRegistry> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByRegistryNumber(String registryNumber) {
        return jpaRepository.existsByRegistryNumber(registryNumber);
    }

    @Override
    public int getAvailableFolios(RegistryId id) {
        Integer available = jpaRepository.getAvailableFolios(id.getValue());
        return available != null ? available : 0;
    }

    @Override
    @Transactional
    public int incrementFolio(RegistryId id) {
        return jpaRepository.incrementFolio(id.getValue());
    }

    @Override
    public List<QualificationRegistry> findNearFullRegistries(int threshold) {
        return jpaRepository.findNearFullRegistries(threshold).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countByAcademicYear(AcademicYearId academicYearId) {
        return jpaRepository.countByAcademicYearId(academicYearId.getValue());
    }

    @Override
    public long countByStatus(RegistryStatus status) {
        // Traducir el VO del dominio a String para JPA
        return jpaRepository.countByStatus(status.name());
    }

    @Override
    @Transactional
    public void delete(RegistryId id) {
        jpaRepository.deleteById(id.getValue());
    }

    @Override
    public Optional<QualificationRegistry> findActiveInCurrentYear() {

        Optional<AcademicYearId> currentYearId = academicYearRepository.findCurrentYear()
                .map(AcademicYear::getAcademicYearId);

        return currentYearId.flatMap(academicYearId -> jpaRepository.findActiveByAcademicYear(academicYearId.getValue())
                .map(mapper::toDomain));

    }
}