package org.school.management.academic.infra.persistence.repository;

import org.school.management.academic.infra.persistence.entity.StudyPlanSubjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudyPlanSubjectJpaRepository extends JpaRepository<StudyPlanSubjectEntity, UUID> {

    List<StudyPlanSubjectEntity> findByStudyPlanId(UUID studyPlanId);

    List<StudyPlanSubjectEntity> findBySubjectId(UUID subjectId);

    Optional<StudyPlanSubjectEntity> findByStudyPlanIdAndSubjectId(UUID studyPlanId, UUID subjectId);

    @Query("""
            SELECT sps FROM StudyPlanSubjectEntity sps
            WHERE sps.studyPlanId = :studyPlanId
            AND sps.isMandatory = true
            """)
    List<StudyPlanSubjectEntity> findMandatorySubjects(@Param("studyPlanId") UUID studyPlanId);

    @Query("SELECT sps.subjectId FROM StudyPlanSubjectEntity sps WHERE sps.studyPlanId = :studyPlanId")
    List<UUID> findSubjectIdsByStudyPlanId(@Param("studyPlanId") UUID studyPlanId);

    @Query("SELECT sps.studyPlanId FROM StudyPlanSubjectEntity sps WHERE sps.subjectId = :subjectId")
    List<UUID> findStudyPlanIdsBySubjectId(@Param("subjectId") UUID subjectId);

    boolean existsByStudyPlanIdAndSubjectId(UUID studyPlanId, UUID subjectId);

    long countByStudyPlanId(UUID studyPlanId);

    void deleteByStudyPlanId(UUID studyPlanId);

    void deleteBySubjectId(UUID subjectId);
}
