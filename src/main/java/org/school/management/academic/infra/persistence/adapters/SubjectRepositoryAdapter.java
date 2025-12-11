package org.school.management.academic.infra.persistence.adapters;

import lombok.RequiredArgsConstructor;
import org.school.management.academic.domain.model.Subject;
import org.school.management.academic.domain.repository.SubjectRepository;
import org.school.management.academic.domain.valueobject.YearLevel;
import org.school.management.academic.domain.valueobject.ids.OrientationId;
import org.school.management.academic.domain.valueobject.ids.SubjectId;
import org.school.management.academic.infra.persistence.entity.SubjectEntity;
import org.school.management.academic.infra.persistence.mappers.SubjectMapper;
import org.school.management.academic.infra.persistence.repository.SubjectJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true) // Optimización: Lectura por defecto
public class SubjectRepositoryAdapter implements SubjectRepository {

    private final SubjectJpaRepository jpaRepository;
    private final SubjectMapper mapper;

    @Override
    @Transactional // Sobreescribe para escritura
    public Subject save(Subject subject) {
        SubjectEntity entity = mapper.toEntity(subject);
        SubjectEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Subject> findById(SubjectId id) {
        return jpaRepository.findById(id.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Subject> findByCode(String code) {
        return jpaRepository.findByCode(code)
                .map(mapper::toDomain);
    }

    @Override
    public List<Subject> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    // IMPLEMENTACIÓN CORREGIDA (Ya que jpaRepository.findByIsActiveTrue existe)
    public List<Subject> findActiveSubjects() {
        return jpaRepository.findByIsActiveTrue().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    /*
     * MÉTODOS ELIMINADOS del Adapter para evitar el error "Method does not override":
     * - public List<Subject> findActive() { ... } // Redundante con findActiveSubjects
     * - Los métodos que usaban 'int' como parámetro (findByYearLevel, findCommonSubjects, findByYearLevelAndOrientation)
     * - findCommonSubjectsForYearLevel: Redundante con findCommonSubjects (al cambiar la firma a VO)
     */

    @Override
    // IMPLEMENTACIÓN SINCRONIZADA con la interfaz (usa YearLevel VO)
    public List<Subject> findByYearLevel(YearLevel yearLevel) {
        return jpaRepository.findByYearLevel(yearLevel.getValue()).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    // IMPLEMENTACIÓN SINCRONIZADA con la interfaz (usa YearLevel VO)
    public List<Subject> findCommonSubjects(YearLevel yearLevel) {
        // Usamos la query JPA que busca materias sin orientationId
        return jpaRepository.findCommonSubjects(yearLevel.getValue()).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    // IMPLEMENTACIÓN CORREGIDA (de List.of())
    public List<Subject> findByOrientation(OrientationId orientationId) {
        return jpaRepository.findByOrientationId(orientationId.getValue()).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    // IMPLEMENTACIÓN SINCRONIZADA con la interfaz (usa YearLevel VO)
    public List<Subject> findByYearLevelAndOrientation(YearLevel yearLevel, OrientationId orientationId) {
        return jpaRepository.findByYearLevelAndOrientation(
                        yearLevel.getValue(),
                        orientationId != null ? orientationId.getValue() : null
                ).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    // IMPLEMENTACIÓN DEL NUEVO MÉTODO DE CONTRATO
    public List<Subject> findAvailableForGradeLevel(YearLevel yearLevel, OrientationId orientationId) {
        return jpaRepository.findAvailableForGradeLevel(
                        yearLevel.getValue(),
                        orientationId != null ? orientationId.getValue() : null
                ).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    // --- MÉTODOS DE CONTEO AÑADIDOS ---

    @Override
    public long count() {
        return jpaRepository.count(); // Usa el método por defecto de JpaRepository
    }

    @Override
    public long countByYearLevel(YearLevel yearLevel) {
        return jpaRepository.countByYearLevel(yearLevel.getValue());
    }

    @Override
    public long countByOrientation(OrientationId orientationId) {
        return jpaRepository.countByOrientationId(orientationId.getValue());
    }

    // --- Métodos de Base ---

    @Override
    public boolean existsByCode(String code) {
        return jpaRepository.existsByCode(code);
    }

    @Override
    @Transactional
    public void delete(SubjectId id) {
        jpaRepository.deleteById(id.getValue());
    }
}