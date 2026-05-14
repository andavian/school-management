package org.school.management.students.personal.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.students.personal.application.dto.response.StudentResponse;
import org.school.management.students.personal.domain.exception.StudentNotFoundException;
import org.school.management.students.personal.domain.model.StudentPersonalData;
import org.school.management.students.personal.domain.repository.StudentPersonalDataRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("GetStudentByDniUseCase")
class GetStudentByDniUseCaseTest {

    @Mock private StudentPersonalDataRepository studentRepository;
    @Mock private GetStudentByIdUseCase getStudentByIdUseCase;

    @InjectMocks private GetStudentByDniUseCase useCase;

    @Test
    @DisplayName("execute — flujo feliz — encuentra por DNI")
    void execute_happyPath_returnsStudent() {
        StudentPersonalData student = mock(StudentPersonalData.class);
        when(studentRepository.findByDni(any())).thenReturn(Optional.of(student));
        when(getStudentByIdUseCase.buildResponse(student)).thenReturn(mock(StudentResponse.class));

        StudentResponse result = useCase.execute("20345676");

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("execute — no encontrado — lanza StudentNotFoundException")
    void execute_notFound_throwsException() {
        when(studentRepository.findByDni(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute("20345676"))
                .isInstanceOf(StudentNotFoundException.class);
    }
}
