package org.school.management.academic.domain.repository;

import org.school.management.academic.domain.model.Subject;
import org.school.management.academic.domain.valueobject.YearLevel; // Nuevo Import
import org.school.management.academic.domain.valueobject.ids.OrientationId;
import org.school.management.academic.domain.valueobject.ids.SubjectId;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository {
    Subject save(Subject subject);

    Optional<Subject> findById(SubjectId subjectId);

    Optional<Subject> findByCode(String code);

    List<Subject> findAll();

    List<Subject> findActiveSubjects();

    // CAMBIO A VO
    List<Subject> findByYearLevel(YearLevel yearLevel);

    // CAMBIO A VO
    List<Subject> findCommonSubjects(YearLevel yearLevel);

    List<Subject> findByOrientation(OrientationId orientationId);

    // CAMBIO A VO
    List<Subject> findByYearLevelAndOrientation(YearLevel yearLevel, OrientationId orientationId);

    // --- MÉTODOS JPA AÑADIDOS ---
    List<Subject> findAvailableForGradeLevel(YearLevel yearLevel, OrientationId orientationId);
    long countByYearLevel(YearLevel yearLevel);
    long countByOrientation(OrientationId orientationId);
    // (findByIdWithOrientation genera una proyección, lo dejaré fuera del contrato puro de dominio)

    boolean existsByCode(String code);

    long count();

    void delete(SubjectId id);
}