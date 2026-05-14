package org.school.management.academic.application.usecases.period;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.academic.application.dto.request.CreateEvaluationPeriodRequest;
import org.school.management.academic.application.dto.response.EvaluationPeriodResponse;
import org.school.management.academic.application.mappers.AcademicApplicationMapper;
import org.school.management.academic.domain.exception.AcademicYearNotFoundException;
import org.school.management.academic.domain.exception.EvaluationPeriodOverlapException;
import org.school.management.academic.domain.model.AcademicYear;
import org.school.management.academic.domain.model.EvaluationPeriod;
import org.school.management.academic.domain.repository.AcademicYearRepository;
import org.school.management.academic.domain.repository.EvaluationPeriodRepository;
import org.school.management.academic.domain.valueobject.enums.AcademicYearStatus;
import org.school.management.academic.domain.valueobject.enums.PeriodStatus;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.Year;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("CreateEvaluationPeriodUseCase")
class CreateEvaluationPeriodUseCaseTest {

    @Mock private EvaluationPeriodRepository evaluationPeriodRepository;
    @Mock private AcademicYearRepository academicYearRepository;
    @Mock private AcademicApplicationMapper mapper;

    @InjectMocks private CreateEvaluationPeriodUseCase useCase;

    private static final String ACADEMIC_YEAR_ID = UUID.randomUUID().toString();

    private CreateEvaluationPeriodRequest buildRequest() {
        return new CreateEvaluationPeriodRequest(1, "Primer Trimestre",
                LocalDate.of(2025, 3, 1), LocalDate.of(2025, 6, 15));
    }

    private EvaluationPeriodResponse buildResponse(EvaluationPeriod saved) {
        return new EvaluationPeriodResponse(
                saved.getPeriodId().value().toString(), saved.getAcademicYearId().value().toString(),
                1, "Primer Trimestre",
                LocalDate.of(2025, 3, 1), LocalDate.of(2025, 6, 15),
                false, "PENDING", LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("execute — flujo feliz — crea período de evaluación")
    void execute_happyPath_createsPeriod() {
        CreateEvaluationPeriodRequest request = buildRequest();

        when(academicYearRepository.findByAcademicYearId(any(AcademicYearId.class)))
                .thenReturn(Optional.of(mock(AcademicYear.class)));
        when(evaluationPeriodRepository.existsByAcademicYearAndPeriodNumber(any(), eq(1))).thenReturn(false);
        when(evaluationPeriodRepository.save(any(EvaluationPeriod.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toEvaluationPeriodResponse(any(EvaluationPeriod.class))).thenAnswer(inv -> {
            EvaluationPeriod p = inv.getArgument(0);
            return buildResponse(p);
        });

        EvaluationPeriodResponse result = useCase.execute(ACADEMIC_YEAR_ID, request);

        assertThat(result).isNotNull();
        assertThat(result.periodNumber()).isEqualTo(1);
        verify(evaluationPeriodRepository).save(any(EvaluationPeriod.class));
    }

    @Test
    @DisplayName("execute — año académico no encontrado — lanza AcademicYearNotFoundException")
    void execute_academicYearNotFound_throwsException() {
        CreateEvaluationPeriodRequest request = buildRequest();

        when(academicYearRepository.findByAcademicYearId(any(AcademicYearId.class)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(ACADEMIC_YEAR_ID, request))
                .isInstanceOf(AcademicYearNotFoundException.class);

        verify(evaluationPeriodRepository, never()).save(any());
    }

    @Test
    @DisplayName("execute — número de período duplicado — lanza EvaluationPeriodOverlapException")
    void execute_duplicatePeriodNumber_throwsException() {
        CreateEvaluationPeriodRequest request = buildRequest();

        when(academicYearRepository.findByAcademicYearId(any(AcademicYearId.class)))
                .thenReturn(Optional.of(mock(AcademicYear.class)));
        when(evaluationPeriodRepository.existsByAcademicYearAndPeriodNumber(any(), eq(1))).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(ACADEMIC_YEAR_ID, request))
                .isInstanceOf(EvaluationPeriodOverlapException.class);

        verify(evaluationPeriodRepository, never()).save(any());
    }

    @Test
    @DisplayName("execute — fechas inválidas (end antes que start) — lanza IllegalArgumentException")
    void execute_endDateBeforeStart_throwsException() {
        CreateEvaluationPeriodRequest request = new CreateEvaluationPeriodRequest(1, "Invertido",
                LocalDate.of(2025, 6, 15), LocalDate.of(2025, 3, 1));

        when(academicYearRepository.findByAcademicYearId(any(AcademicYearId.class)))
                .thenReturn(Optional.of(mock(AcademicYear.class)));
        when(evaluationPeriodRepository.existsByAcademicYearAndPeriodNumber(any(), eq(1))).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(ACADEMIC_YEAR_ID, request))
                .isInstanceOf(IllegalArgumentException.class);

        verify(evaluationPeriodRepository, never()).save(any());
    }
}
