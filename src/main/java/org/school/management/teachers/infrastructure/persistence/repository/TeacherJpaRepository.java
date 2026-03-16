package org.school.management.teachers.infrastructure.persistence.repository;

import org.school.management.teachers.infrastructure.persistence.entity.TeacherEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeacherJpaRepository extends JpaRepository<TeacherEntity, UUID> {

    Optional<TeacherEntity> findByDni(String dni);

    boolean existsByDni(String dni);

    boolean existsByCuil(String cuil);

    List<TeacherEntity> findByLastNameContainingIgnoreCase(String lastName);
}