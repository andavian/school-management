package org.school.management.grades.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.grades.application.dto.response.FinalGradeResponse;
import org.school.management.grades.application.mapper.GradesApplicationMapper;
import org.school.management.grades.domain.exception.InvalidGradeException;
import org.school.management.grades.domain.model.FinalGrade;
import org.school.management.grades.domain.model.PeriodGrade;
import org.school.management.grades.domain.repository.FinalGradeRepository;
import org.school.management.grades.domain.repository.PeriodGradeRepository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("CalculateFinalGradeUseCase")
class CalculateFinalGradeUseCaseTest {

    @Mock private PeriodGradeRepository periodGradeRepository;
    @Mock private FinalGradeRepository finalGradeRepository;
    @Mock private GradesApplicationMapper mapper;

    @InjectMocks private CalculateFinalGradeUseCase useCase;

    @Test
    @DisplayName("execute — flujo feliz — calcula nota final")
    void execute_happyPath_calculatesFinalGrade() {
        PeriodGrade pg = mock(PeriodGrade.class);
        when(pg.isValidated()).thenReturn(true);
        when(pg.getFinalPeriodGrade()).thenReturn(new BigDecimal("8.0"));

        when(periodGradeRepository.findByStudentCourseSubject(any())).thenReturn(List.of(pg));
        when(finalGradeRepository.save(any(FinalGrade.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toFinalGradeResponse(any(FinalGrade.class))).thenReturn(mock(FinalGradeResponse.class));

        FinalGradeResponse result = useCase.execute(UUID.randomUUID(), UUID.randomUUID());

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("execute — sin notas de período — lanza InvalidGradeException")
    void execute_noPeriodGrades_throwsException() {
        when(periodGradeRepository.findByStudentCourseSubject(any())).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> useCase.execute(UUID.randomUUID(), UUID.randomUUID()))
                .isInstanceOf(InvalidGradeException.class);
    }
}
