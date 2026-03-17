package org.school.management.grades.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.grades.domain.valueobject.EvaluationId;
import org.school.management.grades.application.dto.request.GradeEvaluationRequest;
import org.school.management.grades.application.dto.response.EvaluationResponse;
import org.school.management.grades.application.mapper.GradesApplicationMapper;
import org.school.management.grades.domain.exception.GradeAlreadyValidatedException;
import org.school.management.grades.domain.exception.GradeNotFoundException;
import org.school.management.grades.domain.model.Evaluation;
import org.school.management.grades.domain.repository.EvaluationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GradeEvaluationUseCase {

    private final EvaluationRepository evaluationRepository;
    private final GradesApplicationMapper mapper;

    public EvaluationResponse execute(UUID evaluationId, GradeEvaluationRequest request) {
        log.debug("Grading evaluation: {}", evaluationId);

        Evaluation evaluation = evaluationRepository
                .findById(EvaluationId.from(evaluationId))
                .orElseThrow(() -> GradeNotFoundException.evaluation(evaluationId));

        if (evaluation.isValidated()) {
            throw GradeAlreadyValidatedException.evaluation(evaluationId);
        }

        Evaluation graded = evaluation.gradeEvaluation(
                request.grade(),
                request.teacherObservations()
        );

        Evaluation saved = evaluationRepository.save(graded);

        log.info("Evaluation graded: {} with grade: {}",
                evaluationId, request.grade());

        return mapper.toEvaluationResponse(saved);
    }
}