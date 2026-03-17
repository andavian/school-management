package org.school.management.grades.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.grades.domain.valueobject.EvaluationTypeId;
import org.school.management.academic.domain.valueobject.ids.PeriodId;
import org.school.management.course.domain.valueobject.StudentCourseSubjectId;
import org.school.management.grades.application.dto.request.CreateEvaluationRequest;
import org.school.management.grades.application.dto.response.EvaluationResponse;
import org.school.management.grades.application.mapper.GradesApplicationMapper;
import org.school.management.grades.domain.model.Evaluation;
import org.school.management.grades.domain.repository.EvaluationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CreateEvaluationUseCase {

    private final EvaluationRepository evaluationRepository;
    private final GradesApplicationMapper mapper;

    public EvaluationResponse execute(CreateEvaluationRequest request, UUID createdBy) {
        log.debug("Creating evaluation for studentCourseSubject: {}",
                request.studentCourseSubjectId());

        Evaluation evaluation = Evaluation.create(
                StudentCourseSubjectId.from(request.studentCourseSubjectId()),
                PeriodId.from(request.periodId()),
                EvaluationTypeId.from(request.evaluationTypeId()),
                request.title(),
                request.description(),
                request.evaluationDate(),
                createdBy
        );

        Evaluation saved = evaluationRepository.save(evaluation);

        log.info("Evaluation created: {} for studentCourseSubject: {}",
                saved.getEvaluationId().asString(),
                request.studentCourseSubjectId());

        return mapper.toEvaluationResponse(saved);
    }
}