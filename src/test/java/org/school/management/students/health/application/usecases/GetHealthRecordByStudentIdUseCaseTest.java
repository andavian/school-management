package org.school.management.students.health.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.students.health.application.dto.response.HealthRecordResponse;
import org.school.management.students.health.application.mapper.StudentHealthRecordApplicationMapper;
import org.school.management.students.health.domain.exception.HealthRecordNotFoundException;
import org.school.management.students.health.domain.model.StudentHealthRecord;
import org.school.management.students.health.domain.repository.StudentHealthRecordRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("GetHealthRecordByStudentIdUseCase")
class GetHealthRecordByStudentIdUseCaseTest {

    @Mock private StudentHealthRecordRepository healthRecordRepository;
    @Mock private StudentHealthRecordApplicationMapper mapper;

    @InjectMocks private GetHealthRecordByStudentIdUseCase useCase;

    @Test
    @DisplayName("execute — flujo feliz — encuentra ficha médica")
    void execute_happyPath_returnsHealthRecord() {
        when(healthRecordRepository.findByStudentId(any())).thenReturn(Optional.of(mock(StudentHealthRecord.class)));
        when(mapper.toHealthRecordResponse(any())).thenReturn(mock(HealthRecordResponse.class));

        HealthRecordResponse result = useCase.execute(UUID.randomUUID());

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("execute — no encontrada — lanza HealthRecordNotFoundException")
    void execute_notFound_throwsException() {
        when(healthRecordRepository.findByStudentId(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(UUID.randomUUID()))
                .isInstanceOf(HealthRecordNotFoundException.class);
    }
}
