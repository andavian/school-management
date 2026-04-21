package org.school.management.students.records.infrastructure.persistence.repository;

import org.school.management.students.records.infrastructure.persistence.entity.DocumentTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface DocumentTypeJpaRepository extends JpaRepository<DocumentTypeEntity, UUID> {

    List<DocumentTypeEntity> findAllByActiveTrue();

    List<DocumentTypeEntity> findAllByCategory(String category);

    @Query("""
            SELECT dt FROM DocumentTypeEntity dt
            WHERE dt.active = true
              AND dt.category = :category
              AND (:mandatory IS NULL OR dt.mandatory = :mandatory)
            """)
    List<DocumentTypeEntity> findActiveByCategoryAndMandatory(
            @Param("category") String category,
            @Param("mandatory") Boolean mandatory
    );

    boolean existsByCode(String code);

    boolean existsByName(String name);
}