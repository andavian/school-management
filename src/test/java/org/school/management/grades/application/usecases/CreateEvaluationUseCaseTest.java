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
import org.school.management.grades.application.dto.request.CreateEvaluationRequest;
import org.school.management.grades.application.dto.response.EvaluationResponse;
import org.school.management.grades.application.mapper.GradesApplicationMapper;
import org.school.management.grades.domain.model.Evaluation;
import org.school.management.grades.domain.repository.EvaluationRepository;
import org.school.management.grades.domain.valueobject.EvaluationStatus;
import org.school.management.grades.domain.valueobject.EvaluationTypeId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("CreateEvaluationUseCase")
class CreateEvaluationUseCaseTest {

    @Mock private EvaluationRepository evaluationRepository;
    @Mock private GradesApplicationMapper mapper;

    @InjectMocks private CreateEvaluationUseCase useCase;

    // ── Fixtures ──────────────────────────────────────────────────────────

    private static final UUID CREATED_BY               = UUID.randomUUID();
    private static final UUID EVALUATION_UUID          = UUID.randomUUID();
    private static final UUID STUDENT_COURSE_SUBJECT_UUID = UUID.randomUUID();
    private static final UUID PERIOD_UUID              = UUID.randomUUID();
    private static final UUID EVALUATION_TYPE_UUID     = UUID.randomUUID();

    private CreateEvaluationRequest buildRequest() {
        return new CreateEvaluationRequest(
                STUDENT_COURSE_SUBJECT_UUID,
                PERIOD_UUID,
                EVALUATION_TYPE_UUID,
                "Primer parcial",
                "Unidades 1 a 3",
                LocalDate.of(2026, 4, 15)
        );
    }

    private Evaluation buildSavedEvaluation() {
        return Evaluation.builder()
                .evaluationId(
                        org.school.management.grades.domain.valueobject.EvaluationId
                                .of(EVALUATION_UUID))
                .studentCourseSubjectId(
                        StudentCourseSubjectId.of(STUDENT_COURSE_SUBJECT_UUID))
                .periodId(PeriodId.of(PERIOD_UUID))
                .evaluationTypeId(EvaluationTypeId.of(EVALUATION_TYPE_UUID))
                .title("Primer parcial")
                .description("Unidades 1 a 3")
                .evaluationDate(LocalDate.of(2026, 4, 15))
                .maxGrade(java.math.BigDecimal.TEN)
                .status(EvaluationStatus.PENDING)
                .isValidated(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(CREATED_BY)
                .build();
    }

    private EvaluationResponse buildEvaluationResponse() {
        return new EvaluationResponse(
                EVALUATION_UUID,
                STUDENT_COURSE_SUBJECT_UUID,
                PERIOD_UUID,
                EVALUATION_TYPE_UUID,
                "Primer parcial",
                "Unidades 1 a 3",
                LocalDate.of(2026, 4, 15),
                null,
                java.math.BigDecimal.TEN,
                EvaluationStatus.PENDING,
                false, null, null,
                null, null, false,
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

    // ── Tests ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("execute — flujo feliz — crea evaluación en estado PENDING")
    void execute_happyPath_createsEvaluationInPendingStatus() {
        CreateEvaluationRequest request = buildRequest();
        Evaluation saved = buildSavedEvaluation();
        EvaluationResponse response = buildEvaluationResponse();

        when(evaluationRepository.save(any(Evaluation.class))).thenReturn(saved);
        when(mapper.toEvaluationResponse(saved)).thenReturn(response);

        EvaluationResponse result = useCase.execute(request, CREATED_BY);

        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo(EvaluationStatus.PENDING);
        assertThat(result.isValidated()).isFalse();
        assertThat(result.grade()).isNull();

        verify(evaluationRepository).save(any(Evaluation.class));
    }

    @Test
    @DisplayName("execute — título vacío — lanza IllegalArgumentException")
    void execute_whenTitleIsBlank_thenThrowIllegalArgumentException() {
        CreateEvaluationRequest request = new CreateEvaluationRequest(
                STUDENT_COURSE_SUBJECT_UUID,
                PERIOD_UUID,
                EVALUATION_TYPE_UUID,
                "   ",
                null,
                LocalDate.of(2026, 4, 15)
        );

        assertThatThrownBy(() -> useCase.execute(request, CREATED_BY))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("title");

        verify(evaluationRepository, never()).save(any());
    }

    @Test
    @DisplayName("execute — fecha nula — lanza IllegalArgumentException")
    void execute_whenDateIsNull_thenThrowIllegalArgumentException() {
        CreateEvaluationRequest request = new CreateEvaluationRequest(
                STUDENT_COURSE_SUBJECT_UUID,
                PERIOD_UUID,
                EVALUATION_TYPE_UUID,
                "Primer parcial",
                null,
                null
        );

        assertThatThrownBy(() -> useCase.execute(request, CREATED_BY))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("date");

        verify(evaluationRepository, never()).save(any());
    }
}