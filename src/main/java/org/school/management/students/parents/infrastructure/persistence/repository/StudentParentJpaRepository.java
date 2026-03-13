package org.school.management.students.parents.infrastructure.persistence.repository;

import org.school.management.students.parents.infrastructure.persistence.entity.StudentParentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentParentJpaRepository
        extends JpaRepository<StudentParentEntity, UUID> {

    Optional<StudentParentEntity> findByStudentIdAndParentId(
            UUID studentId,
            UUID parentId
    );

    List<StudentParentEntity> findAllByStudentId(UUID studentId);

    List<StudentParentEntity> findAllByParentId(UUID parentId);

    boolean existsByStudentIdAndParentId(UUID studentId, UUID parentId);

    @Query("""
            SELECT COUNT(sp) > 0 FROM StudentParentEntity sp
            WHERE sp.studentId = :studentId
            AND sp.isPrimaryContact = true
            """)
    boolean existsPrimaryContactForStudent(@Param("studentId") UUID studentId);
}