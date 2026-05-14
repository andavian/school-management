package org.school.management.students.records.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.students.records.application.dto.request.AddDocumentRequest;
import org.school.management.students.records.application.dto.response.StudentRecordResponse;
import org.school.management.students.records.domain.exception.RecordNotFoundException;
import org.school.management.students.records.domain.model.StudentRecord;
import org.school.management.students.records.domain.repository.StudentRecordRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("AddDocumentToRecordUseCase")
class AddDocumentToRecordUseCaseTest {

    @Mock private StudentRecordRepository recordRepository;
    @Mock private GetRecordByStudentIdUseCase getRecordByStudentIdUseCase;

    @InjectMocks private AddDocumentToRecordUseCase useCase;

    private static final UUID STUDENT_ID = UUID.randomUUID();

    private AddDocumentRequest buildRequest() {
        return new AddDocumentRequest(UUID.randomUUID(), "Certificado", "", "path/file.pdf",
                "file.pdf", 1024L, "application/pdf", LocalDate.now(), null, "Colegio");
    }

    @Test
    @DisplayName("execute — flujo feliz — agrega documento al legajo")
    void execute_happyPath_addsDocument() {
        AddDocumentRequest request = buildRequest();
        StudentRecord record = mock(StudentRecord.class);
        when(record.getRecordId()).thenReturn(mock(org.school.management.students.records.domain.valueobject.RecordId.class));
        when(record.getRecordId().value()).thenReturn(UUID.randomUUID());

        when(recordRepository.findByStudentId(any())).thenReturn(Optional.of(record));
        doNothing().when(record).addDocument(any());
        when(recordRepository.save(any(StudentRecord.class))).thenReturn(record);
        when(getRecordByStudentIdUseCase.buildResponse(record)).thenReturn(mock(StudentRecordResponse.class));

        StudentRecordResponse result = useCase.execute(STUDENT_ID, request, UUID.randomUUID());

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("execute — legajo no encontrado — lanza RecordNotFoundException")
    void execute_notFound_throwsException() {
        AddDocumentRequest request = buildRequest();

        when(recordRepository.findByStudentId(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(STUDENT_ID, request, UUID.randomUUID()))
                .isInstanceOf(RecordNotFoundException.class);
    }
}
