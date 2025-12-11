package org.school.management.academic.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.domain.exception.*;
import org.school.management.academic.domain.model.StudyPlan;
import org.school.management.academic.domain.model.Subject;
import org.school.management.academic.domain.repository.StudyPlanRepository;
import org.school.management.academic.domain.repository.SubjectRepository;
import org.school.management.academic.domain.valueobject.ids.StudyPlanId;
import org.school.management.academic.domain.valueobject.ids.SubjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StudyPlanManagementService {

    private final StudyPlanRepository studyPlanRepository;
    private final SubjectRepository subjectRepository;

    /**
     * Agrega una materia a un plan de estudio
     *
     * @throws StudyPlanNotFoundException    si el plan no existe
     * @throws SubjectNotFoundException      si la materia no existe
     * @throws SubjectAlreadyInPlanException si la materia ya está en el plan
     * @throws IncompatibleSubjectException  si la materia no es compatible con el plan
     */
    public void addSubjectToPlan(StudyPlanId planId, SubjectId subjectId) {
        log.info("Adding subject {} to study plan {}", subjectId, planId);

        // Verificar que el plan exista
        StudyPlan plan = studyPlanRepository.findById(planId)
                .orElseThrow(() -> new StudyPlanNotFoundException(
                        "Study plan not found: " + planId
                ));

        // Verificar que la materia exista
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new SubjectNotFoundException(
                        "Subject not found: " + subjectId
                ));

        // Validar compatibilidad
        validateSubjectCompatibility(plan, subject);

        // Verificar que no esté ya en el plan
        if (studyPlanRepository.hasSubject(planId, subjectId)) {
            throw new SubjectAlreadyInPlanException(
                    "Subject " + subject.getName() + " is already in this study plan"
            );
        }

        // Agregar la materia
        studyPlanRepository.addSubject(planId, subjectId);

        log.info("Successfully added subject {} to study plan {}",
                subject.getName(), plan.getName());
    }

    /**
     * Remueve una materia de un plan de estudio
     */
    public void removeSubjectFromPlan(StudyPlanId planId, SubjectId subjectId) {
        log.info("Removing subject {} from study plan {}", subjectId, planId);

        if (!studyPlanRepository.hasSubject(planId, subjectId)) {
            throw new SubjectNotInPlanException(
                    "Subject is not in this study plan"
            );
        }

        studyPlanRepository.removeSubject(planId, subjectId);
        log.info("Successfully removed subject from study plan");
    }

    /**
     * Obtiene todas las materias de un plan
     */
    public List<SubjectId> getPlanSubjects(StudyPlanId planId) {
        return studyPlanRepository.findSubjectIds(planId);
    }

    // Private helper
    private void validateSubjectCompatibility(StudyPlan plan, Subject subject) {
        // 1. Mismo año
        if (!plan.getYearLevel().equals(subject.getYearLevel())) {
            throw new IncompatibleSubjectException(
                    String.format("Subject is for year %d but plan is for year %d",
                            subject.getYearLevel().getValue(),
                            plan.getYearLevel().getValue())
            );
        }

        boolean planHasOrientation = plan.getOrientationId() != null;
        boolean subjectHasOrientation = subject.getOrientationId() != null;

        if (planHasOrientation && subjectHasOrientation) {
            if (!plan.getOrientationId().equals(subject.getOrientationId())) {
                throw new IncompatibleSubjectException("Subject and plan have different orientations");
            }
            return; // Si son compatibles y orientados, salimos.
        }

        if (!planHasOrientation && subjectHasOrientation) {
            // ERROR: Materia orientada en plan básico/común
            throw new IncompatibleSubjectException(
                    "Cannot add orientation-specific subject to common plan"
            );
        }
    }
}
