
package org.school.management.grades.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.grades.application.dto.response.EvaluationResponse;
import org.school.management.grades.application.mapper.GradesApplicationMapper;
import org.school.management.grades.domain.exception.GradeAlreadyValidatedException;
import org.school.management.grades.domain.exception.GradeNotFoundException;
import org.school.management.grades.domain.exception.InvalidGradeException;
import org.school.management.grades.domain.model.Evaluation;
import org.school.management.grades.domain.repository.EvaluationRepository;
import org.school.management.grades.domain.valueobject.EvaluationId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ValidateEvaluationUseCase {

    private final EvaluationRepository evaluationRepository;
    private final GradesApplicationMapper mapper;

    public EvaluationResponse execute(UUID evaluationId, UUID validatedBy) {
        log.debug("Validating evaluation: {}", evaluationId);

        Evaluation evaluation = evaluationRepository
                .findById(EvaluationId.from(evaluationId))
                .orElseThrow(() -> GradeNotFoundException.evaluation(evaluationId));

        if (evaluation.isValidated()) {
            throw GradeAlreadyValidatedException.evaluation(evaluationId);
        }

        if (!evaluation.isGraded()) {
            throw InvalidGradeException.missingGradeForValidation(evaluationId);
        }

        Evaluation validated = evaluation.validate(validatedBy);
        Evaluation saved = evaluationRepository.save(validated);

        log.info("Evaluation validated: {} by: {}", evaluationId, validatedBy);

        return mapper.toEvaluationResponse(saved);
    }
}