package org.school.management.teachers.domain.repository;

import org.school.management.shared.person.domain.valueobject.Dni;
import org.school.management.teachers.domain.model.Teacher;
import org.school.management.teachers.domain.valueobject.TeacherId;

import java.util.List;
import java.util.Optional;

/**
 * Puerto del dominio — nunca importar JPA ni Spring aquí.
 * Implementado por TeacherRepositoryAdapter en infrastructure/persistence/adapter/.
 */
public interface TeacherRepository {

    Optional<Teacher> findByTeacherId(TeacherId id);

    Optional<Teacher> findByDni(Dni dni);

    boolean existsByDni(Dni dni);

    boolean existsByCuil(String cuil);

    List<Teacher> findByLastName(String lastName);

    Teacher save(Teacher teacher);

    List<Teacher> findAll();
}