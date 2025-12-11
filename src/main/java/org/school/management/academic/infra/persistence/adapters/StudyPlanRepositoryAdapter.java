package org.school.management.academic.infra.persistence.adapters;

import lombok.RequiredArgsConstructor;
import org.school.management.academic.domain.model.StudyPlan;
import org.school.management.academic.domain.repository.StudyPlanRepository;
import org.school.management.academic.domain.valueobject.YearLevel;
import org.school.management.academic.domain.valueobject.ids.OrientationId;
import org.school.management.academic.domain.valueobject.ids.StudyPlanId;
import org.school.management.academic.domain.valueobject.ids.SubjectId;
import org.school.management.academic.infra.persistence.entity.StudyPlanEntity;
import org.school.management.academic.infra.persistence.entity.StudyPlanSubjectEntity;
import org.school.management.academic.infra.persistence.mappers.StudyPlanMapper;
import org.school.management.academic.infra.persistence.repository.StudyPlanJpaRepository;
import org.school.management.academic.infra.persistence.repository.StudyPlanSubjectJpaRepository; // Asumo que existe

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true) // Optimización: lectura por defecto
public class StudyPlanRepositoryAdapter implements StudyPlanRepository {

    private final StudyPlanJpaRepository jpaRepository;
    private final StudyPlanSubjectJpaRepository subjectJpaRepository; // Asumo que existe
    private final StudyPlanMapper mapper;

    @Override
    @Transactional
    public StudyPlan save(StudyPlan studyPlan) {
        StudyPlanEntity entity = mapper.toEntity(studyPlan);
        StudyPlanEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<StudyPlan> findById(StudyPlanId id) {
        return jpaRepository.findById(id.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<StudyPlan> findByCode(String code) {
        return jpaRepository.findByCode(code)
                .map(mapper::toDomain);
    }

    @Override
    public List<StudyPlan> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudyPlan> findByYearLevel(YearLevel yearLevel) {
        return jpaRepository.findByYearLevel(yearLevel.getValue()).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<StudyPlan> findByYearLevelAndOrientation(YearLevel yearLevel, OrientationId orientationId) {
        return jpaRepository.findByYearLevelAndOrientationId(
                yearLevel.getValue(),
                orientationId != null ? orientationId.getValue() : null
        ).map(mapper::toDomain);
    }

    @Override
    public List<StudyPlan> findActiveStudyPlans() {
        return jpaRepository.findByIsActiveTrue().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudyPlan> findByYearLevelAndOrientationIdIsNull(YearLevel yearLevel) {
        return jpaRepository.findByYearLevelAndOrientationIdIsNull(yearLevel.getValue()).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudyPlan> findByOrientation(OrientationId orientationId) {
        return jpaRepository.findByOrientationId(orientationId.getValue()).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudyPlan> findApplicableForGradeLevel(YearLevel yearLevel, OrientationId orientationId) {
        return jpaRepository.findApplicableForGradeLevel(
                        yearLevel.getValue(),
                        orientationId != null ? orientationId.getValue() : null
                ).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countByYearLevel(YearLevel yearLevel) {
        return jpaRepository.countByYearLevel(yearLevel.getValue());
    }

    @Override
    public boolean existsByCode(String code) {
        return jpaRepository.existsByCode(code);
    }

    @Override
    @Transactional
    public void delete(StudyPlanId id) {
        jpaRepository.deleteById(id.getValue());
    }

    @Override
    @Transactional
    public void addSubject(StudyPlanId planId, SubjectId subjectId) {
        // ... (lógica original) ...
        if (!hasSubject(planId, subjectId)) {
            StudyPlanSubjectEntity entity = StudyPlanSubjectEntity.builder()
                    .studyPlanSubjectId(UUID.randomUUID())
                    .studyPlanId(planId.getValue())
                    .subjectId(subjectId.getValue())
                    .build();
            subjectJpaRepository.save(entity);
        }
    }

    @Override
    @Transactional
    public void removeSubject(StudyPlanId planId, SubjectId subjectId) {
        // ... (lógica original) ...
        subjectJpaRepository.findByStudyPlanIdAndSubjectId(
                planId.getValue(),
                subjectId.getValue()
        ).ifPresent(subjectJpaRepository::delete);
    }

    @Override
    public List<SubjectId> findSubjectIds(StudyPlanId planId) {
        // ... (lógica original) ...
        return subjectJpaRepository.findSubjectIdsByStudyPlanId(planId.getValue()).stream()
                // Asumo que tienes un constructor SubjectId(UUID) o un mapper
                // Usaré SubjectId::new según tu código, asumiendo que es un constructor válido
                .map(SubjectId::new)
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasSubject(StudyPlanId planId, SubjectId subjectId) {
        // ... (lógica original) ...
        return subjectJpaRepository.existsByStudyPlanIdAndSubjectId(
                planId.getValue(),
                subjectId.getValue()
        );
    }

    @Override
    public List<SubjectId> findMandatorySubjectIds(StudyPlanId planId) {
        return subjectJpaRepository.findMandatorySubjects(planId.getValue()).stream()
                .map(StudyPlanSubjectEntity::getSubjectId)
                .map(SubjectId::new) // Asumo constructor SubjectId(UUID)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudyPlanId> findStudyPlanIdsBySubject(SubjectId subjectId) {
        return subjectJpaRepository.findStudyPlanIdsBySubjectId(subjectId.getValue()).stream()
                .map(StudyPlanId::new)
                .collect(Collectors.toList());
    }

    @Override
    public long countSubjects(StudyPlanId planId) {
        return subjectJpaRepository.countByStudyPlanId(planId.getValue());
    }

    @Override
    @Transactional
    public void deleteSubjectLinksByStudyPlan(StudyPlanId planId) {
        subjectJpaRepository.deleteByStudyPlanId(planId.getValue());
    }

    @Override
    @Transactional
    public void deleteSubjectLinksBySubject(SubjectId subjectId) {
        subjectJpaRepository.deleteBySubjectId(subjectId.getValue());
    }
}