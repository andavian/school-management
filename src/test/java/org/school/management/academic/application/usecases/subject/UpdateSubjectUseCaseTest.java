package org.school.management.academic.application.usecases.subject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.academic.application.dto.request.UpdateSubjectRequest;
import org.school.management.academic.application.dto.response.SubjectResponse;
import org.school.management.academic.application.mappers.AcademicApplicationMapper;
import org.school.management.academic.domain.exception.SubjectNotFoundException;
import org.school.management.academic.domain.model.Subject;
import org.school.management.academic.domain.repository.SubjectRepository;
import org.school.management.academic.domain.valueobject.WeeklyHours;
import org.school.management.academic.domain.valueobject.YearLevel;
import org.school.management.academic.domain.valueobject.enums.SubjectCode;
import org.school.management.academic.domain.valueobject.ids.SubjectId;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("UpdateSubjectUseCase")
class UpdateSubjectUseCaseTest {

    @Mock private SubjectRepository subjectRepository;
    @Mock private AcademicApplicationMapper mapper;

    @InjectMocks private UpdateSubjectUseCase useCase;

    private static final String ID = UUID.randomUUID().toString();

    private Subject buildSubject() {
        return Subject.builder()
                .subjectId(new SubjectId(UUID.fromString(ID)))
                .name("Matemática")
                .code(SubjectCode.of("MAT_01"))
                .yearLevel(YearLevel.of(1))
                .isMandatory(true)
                .weeklyHours(WeeklyHours.of(5))
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("execute — flujo feliz — actualiza nombre y horas")
    void execute_happyPath_updatesSubject() {
        Subject subject = buildSubject();
        UpdateSubjectRequest request = new UpdateSubjectRequest("Matemática Avanzada", WeeklyHours.of(6), "");

        when(subjectRepository.findById(any(SubjectId.class))).thenReturn(Optional.of(subject));
        when(subjectRepository.save(any(Subject.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toSubjectResponse(any(Subject.class))).thenReturn(mock(SubjectResponse.class));

        SubjectResponse result = useCase.execute(ID, request);

        assertThat(result).isNotNull();
        verify(subjectRepository).save(argThat(s -> "Matemática Avanzada".equals(s.getName())));
    }

    @Test
    @DisplayName("execute — valores nulos — mantiene los valores existentes")
    void execute_nullFields_preservesExistingValues() {
        Subject subject = buildSubject();
        UpdateSubjectRequest request = new UpdateSubjectRequest(null, null, null);

        when(subjectRepository.findById(any(SubjectId.class))).thenReturn(Optional.of(subject));
        when(subjectRepository.save(any(Subject.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toSubjectResponse(any(Subject.class))).thenReturn(mock(SubjectResponse.class));

        SubjectResponse result = useCase.execute(ID, request);

        assertThat(result).isNotNull();
        verify(subjectRepository).save(argThat(s -> "Matemática".equals(s.getName())));
    }

    @Test
    @DisplayName("execute — materia no encontrada — lanza SubjectNotFoundException")
    void execute_notFound_throwsException() {
        UpdateSubjectRequest request = new UpdateSubjectRequest("Nuevo", WeeklyHours.of(4), "");

        when(subjectRepository.findById(any(SubjectId.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(ID, request))
                .isInstanceOf(SubjectNotFoundException.class);

        verify(subjectRepository, never()).save(any());
    }
}
