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
import org.school.management.grades.application.dto.response.EvaluationResponse;
import org.school.management.grades.application.mapper.GradesApplicationMapper;
import org.school.management.grades.domain.exception.GradeAlreadyValidatedException;
import org.school.management.grades.domain.exception.GradeNotFoundException;
import org.school.management.grades.domain.exception.InvalidGradeException;
import org.school.management.grades.domain.model.Evaluation;
import org.school.management.grades.domain.repository.EvaluationRepository;
import org.school.management.grades.domain.valueobject.EvaluationId;
import org.school.management.grades.domain.valueobject.EvaluationStatus;
import org.school.management.grades.domain.valueobject.EvaluationTypeId;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("ValidateEvaluationUseCase")
class ValidateEvaluationUseCaseTest {

    @Mock private EvaluationRepository evaluationRepository;
    @Mock private GradesApplicationMapper mapper;

    @InjectMocks private ValidateEvaluationUseCase useCase;

    // ── Fixtures ──────────────────────────────────────────────────────────

    private static final UUID EVALUATION_UUID          = UUID.randomUUID();
    private static final UUID VALIDATED_BY             = UUID.randomUUID();
    private static final UUID STUDENT_COURSE_SUBJECT_UUID = UUID.randomUUID();
    private static final UUID PERIOD_UUID              = UUID.randomUUID();
    private static final UUID EVALUATION_TYPE_UUID     = UUID.randomUUID();

    private Evaluation buildGradedEvaluation() {
        return Evaluation.builder()
                .evaluationId(EvaluationId.of(EVALUATION_UUID))
                .studentCourseSubjectId(
                        StudentCourseSubjectId.of(STUDENT_COURSE_SUBJECT_UUID))
                .periodId(PeriodId.of(PERIOD_UUID))
                .evaluationTypeId(EvaluationTypeId.of(EVALUATION_TYPE_UUID))
                .title("Primer parcial")
                .evaluationDate(LocalDate.of(2026, 4, 15))
                .maxGrade(BigDecimal.TEN)
                .grade(BigDecimal.valueOf(8))
                .status(EvaluationStatus.GRADED)
                .isValidated(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(UUID.randomUUID())
                .build();
    }

    private Evaluation buildPendingEvaluation() {
        return buildGradedEvaluation().toBuilder()
                .grade(null)
                .status(EvaluationStatus.PENDING)
                .build();
    }

    private Evaluation buildAlreadyValidatedEvaluation() {
        return buildGradedEvaluation().toBuilder()
                .status(EvaluationStatus.VALIDATED)
                .isValidated(true)
                .validatedBy(VALIDATED_BY)
                .validatedAt(LocalDateTime.now())
                .build();
    }

    private EvaluationResponse buildValidatedResponse() {
        return new EvaluationResponse(
                EVALUATION_UUID, STUDENT_COURSE_SUBJECT_UUID,
                PERIOD_UUID, EVALUATION_TYPE_UUID,
                "Primer parcial", null,
                LocalDate.of(2026, 4, 15),
                BigDecimal.valueOf(8), BigDecimal.TEN,
                EvaluationStatus.VALIDATED,
                true, VALIDATED_BY, LocalDateTime.now(),
                null, null, true,
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

    // ── Tests ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("execute — flujo feliz — valida evaluación calificada")
    void execute_happyPath_validatesGradedEvaluation() {
        Evaluation graded = buildGradedEvaluation();
        EvaluationResponse response = buildValidatedResponse();

        when(evaluationRepository.findById(EvaluationId.from(EVALUATION_UUID)))
                .thenReturn(Optional.of(graded));
        when(evaluationRepository.save(any(Evaluation.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toEvaluationResponse(any(Evaluation.class))).thenReturn(response);

        EvaluationResponse result = useCase.execute(EVALUATION_UUID, VALIDATED_BY);

        assertThat(result).isNotNull();
        assertThat(result.isValidated()).isTrue();
        assertThat(result.status()).isEqualTo(EvaluationStatus.VALIDATED);
        assertThat(result.validatedBy()).isEqualTo(VALIDATED_BY);

        verify(evaluationRepository).save(any(Evaluation.class));
    }

    @Test
    @DisplayName("execute — evaluación no encontrada — lanza GradeNotFoundException")
    void execute_whenEvaluationNotFound_thenThrowGradeNotFoundException() {
        when(evaluationRepository.findById(EvaluationId.from(EVALUATION_UUID)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(EVALUATION_UUID, VALIDATED_BY))
                .isInstanceOf(GradeNotFoundException.class)
                .hasMessageContaining(EVALUATION_UUID.toString());

        verify(evaluationRepository, never()).save(any());
    }

    @Test
    @DisplayName("execute — evaluación ya validada — lanza GradeAlreadyValidatedException")
    void execute_whenAlreadyValidated_thenThrowGradeAlreadyValidatedException() {
        Evaluation alreadyValidated = buildAlreadyValidatedEvaluation();

        when(evaluationRepository.findById(EvaluationId.from(EVALUATION_UUID)))
                .thenReturn(Optional.of(alreadyValidated));

        assertThatThrownBy(() -> useCase.execute(EVALUATION_UUID, VALIDATED_BY))
                .isInstanceOf(GradeAlreadyValidatedException.class)
                .hasMessageContaining(EVALUATION_UUID.toString());

        verify(evaluationRepository, never()).save(any());
    }

    @Test
    @DisplayName("execute — evaluación sin nota — lanza InvalidGradeException")
    void execute_whenEvaluationHasNoGrade_thenThrowInvalidGradeException() {
        Evaluation pending = buildPendingEvaluation();

        when(evaluationRepository.findById(EvaluationId.from(EVALUATION_UUID)))
                .thenReturn(Optional.of(pending));

        assertThatThrownBy(() -> useCase.execute(EVALUATION_UUID, VALIDATED_BY))
                .isInstanceOf(InvalidGradeException.class)
                .hasMessageContaining(EVALUATION_UUID.toString());

        verify(evaluationRepository, never()).save(any());
    }
}