package org.school.management.students.records.infrastructure.persistence.repository;

import org.school.management.students.records.infrastructure.persistence.entity.StudentRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentRecordJpaRepository
        extends JpaRepository<StudentRecordEntity, UUID> {

    Optional<StudentRecordEntity> findByStudentId(UUID studentId);

    Optional<StudentRecordEntity> findByRecordNumber(String recordNumber);

    List<StudentRecordEntity> findAllByAcademicYearId(UUID academicYearId);

    boolean existsByStudentId(UUID studentId);
}