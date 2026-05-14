package org.school.management.grades.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.grades.application.dto.request.RecordExamGradeRequest;
import org.school.management.grades.application.dto.response.FinalGradeResponse;
import org.school.management.grades.application.mapper.GradesApplicationMapper;
import org.school.management.grades.domain.exception.GradeNotFoundException;
import org.school.management.grades.domain.exception.InvalidGradeException;
import org.school.management.grades.domain.model.FinalGrade;
import org.school.management.grades.domain.repository.FinalGradeRepository;
import org.school.management.grades.domain.valueobject.FinalGradeStatus;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("RecordExamGradeUseCase")
class RecordExamGradeUseCaseTest {

    @Mock private FinalGradeRepository finalGradeRepository;
    @Mock private GradesApplicationMapper mapper;

    @InjectMocks private RecordExamGradeUseCase useCase;

    @Test
    @DisplayName("execute — flujo feliz — registra nota de examen")
    void execute_happyPath_recordsExam() {
        RecordExamGradeRequest request = new RecordExamGradeRequest(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("7.0"), null);
        FinalGrade finalGrade = mock(FinalGrade.class);
        FinalGrade withExam = mock(FinalGrade.class);

        when(finalGrade.getStatus()).thenReturn(FinalGradeStatus.PENDING_EXAM);
        when(finalGrade.recordExam(any(BigDecimal.class))).thenReturn(withExam);
        when(finalGradeRepository.findByStudentCourseSubjectAndYear(any(), any())).thenReturn(Optional.of(finalGrade));
        when(finalGradeRepository.save(any(FinalGrade.class))).thenReturn(withExam);
        when(mapper.toFinalGradeResponse(any(FinalGrade.class))).thenReturn(mock(FinalGradeResponse.class));

        FinalGradeResponse result = useCase.execute(request);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("execute — no encontrada — lanza GradeNotFoundException")
    void execute_notFound_throwsException() {
        RecordExamGradeRequest request = new RecordExamGradeRequest(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("7.0"), null);

        when(finalGradeRepository.findByStudentCourseSubjectAndYear(any(), any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(GradeNotFoundException.class);
    }

    @Test
    @DisplayName("execute — estado distinto de PENDING_EXAM — lanza InvalidGradeException")
    void execute_wrongStatus_throwsException() {
        RecordExamGradeRequest request = new RecordExamGradeRequest(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("7.0"), null);
        FinalGrade finalGrade = mock(FinalGrade.class);
        when(finalGrade.getStatus()).thenReturn(FinalGradeStatus.PASSED);

        when(finalGradeRepository.findByStudentCourseSubjectAndYear(any(), any())).thenReturn(Optional.of(finalGrade));

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(InvalidGradeException.class);
    }
}
