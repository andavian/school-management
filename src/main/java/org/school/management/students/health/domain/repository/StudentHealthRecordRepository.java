package org.school.management.students.health.domain.repository;

import org.school.management.students.health.domain.model.StudentHealthRecord;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para el agregado raíz StudentHealthRecord.
 * <p>
 * Este repositorio opera únicamente sobre la raíz del agregado.
 * </p>
 */
public interface StudentHealthRecordRepository {

    /**
     * Busca un registro de salud por su ID único.
     *
     * @param healthRecordId ID del registro de salud
     * @return Optional con el registro, o vacío si no existe
     */
    Optional<StudentHealthRecord> findByHealthRecordId(UUID healthRecordId);

    /**
     * Busca un registro de salud por el ID del estudiante.
     *
     * @param studentId ID del estudiante
     * @return Optional con el registro, o vacío si no existe
     */
    Optional<StudentHealthRecord> findByStudentId(StudentPersonalDataId studentId);

    /**
     * Guarda o actualiza un registro de salud.
     *
     * @param healthRecord Registro de salud a persistir
     * @return Registro guardado
     */
    StudentHealthRecord save(StudentHealthRecord healthRecord);

    /**
     * Verifica si ya existe un registro de salud para el estudiante dado.
     *
     * @param studentId ID del estudiante
     * @return true si ya existe, false en caso contrario
     */
    boolean existsByStudentId(StudentPersonalDataId studentId);

    /**
     * Elimina un registro de salud por su ID.
     *
     * @param healthRecordId ID del registro a eliminar
     */
    void deleteByHealthRecordId(UUID healthRecordId);
}