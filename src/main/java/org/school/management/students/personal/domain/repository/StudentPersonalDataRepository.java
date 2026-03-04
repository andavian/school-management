package org.school.management.students.personal.domain.repository;

import org.school.management.geography.domain.valueobject.PlaceId;
import org.school.management.students.personal.domain.model.StudentPersonalData;
import org.school.management.shared.person.domain.valueobject.Dni;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Port: StudentPersonalData (Hexagonal Architecture)
 *
 * Define las operaciones de persistencia para el agregado StudentPersonalData.
 * La implementación está en la capa de infraestructura.
 */
public interface StudentPersonalDataRepository {

    /**
     * Guarda o actualiza un estudiante.
     *
     * @param student Estudiante a persistir
     * @return Estudiante guardado
     */
    StudentPersonalData save(StudentPersonalData student);

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
     * Busca estudiantes cuyo nombre completo contenga el término de búsqueda.
     *
     * @param searchTerm Término a buscar (case-insensitive)
     * @return Lista de estudiantes que coinciden
     */
    List<StudentPersonalData> findByFullNameContaining(String searchTerm);

    /**
     * Busca estudiantes por lugar de residencia.
     *
     * @param residencePlaceId ID del lugar de residencia
     * @return Lista de estudiantes que viven en ese lugar
     */
    List<StudentPersonalData> findByResidencePlaceId(PlaceId residencePlaceId);

    /**
     * Obtiene todos los estudiantes.
     *
     * @return Lista de todos los estudiantes
     */
    List<StudentPersonalData> findAll();

    /**
     * Verifica si ya existe un estudiante con el DNI dado.
     *
     * @param dni DNI a verificar
     * @return true si ya existe, false en caso contrario
     */
    boolean existsByDni(Dni dni);

    /**
     * Cuenta el total de estudiantes.
     *
     * @return Cantidad total de estudiantes
     */
    long count();

    /**
     * Elimina un estudiante por su ID.
     *
     * Nota: En producción, considerar soft delete en lugar de eliminación física.
     *
     * @param studentId ID del estudiante a eliminar
     */
    void deleteByStudentId(UUID studentId);
}