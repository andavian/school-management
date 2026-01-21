package org.school.management.students.enrollment.domain.repository;

import org.school.management.students.enrollment.domain.model.StudentEnrollment;
import org.school.management.students.enrollment.domain.valueobject.EnrollmentStatus;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.GradeLevelId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para el agregado raíz StudentEnrollment.
 * <p>
 * Este repositorio opera únicamente sobre la raíz del agregado.
 * </p>
 */
public interface StudentEnrollmentRepository {

    /**
     * Busca una inscripción por su ID único.
     *
     * @param enrollmentId ID de la inscripción
     * @return Optional con la inscripción, o vacío si no existe
     */
    Optional<StudentEnrollment> findByEnrollmentId(UUID enrollmentId);

    /**
     * Busca una inscripción por el ID del estudiante y el año académico.
     *
     * @param studentId ID del estudiante
     * @param academicYearId ID del año académico
     * @return Optional con la inscripción, o vacío si no existe
     */
    Optional<StudentEnrollment> findByStudentIdAndAcademicYearId(
            StudentPersonalDataId studentId,
            AcademicYearId academicYearId
    );

    /**
     * Busca todas las inscripciones de un estudiante (histórico).
     *
     * @param studentId ID del estudiante
     * @return Lista de inscripciones
     */
    List<StudentEnrollment> findAllByStudentId(StudentPersonalDataId studentId);

    /**
     * Busca todas las inscripciones activas de un estudiante.
     *
     * @param studentId ID del estudiante
     * @return Lista de inscripciones activas
     */
    List<StudentEnrollment> findActiveByStudentId(StudentPersonalDataId studentId);

    /**
     * Busca todas las inscripciones de un año académico.
     *
     * @param academicYearId ID del año académico
     * @return Lista de inscripciones
     */
    List<StudentEnrollment> findAllByAcademicYearId(AcademicYearId academicYearId);

    /**
     * Busca todas las inscripciones de un grado/nivel en un año académico.
     *
     * @param gradeLevelId ID del grado/nivel
     * @param academicYearId ID del año académico
     * @return Lista de inscripciones
     */
    List<StudentEnrollment> findByGradeLevelIdAndAcademicYearId(
            GradeLevelId gradeLevelId,
            AcademicYearId academicYearId
    );

    /**
     * Verifica si ya existe una inscripción activa para el estudiante en el año académico.
     *
     * @param studentId ID del estudiante
     * @param academicYearId ID del año académico
     * @return true si ya existe una inscripción activa, false en caso contrario
     */
    boolean existsActiveEnrollment(StudentPersonalDataId studentId, AcademicYearId academicYearId);

    /**
     * Verifica si ya existe una inscripción con estado COMPLETED para el estudiante en el año académico.
     *
     * @param studentId ID del estudiante
     * @param academicYearId ID del año académico
     * @return true si ya existe una inscripción completada, false en caso contrario
     */
    boolean existsCompletedEnrollment(StudentPersonalDataId studentId, AcademicYearId academicYearId);

    /**
     * Guarda o actualiza una inscripción.
     *
     * @param enrollment Inscripción a persistir
     * @return Inscripción guardada
     */
    StudentEnrollment save(StudentEnrollment enrollment);

    /**
     * Elimina una inscripción por su ID.
     *
     * @param enrollmentId ID de la inscripción a eliminar
     */
    void deleteByEnrollmentId(UUID enrollmentId);
}