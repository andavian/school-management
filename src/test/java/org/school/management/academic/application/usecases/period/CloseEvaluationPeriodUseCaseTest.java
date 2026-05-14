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
import org.school.management.academic.domain.exception.EvaluationPeriodNotFoundException;
import org.school.management.academic.domain.model.EvaluationPeriod;
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
@DisplayName("CloseEvaluationPeriodUseCase")
class CloseEvaluationPeriodUseCaseTest {

    @Mock private EvaluationPeriodRepository evaluationPeriodRepository;
    @Mock private AcademicApplicationMapper mapper;

    @InjectMocks private CloseEvaluationPeriodUseCase useCase;

    private static final UUID PERIOD_UUID = UUID.randomUUID();
    private static final String PERIOD_ID = PERIOD_UUID.toString();

    private EvaluationPeriod buildActivePeriod() {
        return EvaluationPeriod.builder()
                .periodId(new PeriodId(PERIOD_UUID))
                .academicYearId(new AcademicYearId(UUID.randomUUID()))
                .periodNumber(PeriodNumber.of(1))
                .name("Primer Trimestre")
                .startDate(LocalDate.of(2025, 3, 1))
                .endDate(LocalDate.of(2025, 6, 15))
                .isCurrent(true)
                .status(PeriodStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("execute — flujo feliz — cierra un período")
    void execute_happyPath_closesPeriod() {
        EvaluationPeriod period = buildActivePeriod();

        when(evaluationPeriodRepository.findById(any(PeriodId.class))).thenReturn(Optional.of(period));
        when(evaluationPeriodRepository.save(any(EvaluationPeriod.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toEvaluationPeriodResponse(any(EvaluationPeriod.class))).thenAnswer(inv -> {
            EvaluationPeriod p = inv.getArgument(0);
            return new EvaluationPeriodResponse(
                    PERIOD_ID, UUID.randomUUID().toString(), 1, "Primer Trimestre",
                    LocalDate.of(2025, 3, 1), LocalDate.of(2025, 6, 15),
                    false, "CLOSED", LocalDateTime.now()
            );
        });

        EvaluationPeriodResponse result = useCase.execute(PERIOD_ID);

        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo("CLOSED");
    }

    @Test
    @DisplayName("execute — período no encontrado — lanza EvaluationPeriodNotFoundException")
    void execute_notFound_throwsException() {
        when(evaluationPeriodRepository.findById(any(PeriodId.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(PERIOD_ID))
                .isInstanceOf(EvaluationPeriodNotFoundException.class);

        verify(evaluationPeriodRepository, never()).save(any());
    }
}
