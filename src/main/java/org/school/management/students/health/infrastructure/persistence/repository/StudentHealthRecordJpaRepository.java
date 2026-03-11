package org.school.management.students.health.infrastructure.persistence.repository;

import org.school.management.students.health.infrastructure.persistence.entity.StudentHealthRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StudentHealthRecordJpaRepository extends JpaRepository<StudentHealthRecordEntity, UUID> {

    Optional<StudentHealthRecordEntity> findByStudentId(UUID studentId);

    boolean existsByStudentId(UUID studentId);

    void deleteByHealthRecordId(UUID healthRecordId);
}