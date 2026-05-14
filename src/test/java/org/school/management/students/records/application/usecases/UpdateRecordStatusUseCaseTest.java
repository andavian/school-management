package org.school.management.students.records.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.students.records.application.dto.request.UpdateRecordStatusRequest;
import org.school.management.students.records.application.dto.response.StudentRecordResponse;
import org.school.management.students.records.domain.exception.RecordNotFoundException;
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
@DisplayName("UpdateRecordStatusUseCase")
class UpdateRecordStatusUseCaseTest {

    @Mock private StudentRecordRepository recordRepository;
    @Mock private GetRecordByStudentIdUseCase getRecordByStudentIdUseCase;

    @InjectMocks private UpdateRecordStatusUseCase useCase;

    private static final UUID STUDENT_ID = UUID.randomUUID();

    @Test
    @DisplayName("execute — SUBMIT — envía legajo a revisión")
    void execute_submit_submitsForReview() {
        UpdateRecordStatusRequest request = new UpdateRecordStatusRequest(null, null, null, "SUBMIT", null, null);
        StudentRecord record = mock(StudentRecord.class);

        when(recordRepository.findByStudentId(any())).thenReturn(Optional.of(record));
        doNothing().when(record).submitForReview(any());
        when(recordRepository.save(any(StudentRecord.class))).thenReturn(record);
        when(getRecordByStudentIdUseCase.buildResponse(record)).thenReturn(mock(StudentRecordResponse.class));

        StudentRecordResponse result = useCase.execute(STUDENT_ID, request, UUID.randomUUID());

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("execute — APPROVE — aprueba legajo")
    void execute_approve_approvesRecord() {
        UpdateRecordStatusRequest request = new UpdateRecordStatusRequest(null, null, null, "APPROVE", "Todo correcto", null);
        StudentRecord record = mock(StudentRecord.class);

        when(recordRepository.findByStudentId(any())).thenReturn(Optional.of(record));
        doNothing().when(record).approve(any(), any());
        when(recordRepository.save(any(StudentRecord.class))).thenReturn(record);
        when(getRecordByStudentIdUseCase.buildResponse(record)).thenReturn(mock(StudentRecordResponse.class));

        StudentRecordResponse result = useCase.execute(STUDENT_ID, request, UUID.randomUUID());

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("execute — no encontrado — lanza RecordNotFoundException")
    void execute_notFound_throwsException() {
        UpdateRecordStatusRequest request = new UpdateRecordStatusRequest(null, null, null, "SUBMIT", null, null);

        when(recordRepository.findByStudentId(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(STUDENT_ID, request, UUID.randomUUID()))
                .isInstanceOf(RecordNotFoundException.class);
    }
}
