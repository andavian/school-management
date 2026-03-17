package org.school.management.grades.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.course.domain.valueobject.StudentCourseSubjectId;
import org.school.management.grades.application.dto.response.FinalGradeResponse;
import org.school.management.grades.application.mapper.GradesApplicationMapper;
import org.school.management.grades.domain.exception.GradeAlreadyRecordedInRegistryException;
import org.school.management.grades.domain.exception.GradeNotFoundException;
import org.school.management.grades.domain.exception.InvalidGradeException;
import org.school.management.grades.domain.model.FinalGrade;
import org.school.management.grades.domain.repository.FinalGradeRepository;
import org.school.management.grades.domain.valueobject.FinalGradeId;
import org.school.management.grades.domain.valueobject.FinalGradeStatus;
import org.school.management.students.records.application.dto.response.StudentRecordResponse;
import org.school.management.students.records.application.usecases.GetRecordByStudentIdUseCase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("RecordFinalGradeInRegistryUseCase")
class RecordFinalGradeInRegistryUseCaseTest {

    @Mock private FinalGradeRepository finalGradeRepository;
    @Mock private GetRecordByStudentIdUseCase getRecordByStudentIdUseCase;
    @Mock private GradesApplicationMapper mapper;

    @InjectMocks private RecordFinalGradeInRegistryUseCase useCase;

    // ── Fixtures ──────────────────────────────────────────────────────────

    private static final UUID FINAL_GRADE_UUID  = UUID.randomUUID();
    private static final UUID STUDENT_UUID      = UUID.randomUUID();
    private static final UUID REGISTRY_UUID     = UUID.randomUUID();
    private static final int  FOLIO_NUMBER      = 42;

    private FinalGrade buildValidatedFinalGrade() {
        return FinalGrade.builder()
                .finalGradeId(FinalGradeId.of(FINAL_GRADE_UUID))
                .studentCourseSubjectId(
                        StudentCourseSubjectId.of(UUID.randomUUID()))
                .academicYearId(AcademicYearId.of(UUID.randomUUID()))
                .periodAverage(BigDecimal.valueOf(8))
                .finalGrade(BigDecimal.valueOf(8))
                .status(FinalGradeStatus.PASSED)
                .isValidated(true)
                .validatedBy(UUID.randomUUID())
                .validatedAt(LocalDateTime.now())
                .recordedInRegistry(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private FinalGrade buildUnvalidatedFinalGrade() {
        return buildValidatedFinalGrade().toBuilder()
                .isValidated(false)
                .validatedBy(null)
                .validatedAt(null)
                .build();
    }

    private FinalGrade buildAlreadyRecordedFinalGrade() {
        return buildValidatedFinalGrade().toBuilder()
                .recordedInRegistry(true)
                .registryId(
                        org.school.management.academic.domain.valueobject.ids.RegistryId
                                .of(REGISTRY_UUID))
                .folioNumber(FOLIO_NUMBER)
                .recordedAt(LocalDateTime.now())
                .build();
    }

    private StudentRecordResponse buildStudentRecordResponse() {
        return new StudentRecordResponse(
                UUID.randomUUID(),          // recordId
                STUDENT_UUID,               // studentId
                UUID.randomUUID(),          // academicYearId
                "12345678",                 // recordNumber
                REGISTRY_UUID,             // registryId
                FOLIO_NUMBER,              // folioNumber
                null,                      // status
                null,                      // completenessPercentage
                null,                      // reviewedBy
                null,                      // reviewedAt
                null,                      // reviewObservations
                List.of(),                 // documents
                0,                         // totalDocuments
                false,                     // complete
                false,                     // hasExpiredDocuments
                false,                     // hasExpiringSoonDocuments
                Map.of(),                  // documentCountByStatus
                LocalDateTime.now(),       // createdAt
                LocalDateTime.now()        // updatedAt
        );
    }

    private StudentRecordResponse buildStudentRecordResponseSinFolio() {
        return new StudentRecordResponse(
                UUID.randomUUID(),          // recordId
                STUDENT_UUID,               // studentId
                UUID.randomUUID(),          // academicYearId
                "12345678",                 // recordNumber
                null,                      // registryId  ← null
                null,                      // folioNumber ← null
                null,                      // status
                null,                      // completenessPercentage
                null,                      // reviewedBy
                null,                      // reviewedAt
                null,                      // reviewObservations
                List.of(),                 // documents
                0,                         // totalDocuments
                false,                     // complete
                false,                     // hasExpiredDocuments
                false,                     // hasExpiringSoonDocuments
                Map.of(),                  // documentCountByStatus
                LocalDateTime.now(),       // createdAt
                LocalDateTime.now()        // updatedAt
        );
    }

    private FinalGradeResponse buildFinalGradeResponse() {
        return new FinalGradeResponse(
                FINAL_GRADE_UUID, UUID.randomUUID(), UUID.randomUUID(),
                BigDecimal.valueOf(8), null, BigDecimal.valueOf(8),
                FinalGradeStatus.PASSED,
                true, UUID.randomUUID(), LocalDateTime.now(),
                true, REGISTRY_UUID, FOLIO_NUMBER, LocalDateTime.now(),
                null, LocalDateTime.now(), LocalDateTime.now()
        );
    }

    // ── Tests ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("execute — flujo feliz — registra nota en folio del alumno")
    void execute_happyPath_recordsFinalGradeInStudentFolio() {
        FinalGrade validated = buildValidatedFinalGrade();
        StudentRecordResponse record = buildStudentRecordResponse();
        FinalGradeResponse response = buildFinalGradeResponse();

        when(finalGradeRepository.findById(FinalGradeId.from(FINAL_GRADE_UUID)))
                .thenReturn(Optional.of(validated));
        when(getRecordByStudentIdUseCase.execute(STUDENT_UUID))
                .thenReturn(record);
        when(finalGradeRepository.save(any(FinalGrade.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toFinalGradeResponse(any(FinalGrade.class))).thenReturn(response);

        FinalGradeResponse result = useCase.execute(FINAL_GRADE_UUID, STUDENT_UUID);

        assertThat(result).isNotNull();
        assertThat(result.recordedInRegistry()).isTrue();
        assertThat(result.registryId()).isEqualTo(REGISTRY_UUID);
        assertThat(result.folioNumber()).isEqualTo(FOLIO_NUMBER);

        verify(finalGradeRepository).save(any(FinalGrade.class));
        verify(getRecordByStudentIdUseCase).execute(STUDENT_UUID);
    }

    @Test
    @DisplayName("execute — nota final no encontrada — lanza GradeNotFoundException")
    void execute_whenFinalGradeNotFound_thenThrowGradeNotFoundException() {
        when(finalGradeRepository.findById(FinalGradeId.from(FINAL_GRADE_UUID)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(FINAL_GRADE_UUID, STUDENT_UUID))
                .isInstanceOf(GradeNotFoundException.class)
                .hasMessageContaining(FINAL_GRADE_UUID.toString());

        verify(finalGradeRepository, never()).save(any());
        verifyNoInteractions(getRecordByStudentIdUseCase);
    }

    @Test
    @DisplayName("execute — nota ya registrada en libro — lanza GradeAlreadyRecordedInRegistryException")
    void execute_whenAlreadyRecorded_thenThrowGradeAlreadyRecordedInRegistryException() {
        FinalGrade alreadyRecorded = buildAlreadyRecordedFinalGrade();

        when(finalGradeRepository.findById(FinalGradeId.from(FINAL_GRADE_UUID)))
                .thenReturn(Optional.of(alreadyRecorded));

        assertThatThrownBy(() -> useCase.execute(FINAL_GRADE_UUID, STUDENT_UUID))
                .isInstanceOf(GradeAlreadyRecordedInRegistryException.class)
                .hasMessageContaining(FINAL_GRADE_UUID.toString());

        verify(finalGradeRepository, never()).save(any());
        verifyNoInteractions(getRecordByStudentIdUseCase);
    }

    @Test
    @DisplayName("execute — nota no validada — lanza InvalidGradeException")
    void execute_whenFinalGradeNotValidated_thenThrowInvalidGradeException() {
        FinalGrade unvalidated = buildUnvalidatedFinalGrade();

        when(finalGradeRepository.findById(FinalGradeId.from(FINAL_GRADE_UUID)))
                .thenReturn(Optional.of(unvalidated));

        assertThatThrownBy(() -> useCase.execute(FINAL_GRADE_UUID, STUDENT_UUID))
                .isInstanceOf(InvalidGradeException.class)
                .hasMessageContaining(FINAL_GRADE_UUID.toString());

        verify(finalGradeRepository, never()).save(any());
        verifyNoInteractions(getRecordByStudentIdUseCase);
    }

    @Test
    @DisplayName("execute — legajo sin folio asignado — lanza InvalidGradeException")
    void execute_whenStudentRecordHasNoFolio_thenThrowInvalidGradeException() {
        FinalGrade validated = buildValidatedFinalGrade();
        StudentRecordResponse recordSinFolio = buildStudentRecordResponseSinFolio();

        when(finalGradeRepository.findById(FinalGradeId.from(FINAL_GRADE_UUID)))
                .thenReturn(Optional.of(validated));
        when(getRecordByStudentIdUseCase.execute(STUDENT_UUID))
                .thenReturn(recordSinFolio);

        assertThatThrownBy(() -> useCase.execute(FINAL_GRADE_UUID, STUDENT_UUID))
                .isInstanceOf(InvalidGradeException.class)
                .hasMessageContaining("registry");

        verify(finalGradeRepository, never()).save(any());
    }
}
