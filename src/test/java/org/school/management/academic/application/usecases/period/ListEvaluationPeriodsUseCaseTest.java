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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("ListEvaluationPeriodsUseCase")
class ListEvaluationPeriodsUseCaseTest {

    @Mock private EvaluationPeriodRepository evaluationPeriodRepository;
    @Mock private AcademicYearRepository academicYearRepository;
    @Mock private AcademicApplicationMapper mapper;

    @InjectMocks private ListEvaluationPeriodsUseCase useCase;

    private static final String ACADEMIC_YEAR_ID = UUID.randomUUID().toString();

    private EvaluationPeriod buildPeriod(int number, PeriodStatus status) {
        return EvaluationPeriod.builder()
                .periodId(new PeriodId(UUID.randomUUID()))
                .academicYearId(new AcademicYearId(UUID.fromString(ACADEMIC_YEAR_ID)))
                .periodNumber(PeriodNumber.of(number))
                .name("Period " + number)
                .startDate(LocalDate.of(2025, 1 + (number - 1) * 3, 1))
                .endDate(LocalDate.of(2025, 3 + (number - 1) * 3, 30))
                .isCurrent(status == PeriodStatus.ACTIVE)
                .status(status)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("execute — flujo feliz — lista períodos del año")
    void execute_happyPath_listsPeriods() {
        EvaluationPeriod p1 = buildPeriod(1, PeriodStatus.CLOSED);
        EvaluationPeriod p2 = buildPeriod(2, PeriodStatus.ACTIVE);

        when(academicYearRepository.findByAcademicYearId(any(AcademicYearId.class)))
                .thenReturn(Optional.of(mock(AcademicYear.class)));
        when(evaluationPeriodRepository.findByAcademicYear(any(AcademicYearId.class)))
                .thenReturn(List.of(p1, p2));
        when(mapper.toEvaluationPeriodResponse(any(EvaluationPeriod.class))).thenAnswer(inv -> {
            EvaluationPeriod p = inv.getArgument(0);
            return new EvaluationPeriodResponse(
                    p.getPeriodId().value().toString(), ACADEMIC_YEAR_ID, p.getPeriodNumber().value(),
                    p.getName(), p.getStartDate(), p.getEndDate(),
                    p.getIsCurrent(), p.getStatus().name(), p.getCreatedAt()
            );
        });

        List<EvaluationPeriodResponse> result = useCase.execute(ACADEMIC_YEAR_ID);

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("execute — año no encontrado — lanza AcademicYearNotFoundException")
    void execute_academicYearNotFound_throwsException() {
        when(academicYearRepository.findByAcademicYearId(any(AcademicYearId.class)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(ACADEMIC_YEAR_ID))
                .isInstanceOf(AcademicYearNotFoundException.class);
    }

    @Test
    @DisplayName("execute — lista vacía — retorna lista vacía")
    void execute_emptyList_returnsEmptyList() {
        when(academicYearRepository.findByAcademicYearId(any(AcademicYearId.class)))
                .thenReturn(Optional.of(mock(AcademicYear.class)));
        when(evaluationPeriodRepository.findByAcademicYear(any(AcademicYearId.class)))
                .thenReturn(List.of());

        List<EvaluationPeriodResponse> result = useCase.execute(ACADEMIC_YEAR_ID);

        assertThat(result).isEmpty();
    }
}
