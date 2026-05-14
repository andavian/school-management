package org.school.management.students.personal.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.geography.application.dto.response.PlaceResponse;
import org.school.management.shared.geography.domain.valueobject.PlaceId;
import org.school.management.shared.person.domain.valueobject.Address;
import org.school.management.students.personal.application.dto.response.StudentResponse;
import org.school.management.students.personal.application.mapper.StudentPersonalDataApplicationMapper;
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
@DisplayName("GetStudentByIdUseCase")
class GetStudentByIdUseCaseTest {

    @Mock private StudentPersonalDataRepository studentRepository;
    @Mock private StudentPersonalDataApplicationMapper mapper;
    @Mock private org.school.management.geography.application.usecases.GetPlaceByIdUseCase getPlaceByIdUseCase;

    @InjectMocks private GetStudentByIdUseCase useCase;

    @Test
    @DisplayName("execute — flujo feliz — encuentra por ID")
    void execute_happyPath_returnsStudent() {
        StudentPersonalData student = mock(StudentPersonalData.class);
        UUID placeUuid = UUID.randomUUID();
        when(student.getBirthPlaceId()).thenReturn(PlaceId.of(placeUuid));
        when(student.getAddress()).thenReturn(mock(Address.class));
        when(student.getAddress().placeId()).thenReturn(PlaceId.of(placeUuid));
        when(studentRepository.findByStudentId(any())).thenReturn(Optional.of(student));
        when(getPlaceByIdUseCase.execute(any())).thenReturn(mock(PlaceResponse.class));
        when(mapper.toStudentResponse(any(), any(), any())).thenReturn(mock(StudentResponse.class));

        StudentResponse result = useCase.execute(UUID.randomUUID());

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("execute — no encontrado — lanza StudentNotFoundException")
    void execute_notFound_throwsException() {
        when(studentRepository.findByStudentId(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(UUID.randomUUID()))
                .isInstanceOf(StudentNotFoundException.class);
    }
}
