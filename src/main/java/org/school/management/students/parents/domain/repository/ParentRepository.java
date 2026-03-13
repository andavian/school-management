package org.school.management.students.parents.domain.repository;

import org.school.management.shared.person.domain.valueobject.Dni;
import org.school.management.shared.person.domain.valueobject.Email;
import org.school.management.students.parents.domain.model.Parent;
import org.school.management.students.parents.domain.valueobject.ParentId;

import java.util.Optional;

/**
 * Puerto del repositorio para el agregado Parent.
 * Parent es una entidad global — identificada por DNI.
 */
public interface ParentRepository {

    Optional<Parent> findByParentId(ParentId parentId);

    Optional<Parent> findByDni(Dni dni);

    Optional<Parent> findByEmail(Email email);

    boolean existsByDni(Dni dni);

    boolean existsByEmail(Email email);

    Parent save(Parent parent);
}