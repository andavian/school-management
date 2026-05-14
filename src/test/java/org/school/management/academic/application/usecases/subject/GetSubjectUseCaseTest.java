package org.school.management.academic.application.usecases.subject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.academic.application.dto.response.SubjectResponse;
import org.school.management.academic.application.mappers.AcademicApplicationMapper;
import org.school.management.academic.domain.exception.SubjectNotFoundException;
import org.school.management.academic.domain.model.Subject;
import org.school.management.academic.domain.repository.SubjectRepository;
import org.school.management.academic.domain.valueobject.ids.SubjectId;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("GetSubjectUseCase")
class GetSubjectUseCaseTest {

    @Mock private SubjectRepository subjectRepository;
    @Mock private AcademicApplicationMapper mapper;

    @InjectMocks private GetSubjectUseCase useCase;

    private static final String ID = UUID.randomUUID().toString();

    @Test
    @DisplayName("execute — flujo feliz — encuentra la materia")
    void execute_happyPath_returnsSubject() {
        when(subjectRepository.findById(any(SubjectId.class))).thenReturn(Optional.of(mock(Subject.class)));
        when(mapper.toSubjectResponse(any(Subject.class))).thenReturn(mock(SubjectResponse.class));

        SubjectResponse result = useCase.execute(ID);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("execute — materia no encontrada — lanza SubjectNotFoundException")
    void execute_notFound_throwsException() {
        when(subjectRepository.findById(any(SubjectId.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(ID))
                .isInstanceOf(SubjectNotFoundException.class);
    }
}
