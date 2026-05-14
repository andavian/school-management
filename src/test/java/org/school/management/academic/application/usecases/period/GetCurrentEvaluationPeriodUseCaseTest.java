package org.school.management.academic.application.usecases.period;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.academic.application.dto.response.EvaluationPeriodResponse;
import org.school.management.academic.application.mappers.AcademicApplicationMapper;
import org.school.management.academic.domain.exception.AcademicYearNotFoundException;
import org.school.management.academic.domain.exception.EvaluationPeriodNotFoundException;
import org.school.management.academic.domain.model.AcademicYear;
import org.school.management.academic.domain.model.EvaluationPeriod;
import org.school.management.academic.domain.repository.AcademicYearRepository;
import org.school.management.academic.domain.repository.EvaluationPeriodRepository;
import org.school.management.academic.domain.valueobject.PeriodNumber;
import org.school.management.academic.domain.valueobject.enums.PeriodStatus;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.PeriodId;

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
@DisplayName("GetCurrentEvaluationPeriodUseCase")
class GetCurrentEvaluationPeriodUseCaseTest {

    @Mock private EvaluationPeriodRepository evaluationPeriodRepository;
    @Mock private AcademicYearRepository academicYearRepository;
    @Mock private AcademicApplicationMapper mapper;

    @InjectMocks private GetCurrentEvaluationPeriodUseCase useCase;

    private static final String ACADEMIC_YEAR_ID = UUID.randomUUID().toString();

    @Test
    @DisplayName("execute — flujo feliz — retorna el período actual")
    void execute_happyPath_returnsCurrentPeriod() {
        EvaluationPeriod period = EvaluationPeriod.builder()
                .periodId(new PeriodId(UUID.randomUUID()))
                .academicYearId(new AcademicYearId(UUID.fromString(ACADEMIC_YEAR_ID)))
                .periodNumber(PeriodNumber.of(1))
                .name("Primer Trimestre")
                .startDate(LocalDate.of(2025, 3, 1))
                .endDate(LocalDate.of(2025, 6, 15))
                .isCurrent(true)
                .status(PeriodStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        EvaluationPeriodResponse response = new EvaluationPeriodResponse(
                period.getPeriodId().value().toString(), ACADEMIC_YEAR_ID, 1, "Primer Trimestre",
                LocalDate.of(2025, 3, 1), LocalDate.of(2025, 6, 15),
                true, "ACTIVE", LocalDateTime.now()
        );

        when(academicYearRepository.findByAcademicYearId(any(AcademicYearId.class)))
                .thenReturn(Optional.of(mock(AcademicYear.class)));
        when(evaluationPeriodRepository.findCurrentPeriod(any(AcademicYearId.class)))
                .thenReturn(Optional.of(period));
        when(mapper.toEvaluationPeriodResponse(period)).thenReturn(response);

        EvaluationPeriodResponse result = useCase.execute(ACADEMIC_YEAR_ID);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("execute — año académico no encontrado — lanza AcademicYearNotFoundException")
    void execute_academicYearNotFound_throwsException() {
        when(academicYearRepository.findByAcademicYearId(any(AcademicYearId.class)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(ACADEMIC_YEAR_ID))
                .isInstanceOf(AcademicYearNotFoundException.class);
    }

    @Test
    @DisplayName("execute — no hay período actual — lanza EvaluationPeriodNotFoundException")
    void execute_noCurrentPeriod_throwsException() {
        when(academicYearRepository.findByAcademicYearId(any(AcademicYearId.class)))
                .thenReturn(Optional.of(mock(AcademicYear.class)));
        when(evaluationPeriodRepository.findCurrentPeriod(any(AcademicYearId.class)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(ACADEMIC_YEAR_ID))
                .isInstanceOf(EvaluationPeriodNotFoundException.class);
    }
}
