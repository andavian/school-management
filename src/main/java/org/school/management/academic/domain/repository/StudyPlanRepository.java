package org.school.management.academic.domain.repository;

import org.school.management.academic.domain.model.*;
import org.school.management.academic.domain.valueobject.YearLevel;
import org.school.management.academic.domain.valueobject.ids.*;

import java.util.List;
import java.util.Optional;



public interface StudyPlanRepository {
    StudyPlan save(StudyPlan studyPlan);
    Optional<StudyPlan> findById(StudyPlanId studyPlanId);
    Optional<StudyPlan> findByCode(String code);
    List<StudyPlan> findAll();

    List<StudyPlan> findByYearLevel(YearLevel yearLevel);

    Optional<StudyPlan> findByYearLevelAndOrientation(YearLevel yearLevel, OrientationId orientationId);

    List<StudyPlan> findActiveStudyPlans();
    boolean existsByCode(String code);
    void delete(StudyPlanId id);

    List<StudyPlan> findByYearLevelAndOrientationIdIsNull(YearLevel yearLevel);
    List<StudyPlan> findByOrientation(OrientationId orientationId);
    long countByYearLevel(YearLevel yearLevel);
    List<StudyPlan> findApplicableForGradeLevel(YearLevel yearLevel, OrientationId orientationId);

    void addSubject(StudyPlanId planId, SubjectId subjectId);
    void removeSubject(StudyPlanId planId, SubjectId subjectId);
    List<SubjectId> findSubjectIds(StudyPlanId planId);
    boolean hasSubject(StudyPlanId planId, SubjectId subjectId);
    List<SubjectId> findMandatorySubjectIds(StudyPlanId planId);
    List<StudyPlanId> findStudyPlanIdsBySubject(SubjectId subjectId);
    long countSubjects(StudyPlanId planId);
    void deleteSubjectLinksByStudyPlan(StudyPlanId planId);
    void deleteSubjectLinksBySubject(SubjectId subjectId);
}

