package org.school.management.students.records.domain.repository;

import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;
import org.school.management.students.records.domain.model.StudentRecord;
import org.school.management.students.records.domain.valueobject.RecordId;
import org.school.management.students.records.domain.valueobject.RecordNumber;

import java.util.List;
import java.util.Optional;

/**
 * Puerto del repositorio para el agregado StudentRecord.
 * El legajo es único por estudiante — no por año académico.
 */
public interface StudentRecordRepository {

    /**
     * Busca el legajo por su ID único.
     */
    Optional<StudentRecord> findByRecordId(RecordId recordId);

    /**
     * Busca el legajo del estudiante.
     * Un estudiante tiene un único legajo permanente (su DNI como número).
     */
    Optional<StudentRecord> findByStudentId(StudentPersonalDataId studentId);

    /**
     * Busca el legajo por su número (DNI del estudiante).
     */
    Optional<StudentRecord> findByRecordNumber(RecordNumber recordNumber);

    /**
     * Busca todos los legajos de un año académico (para reportes administrativos).
     */
    List<StudentRecord> findAllByAcademicYearId(AcademicYearId academicYearId);

    /**
     * Verifica si ya existe un legajo para el estudiante.
     */
    boolean existsByStudentId(StudentPersonalDataId studentId);

    /**
     * Guarda o actualiza el legajo.
     */
    StudentRecord save(StudentRecord record);
}