package org.school.management.students.personal.domain.repository;


import org.school.management.students.personal.domain.model.StudentPersonalData;
import org.school.management.shared.person.domain.valueobject.Dni;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para el agregado raíz StudentPersonalData.
 * <p>
 * Este repositorio opera únicamente sobre la raíz del agregado.
 * </p>
 */
public interface StudentPersonalDataRepository {

    /**
     * Busca un estudiante por su ID único.
     *
     * @param studentId ID del estudiante
     * @return Optional con el estudiante, o vacío si no existe
     */
    Optional<StudentPersonalData> findByStudentId(UUID studentId);

    /**
     * Busca un estudiante por su DNI (único en el sistema).
     *
     * @param dni DNI del estudiante
     * @return Optional con el estudiante, o vacío si no existe
     */
    Optional<StudentPersonalData> findByDni(Dni dni);

    /**
     * Guarda o actualiza un estudiante.
     *
     * @param student Estudiante a persistir
     * @return Estudiante guardado
     */
    StudentPersonalData save(StudentPersonalData student);

    /**
     * Verifica si ya existe un estudiante con el DNI dado.
     *
     * @param dni DNI a verificar
     * @return true si ya existe, false en caso contrario
     */
    boolean existsByDni(Dni dni);

    /**
     * Elimina un estudiante por su ID.
     *
     * @param studentId ID del estudiante a eliminar
     */
    void deleteByStudentId(UUID studentId);
}