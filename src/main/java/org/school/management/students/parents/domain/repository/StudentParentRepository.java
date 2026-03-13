package org.school.management.students.parents.domain.repository;

import org.school.management.students.parents.domain.model.StudentParent;
import org.school.management.students.parents.domain.valueobject.ParentId;
import org.school.management.students.parents.domain.valueobject.StudentParentId;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;

import java.util.List;
import java.util.Optional;

/**
 * Puerto del repositorio para el agregado StudentParent.
 * Gestiona los vínculos entre estudiantes y padres/tutores.
 */
public interface StudentParentRepository {

    Optional<StudentParent> findByStudentParentId(StudentParentId studentParentId);

    Optional<StudentParent> findByStudentIdAndParentId(
            StudentPersonalDataId studentId,
            ParentId parentId
    );

    /**
     * Retorna todos los vínculos de un estudiante (sus padres/tutores).
     */
    List<StudentParent> findAllByStudentId(StudentPersonalDataId studentId);

    /**
     * Retorna todos los vínculos de un padre (sus hijos).
     */
    List<StudentParent> findAllByParentId(ParentId parentId);

    /**
     * Verifica si ya existe un vínculo entre el estudiante y el padre.
     */
    boolean existsByStudentIdAndParentId(
            StudentPersonalDataId studentId,
            ParentId parentId
    );

    /**
     * Verifica si el estudiante ya tiene un contacto principal designado.
     */
    boolean existsPrimaryContactForStudent(StudentPersonalDataId studentId);

    StudentParent save(StudentParent studentParent);
}
