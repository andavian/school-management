package org.school.management.academic.application.usecases.subject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.academic.application.dto.request.CreateSubjectRequest;
import org.school.management.academic.application.dto.response.SubjectResponse;
import org.school.management.academic.application.mappers.AcademicApplicationMapper;
import org.school.management.academic.domain.exception.OrientationNotFoundException;
import org.school.management.academic.domain.exception.SubjectAlreadyExistsException;
import org.school.management.academic.domain.model.Orientation;
import org.school.management.academic.domain.model.Subject;
import org.school.management.academic.domain.repository.OrientationRepository;
import org.school.management.academic.domain.repository.SubjectRepository;
import org.school.management.academic.domain.valueobject.ids.OrientationId;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("CreateSubjectUseCase")
class CreateSubjectUseCaseTest {

    @Mock private SubjectRepository subjectRepository;
    @Mock private OrientationRepository orientationRepository;
    @Mock private AcademicApplicationMapper mapper;

    @InjectMocks private CreateSubjectUseCase useCase;

    private CreateSubjectRequest buildRequest() {
        return new CreateSubjectRequest("Matemática", "MAT_01", 1, null, true, 5, "");
    }

    @Test
    @DisplayName("execute — flujo feliz — crea materia común (sin orientación)")
    void execute_commonSubject_createsSubject() {
        CreateSubjectRequest request = buildRequest();

        when(subjectRepository.existsByCode("MAT_01")).thenReturn(false);
        when(subjectRepository.save(any(Subject.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toSubjectResponse(any(Subject.class))).thenReturn(mock(SubjectResponse.class));

        SubjectResponse result = useCase.execute(request);

        assertThat(result).isNotNull();
        verify(subjectRepository).save(any(Subject.class));
    }

    @Test
    @DisplayName("execute — con orientación — valida que exista y crea la materia")
    void execute_withOrientation_createsSubject() {
        CreateSubjectRequest request = new CreateSubjectRequest(
                "Programación", "PROG_01", 4, UUID.randomUUID().toString(), true, 4, "");

        when(subjectRepository.existsByCode("PROG_01")).thenReturn(false);
        when(orientationRepository.findById(any(OrientationId.class)))
                .thenReturn(Optional.of(mock(Orientation.class)));
        when(subjectRepository.save(any(Subject.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toSubjectResponse(any(Subject.class))).thenReturn(mock(SubjectResponse.class));

        SubjectResponse result = useCase.execute(request);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("execute — código duplicado — lanza SubjectAlreadyExistsException")
    void execute_duplicateCode_throwsException() {
        CreateSubjectRequest request = buildRequest();

        when(subjectRepository.existsByCode("MAT_01")).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(SubjectAlreadyExistsException.class);

        verify(subjectRepository, never()).save(any());
    }

    @Test
    @DisplayName("execute — orientación no encontrada — lanza OrientationNotFoundException")
    void execute_orientationNotFound_throwsException() {
        String orientationId = UUID.randomUUID().toString();
        CreateSubjectRequest request = new CreateSubjectRequest(
                "Programación", "PROG_01", 4, orientationId, true, 4, "");

        when(subjectRepository.existsByCode("PROG_01")).thenReturn(false);
        when(orientationRepository.findById(any(OrientationId.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(OrientationNotFoundException.class);

        verify(subjectRepository, never()).save(any());
    }
}
