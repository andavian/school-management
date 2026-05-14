package org.school.management.students.records.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.students.records.application.dto.response.StudentRecordResponse;
import org.school.management.students.records.application.mapper.StudentRecordApplicationMapper;
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
@DisplayName("GetRecordByStudentIdUseCase")
class GetRecordByStudentIdUseCaseTest {

    @Mock private StudentRecordRepository recordRepository;
    @Mock private StudentRecordApplicationMapper mapper;

    @InjectMocks private GetRecordByStudentIdUseCase useCase;

    @Test
    @DisplayName("execute — flujo feliz — encuentra el legajo")
    void execute_happyPath_returnsRecord() {
        when(recordRepository.findByStudentId(any())).thenReturn(Optional.of(mock(StudentRecord.class)));
        when(mapper.toRecordResponse(any(StudentRecord.class))).thenReturn(mock(StudentRecordResponse.class));

        StudentRecordResponse result = useCase.execute(UUID.randomUUID());

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("execute — no encontrado — lanza RecordNotFoundException")
    void execute_notFound_throwsException() {
        when(recordRepository.findByStudentId(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(UUID.randomUUID()))
                .isInstanceOf(RecordNotFoundException.class);
    }
}
