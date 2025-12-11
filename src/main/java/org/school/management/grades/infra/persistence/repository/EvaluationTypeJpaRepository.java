package org.school.management.grades.infra.persistence.repository;

import org.school.management.grades.infra.persistence.entity.EvaluationTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EvaluationTypeJpaRepository extends JpaRepository<EvaluationTypeEntity, UUID> {

    Optional<EvaluationTypeEntity> findByCode(String code);

    List<EvaluationTypeEntity> findByIsActiveTrue();

    boolean existsByCode(String code);
}
