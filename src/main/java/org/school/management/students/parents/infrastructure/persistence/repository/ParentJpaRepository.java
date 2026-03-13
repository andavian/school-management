package org.school.management.students.parents.infrastructure.persistence.repository;

import org.school.management.students.parents.infrastructure.persistence.entity.ParentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ParentJpaRepository
        extends JpaRepository<ParentEntity, UUID> {

    Optional<ParentEntity> findByDni(String dni);

    Optional<ParentEntity> findByEmail(String email);

    boolean existsByDni(String dni);

    boolean existsByEmail(String email);
}