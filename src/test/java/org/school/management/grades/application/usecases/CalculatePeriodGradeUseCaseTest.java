package org.school.management.grades.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.academic.domain.valueobject.ids.PeriodId;
import org.school.management.course.domain.valueobject.StudentCourseSubjectId;
import org.school.management.grades.application.dto.response.PeriodGradeResponse;
import org.school.management.grades.application.mapper.GradesApplicationMapper;
import org.school.management.grades.domain.exception.InvalidGradeException;
import org.school.management.grades.domain.model.Evaluation;
import org.school.management.grades.domain.model.PeriodGrade;
import org.school.management.grades.domain.repository.EvaluationRepository;
import org.school.management.grades.domain.repository.PeriodGradeRepository;
import org.school.management.grades.domain.valueobject.EvaluationId;
import org.school.management.grades.domain.valueobject.EvaluationStatus;
import org.school.management.grades.domain.valueobject.EvaluationTypeId;
import org.school.management.grades.domain.valueobject.PeriodGradeId;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("CalculatePeriodGradeUseCase")
class CalculatePeriodGradeUseCaseTest {

    @Mock private EvaluationRepository evaluationRepository;
    @Mock private PeriodGradeRepository periodGradeRepository;
    @Mock private GradesApplicationMapper mapper;

    @InjectMocks private CalculatePeriodGradeUseCase useCase;

    // ── Fixtures ──────────────────────────────────────────────────────────

    private static final UUID STUDENT_COURSE_SUBJECT_UUID = UUID.randomUUID();
    private static final UUID PERIOD_UUID                 = UUID.randomUUID();
    private static final UUID PERIOD_GRADE_UUID           = UUID.randomUUID();

    private Evaluation buildValidatedEvaluation(BigDecimal grade) {
        return Evaluation.builder()
                .evaluationId(EvaluationId.generate())
                .studentCourseSubjectId(
                        StudentCourseSubjectId.of(STUDENT_COURSE_SUBJECT_UUID))
                .periodId(PeriodId.of(PERIOD_UUID))
                .evaluationTypeId(EvaluationTypeId.generate())
                .title("Parcial")
                .evaluationDate(LocalDate.of(2026, 4, 15))
                .maxGrade(BigDecimal.TEN)
                .grade(grade)
                .status(EvaluationStatus.VALIDATED)
                .isValidated(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(UUID.randomUUID())
                .build();
    }

    private PeriodGrade buildExistingPeriodGrade() {
        return PeriodGrade.builder()
                .periodGradeId(PeriodGradeId.of(PERIOD_GRADE_UUID))
                .studentCourseSubjectId(
                        StudentCourseSubjectId.of(STUDENT_COURSE_SUBJECT_UUID))
                .periodId(PeriodId.of(PERIOD_UUID))
                .isValidated(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private PeriodGradeResponse buildPeriodGradeResponse(BigDecimal average) {
        return new PeriodGradeResponse(
                PERIOD_GRADE_UUID,
                STUDENT_COURSE_SUBJECT_UUID,
                PERIOD_UUID,
                average, null, average,
                null, false, null, null,
                null,
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

    // ── Tests ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("execute — flujo feliz — calcula promedio de evaluaciones validadas")
    void execute_happyPath_calculatesAverageFromValidatedEvaluations() {
        List<Evaluation> evaluations = List.of(
                buildValidatedEvaluation(BigDecimal.valueOf(8)),
                buildValidatedEvaluation(BigDecimal.valueOf(6))
        );
        PeriodGrade existing = buildExistingPeriodGrade();
        PeriodGradeResponse response = buildPeriodGradeResponse(BigDecimal.valueOf(7.00));

        when(evaluationRepository.findValidatedEvaluations(
                StudentCourseSubjectId.of(STUDENT_COURSE_SUBJECT_UUID),
                PeriodId.of(PERIOD_UUID)))
                .thenReturn(evaluations);
        when(periodGradeRepository.findByStudentCourseSubjectAndPeriod(
                StudentCourseSubjectId.of(STUDENT_COURSE_SUBJECT_UUID),
                PeriodId.of(PERIOD_UUID)))
                .thenReturn(Optional.of(existing));
        when(periodGradeRepository.save(any(PeriodGrade.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toPeriodGradeResponse(any(PeriodGrade.class))).thenReturn(response);

        PeriodGradeResponse result = useCase.execute(
                STUDENT_COURSE_SUBJECT_UUID, PERIOD_UUID);

        assertThat(result).isNotNull();
        assertThat(result.finalPeriodGrade())
                .isEqualByComparingTo(BigDecimal.valueOf(7.00));

        verify(periodGradeRepository).save(any(PeriodGrade.class));
    }

    @Test
    @DisplayName("execute — sin evaluaciones validadas — lanza InvalidGradeException")
    void execute_whenNoValidatedEvaluations_thenThrowInvalidGradeException() {
        when(evaluationRepository.findValidatedEvaluations(
                StudentCourseSubjectId.of(STUDENT_COURSE_SUBJECT_UUID),
                PeriodId.of(PERIOD_UUID)))
                .thenReturn(List.of());

        assertThatThrownBy(() -> useCase.execute(
                STUDENT_COURSE_SUBJECT_UUID, PERIOD_UUID))
                .isInstanceOf(InvalidGradeException.class)
                .hasMessageContaining("No validated evaluations");

        verify(periodGradeRepository, never()).save(any());
    }

    @Test
    @DisplayName("execute — sin period grade existente — crea uno nuevo")
    void execute_whenNoPeriodGradeExists_thenCreatesNew() {
        List<Evaluation> evaluations = List.of(
                buildValidatedEvaluation(BigDecimal.valueOf(9))
        );
        PeriodGradeResponse response = buildPeriodGradeResponse(BigDecimal.valueOf(9.00));

        when(evaluationRepository.findValidatedEvaluations(
                StudentCourseSubjectId.of(STUDENT_COURSE_SUBJECT_UUID),
                PeriodId.of(PERIOD_UUID)))
                .thenReturn(evaluations);
        when(periodGradeRepository.findByStudentCourseSubjectAndPeriod(
                StudentCourseSubjectId.of(STUDENT_COURSE_SUBJECT_UUID),
                PeriodId.of(PERIOD_UUID)))
                .thenReturn(Optional.empty());
        when(periodGradeRepository.save(any(PeriodGrade.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toPeriodGradeResponse(any(PeriodGrade.class))).thenReturn(response);

        PeriodGradeResponse result = useCase.execute(
                STUDENT_COURSE_SUBJECT_UUID, PERIOD_UUID);

        assertThat(result).isNotNull();
        verify(periodGradeRepository).save(any(PeriodGrade.class));
    }
}