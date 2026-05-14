package org.school.management.students.records.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.students.records.application.dto.response.StudentRecordResponse;
import org.school.management.students.records.domain.exception.DocumentNotFoundException;
import org.school.management.students.records.domain.exception.RecordNotFoundException;
import org.school.management.students.records.domain.model.RecordDocument;
import org.school.management.students.records.domain.model.StudentRecord;
import org.school.management.students.records.domain.repository.StudentRecordRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("ReviewDocumentUseCase")
class ReviewDocumentUseCaseTest {

    @Mock private StudentRecordRepository recordRepository;
    @Mock private GetRecordByStudentIdUseCase getRecordByStudentIdUseCase;

    @InjectMocks private ReviewDocumentUseCase useCase;

    private static final UUID STUDENT_ID = UUID.randomUUID();
    private static final UUID DOCUMENT_ID = UUID.randomUUID();

    @Test
    @DisplayName("execute — APPROVE — aprueba documento")
    void execute_approve_approvesDocument() {
        StudentRecord record = mock(StudentRecord.class);
        RecordDocument document = mock(RecordDocument.class);

        when(recordRepository.findByStudentId(any())).thenReturn(Optional.of(record));
        when(record.getDocument(any())).thenReturn(Optional.of(document));
        doNothing().when(document).approve(any());
        when(recordRepository.save(any(StudentRecord.class))).thenReturn(record);
        when(getRecordByStudentIdUseCase.buildResponse(record)).thenReturn(mock(StudentRecordResponse.class));

        StudentRecordResponse result = useCase.execute(STUDENT_ID, DOCUMENT_ID, "APPROVE", "Todo ok");

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("execute — REJECT — rechaza documento")
    void execute_reject_rejectsDocument() {
        StudentRecord record = mock(StudentRecord.class);
        RecordDocument document = mock(RecordDocument.class);

        when(recordRepository.findByStudentId(any())).thenReturn(Optional.of(record));
        when(record.getDocument(any())).thenReturn(Optional.of(document));
        doNothing().when(document).reject(any());
        when(recordRepository.save(any(StudentRecord.class))).thenReturn(record);
        when(getRecordByStudentIdUseCase.buildResponse(record)).thenReturn(mock(StudentRecordResponse.class));

        StudentRecordResponse result = useCase.execute(STUDENT_ID, DOCUMENT_ID, "REJECT", "Falta firma");

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("execute — acción inválida — lanza IllegalArgumentException")
    void execute_invalidAction_throwsException() {
        StudentRecord record = mock(StudentRecord.class);
        RecordDocument document = mock(RecordDocument.class);

        when(recordRepository.findByStudentId(any())).thenReturn(Optional.of(record));
        when(record.getDocument(any())).thenReturn(Optional.of(document));

        assertThatThrownBy(() -> useCase.execute(STUDENT_ID, DOCUMENT_ID, "INVALID", ""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("execute — legajo no encontrado — lanza RecordNotFoundException")
    void execute_recordNotFound_throwsException() {
        when(recordRepository.findByStudentId(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(STUDENT_ID, DOCUMENT_ID, "APPROVE", ""))
                .isInstanceOf(RecordNotFoundException.class);
    }
}
