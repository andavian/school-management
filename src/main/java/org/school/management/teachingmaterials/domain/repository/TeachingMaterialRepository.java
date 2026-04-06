package org.school.management.teachingmaterials.domain.repository;

import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.course.domain.valueobject.CourseSubjectId;
import org.school.management.teachers.domain.valueobject.TeacherId;
import org.school.management.teachingmaterials.domain.model.TeachingMaterial;
import org.school.management.teachingmaterials.domain.valueobject.TeachingMaterialId;

import java.util.List;
import java.util.Optional;

/**
 * Puerto del dominio para persistencia de {@link TeachingMaterial}.
 *
 * <p>Recibe Value Objects, nunca tipos primitivos crudos.</p>
 */
public interface TeachingMaterialRepository {

    TeachingMaterial save(TeachingMaterial material);

    Optional<TeachingMaterial> findById(TeachingMaterialId materialId);

    /**
     * Busca materiales de un curso específico en un año académico.
     * Usado por TEACHER, ADMIN y STAFF.
     */
    List<TeachingMaterial> findByCourseSubjectId(CourseSubjectId courseSubjectId);

    /**
     * Busca solo los materiales visibles de los cursos en que participa un estudiante.
     * El filtro de inscripción se resuelve en el use case, pasando los IDs de cursos.
     */
    List<TeachingMaterial> findVisibleByCourseSubjectIds(List<CourseSubjectId> courseSubjectIds);

    /**
     * Todos los materiales subidos por un profesor — útil para gestión.
     */
    List<TeachingMaterial> findByTeacherId(TeacherId teacherId);

    /**
     * Materiales de un profesor en un curso específico.
     */
    List<TeachingMaterial> findByTeacherIdAndCourseSubjectId(TeacherId teacherId,
                                                             CourseSubjectId courseSubjectId);

    void delete(TeachingMaterialId materialId);

    boolean existsById(TeachingMaterialId materialId);
}