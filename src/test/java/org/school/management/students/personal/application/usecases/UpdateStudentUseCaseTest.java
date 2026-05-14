package org.school.management.students.personal.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.students.personal.application.dto.request.UpdateStudentRequest;
import org.school.management.students.personal.application.dto.response.StudentResponse;
import org.school.management.students.personal.domain.exception.StudentNotFoundException;
import org.school.management.students.personal.domain.model.StudentPersonalData;
import org.school.management.students.personal.domain.repository.StudentPersonalDataRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("UpdateStudentUseCase")
class UpdateStudentUseCaseTest {

    @Mock private StudentPersonalDataRepository studentRepository;
    @Mock private GetStudentByIdUseCase getStudentByIdUseCase;

    @InjectMocks private UpdateStudentUseCase useCase;

    private static final UUID STUDENT_ID = UUID.randomUUID();
    private static final UUID PLACE_ID = UUID.randomUUID();

    private UpdateStudentRequest buildRequest() {
        return new UpdateStudentRequest("Juan", "Pérez", "3511234567", "juan@test.com",
                "Av. Siempre Viva", "123", null, null, "5000", PLACE_ID);
    }

    @Test
    @DisplayName("execute — flujo feliz — actualiza datos del estudiante")
    void execute_happyPath_updatesStudent() {
        UpdateStudentRequest request = buildRequest();
        StudentPersonalData student = mock(StudentPersonalData.class);

        when(studentRepository.findByStudentId(any())).thenReturn(Optional.of(student));
        doNothing().when(student).updatePersonalData(any(), any(), any(), any());
        when(studentRepository.save(any(StudentPersonalData.class))).thenReturn(student);
        when(getStudentByIdUseCase.buildResponse(student)).thenReturn(mock(StudentResponse.class));

        StudentResponse result = useCase.execute(STUDENT_ID, request);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("execute — no encontrado — lanza StudentNotFoundException")
    void execute_notFound_throwsException() {
        UpdateStudentRequest request = buildRequest();

        when(studentRepository.findByStudentId(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(STUDENT_ID, request))
                .isInstanceOf(StudentNotFoundException.class);
    }
}
