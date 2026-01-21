package org.school.management.students.records.domain.repository;

import org.school.management.students.records.domain.model.StudentRecord;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.students.records.domain.valueobject.RecordId;
import org.school.management.students.records.domain.valueobject.RecordNumber;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para el agregado raíz StudentRecord.
 * <p>
 * Este repositorio opera únicamente sobre la raíz del agregado.
 * </p>
 */
public interface StudentRecordRepository {

    /**
     * Busca un legajo por su ID único.
     *
     * @param recordId ID del legajo
     * @return Optional con el legajo, o vacío si no existe
     */
    Optional<StudentRecord> findByRecordId(RecordId recordId);

    /**
     * Busca un legajo por el ID del estudiante y el año académico.
     *
     * @param studentId ID del estudiante
     * @param academicYearId ID del año académico
     * @return Optional con el legajo, o vacío si no existe
     */
    Optional<StudentRecord> findByStudentIdAndAcademicYearId(
            StudentPersonalDataId studentId,
            AcademicYearId academicYearId
    );

    /**
     * Busca un legajo por su número de legajo (único global).
     *
     * @param recordNumber Número de legajo (ej: LEG-2024-001234)
     * @return Optional con el legajo, o vacío si no existe
     */
    Optional<StudentRecord> findByRecordNumber(RecordNumber recordNumber);

    /**
     * Busca todos los legajos de un estudiante (histórico).
     *
     * @param studentId ID del estudiante
     * @return Lista de legajos
     */
    List<StudentRecord> findAllByStudentId(StudentPersonalDataId studentId);

    /**
     * Busca todos los legajos de un año académico.
     *
     * @param academicYearId ID del año académico
     * @return Lista de legajos
     */
    List<StudentRecord> findAllByAcademicYearId(AcademicYearId academicYearId);

    /**
     * Verifica si ya existe un legajo para el estudiante en el año académico.
     *
     * @param studentId ID del estudiante
     * @param academicYearId ID del año académico
     * @return true si ya existe, false en caso contrario
     */
    boolean existsByStudentIdAndAcademicYearId(
            StudentPersonalDataId studentId,
            AcademicYearId academicYearId
    );

    /**
     * Guarda o actualiza un legajo.
     *
     * @param record Legajo a persistir
     * @return Legajo guardado
     */
    StudentRecord save(StudentRecord record);

    /**
     * Elimina un legajo por su ID.
     *
     * @param recordId ID del legajo a eliminar
     */
    void deleteByRecordId(RecordId recordId);
}