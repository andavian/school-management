package org.school.management.academic.infra.persistence.adapters;

import lombok.RequiredArgsConstructor;
import org.school.management.academic.domain.model.GradeLevel;
import org.school.management.academic.domain.repository.GradeLevelRepository;
import org.school.management.academic.domain.valueobject.Division;
import org.school.management.academic.domain.valueobject.YearLevel;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.GradeLevelId;
import org.school.management.academic.domain.valueobject.ids.OrientationId;
import org.school.management.academic.infra.persistence.entity.GradeLevelEntity;
import org.school.management.academic.infra.persistence.mappers.GradeLevelMapper;
import org.school.management.academic.infra.persistence.repository.GradeLevelJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GradeLevelRepositoryAdapter implements GradeLevelRepository {

    private final GradeLevelJpaRepository jpaRepository;
    private final GradeLevelMapper mapper;

    @Override
    @Transactional
    public GradeLevel save(GradeLevel gradeLevel) {
        GradeLevelEntity entity = mapper.toEntity(gradeLevel);
        GradeLevelEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<GradeLevel> findById(GradeLevelId id) {
        return jpaRepository.findById(id.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<GradeLevel> findByAcademicYearAndYearLevelAndDivision(
            AcademicYearId academicYearId,
            YearLevel yearLevel,
            Division division) {
        return jpaRepository.findByAcademicYearIdAndYearLevelAndDivision(
                academicYearId.getValue(),
                yearLevel.getValue(),
                division.getValue()
        ).map(mapper::toDomain);
    }

    @Override
    public List<GradeLevel> findByAcademicYear(AcademicYearId academicYearId) {
        return jpaRepository.findByAcademicYearId(academicYearId.getValue()).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
      public List<GradeLevel> findByAcademicYearAndYearLevel(AcademicYearId academicYearId, YearLevel yearLevel) {
        return jpaRepository.findByAcademicYearAndYearLevel(
                        academicYearId.getValue(),
                        yearLevel.getValue() // Acceso al valor para la capa de persistencia
                ).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<GradeLevel> findByOrientation(OrientationId orientationId) {
        return jpaRepository.findByOrientationId(orientationId.getValue()).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<GradeLevel> findActiveGradeLevels() {
        return jpaRepository.findByIsActiveTrue().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<GradeLevel> findActiveByAcademicYear(AcademicYearId academicYearId) {
        return jpaRepository.findByAcademicYearIdAndIsActiveTrue(academicYearId.getValue()).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<GradeLevel> findCurrentYearActiveLevels() {
        return jpaRepository.findCurrentYearActiveLevels().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

   @Override
    public List<GradeLevel> findByYearLevelAndOrientation(
            AcademicYearId academicYearId,
            YearLevel yearLevel,
            OrientationId orientationId) {
        return jpaRepository.findByYearLevelAndOrientation(
                        academicYearId.getValue(),
                        yearLevel.getValue(),
                        orientationId != null ? orientationId.getValue() : null
                ).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<GradeLevel> findByTeacher(UUID teacherId) {
        return jpaRepository.findByHomeroomTeacherId(teacherId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByAcademicYearAndYearLevelAndDivision(
            AcademicYearId academicYearId,
            YearLevel yearLevel,
            Division division) {
        return jpaRepository.existsByAcademicYearIdAndYearLevelAndDivision(
                academicYearId.getValue(),
                yearLevel.getValue(),
                division.getValue()
        );
    }

    @Override
    public long countByAcademicYear(AcademicYearId academicYearId) {
        return jpaRepository.countByAcademicYearId(academicYearId.getValue());
    }

    @Override
    @Transactional
    public void delete(GradeLevelId id) {
        jpaRepository.deleteById(id.getValue());
    }
}