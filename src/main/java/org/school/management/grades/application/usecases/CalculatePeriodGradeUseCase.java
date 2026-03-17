package org.school.management.grades.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.domain.valueobject.ids.PeriodId;
import org.school.management.course.domain.valueobject.StudentCourseSubjectId;
import org.school.management.grades.application.dto.response.PeriodGradeResponse;
import org.school.management.grades.application.mapper.GradesApplicationMapper;
import org.school.management.grades.domain.exception.InvalidGradeException;
import org.school.management.grades.domain.model.Evaluation;
import org.school.management.grades.domain.model.PeriodGrade;
import org.school.management.grades.domain.repository.EvaluationRepository;
import org.school.management.grades.domain.repository.PeriodGradeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CalculatePeriodGradeUseCase {

    private final EvaluationRepository evaluationRepository;
    private final PeriodGradeRepository periodGradeRepository;
    private final GradesApplicationMapper mapper;

    public PeriodGradeResponse execute(UUID studentCourseSubjectId, UUID periodId) {
        log.debug("Calculating period grade for studentCourseSubject: {} period: {}",
                studentCourseSubjectId, periodId);

        StudentCourseSubjectId scsId = StudentCourseSubjectId.from(studentCourseSubjectId);
        PeriodId pId = PeriodId.from(periodId);

        List<Evaluation> validatedEvaluations = evaluationRepository
                .findValidatedEvaluations(scsId, pId);

        if (validatedEvaluations.isEmpty()) {
            throw InvalidGradeException.withReason(
                    "No validated evaluations found for studentCourseSubject: "
                            + studentCourseSubjectId + " in period: " + periodId
            );
        }

        List<BigDecimal> grades = validatedEvaluations.stream()
                .map(Evaluation::getGrade)
                .toList();

        PeriodGrade periodGrade = periodGradeRepository
                .findByStudentCourseSubjectAndPeriod(scsId, pId)
                .orElseGet(() -> PeriodGrade.create(scsId, pId));

        PeriodGrade calculated = periodGrade.calculateAverage(grades);
        PeriodGrade saved = periodGradeRepository.save(calculated);

        log.info("Period grade calculated: {} for studentCourseSubject: {} period: {}",
                saved.getFinalPeriodGrade(), studentCourseSubjectId, periodId);

        return mapper.toPeriodGradeResponse(saved);
    }
}