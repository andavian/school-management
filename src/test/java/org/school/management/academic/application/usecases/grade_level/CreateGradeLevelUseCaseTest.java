package org.school.management.academic.application.usecases.grade_level;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.academic.application.dto.request.CreateGradeLevelRequest;
import org.school.management.academic.application.dto.response.GradeLevelResponse;
import org.school.management.academic.application.mappers.AcademicApplicationMapper;
import org.school.management.academic.domain.exception.AcademicYearNotFoundException;
import org.school.management.academic.domain.exception.GradeLevelAlreadyExistsException;
import org.school.management.academic.domain.exception.InvalidOrientationForYearLevelException;
import org.school.management.academic.domain.model.AcademicYear;
import org.school.management.academic.domain.model.GradeLevel;
import org.school.management.academic.domain.repository.AcademicYearRepository;
import org.school.management.academic.domain.repository.GradeLevelRepository;
import org.school.management.academic.domain.service.GradeLevelValidationService;
import org.school.management.academic.domain.valueobject.Division;
import org.school.management.academic.domain.valueobject.YearLevel;
import org.school.management.academic.domain.valueobject.enums.Shift;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.GradeLevelId;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("CreateGradeLevelUseCase")
class CreateGradeLevelUseCaseTest {

    @Mock private GradeLevelRepository gradeLevelRepository;
    @Mock private AcademicYearRepository academicYearRepository;
    @Mock private GradeLevelValidationService validationService;
    @Mock private AcademicApplicationMapper mapper;

    @InjectMocks private CreateGradeLevelUseCase useCase;

    private static final String ACADEMIC_YEAR_ID = UUID.randomUUID().toString();

    private CreateGradeLevelRequest buildRequest() {
        return new CreateGradeLevelRequest(ACADEMIC_YEAR_ID, 1, "A", null, "MORNING", 30);
    }

    @Test
    @DisplayName("execute — flujo feliz — crea un curso sin orientación (1° año)")
    void execute_happyPath_createsGradeLevel() {
        CreateGradeLevelRequest request = buildRequest();

        when(academicYearRepository.findByAcademicYearId(any(AcademicYearId.class)))
                .thenReturn(Optional.of(mock(AcademicYear.class)));
        doNothing().when(validationService).validateGradeLevelCreation(any(), any(YearLevel.class), any(Division.class), eq(null));
        when(gradeLevelRepository.save(any(GradeLevel.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toGradeLevelResponse(any(GradeLevel.class))).thenReturn(mock(GradeLevelResponse.class));

        GradeLevelResponse result = useCase.execute(request);

        assertThat(result).isNotNull();
        verify(gradeLevelRepository).save(any(GradeLevel.class));
    }

    @Test
    @DisplayName("execute — año académico no encontrado — lanza AcademicYearNotFoundException")
    void execute_academicYearNotFound_throwsException() {
        CreateGradeLevelRequest request = buildRequest();

        when(academicYearRepository.findByAcademicYearId(any(AcademicYearId.class)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(AcademicYearNotFoundException.class);

        verify(gradeLevelRepository, never()).save(any());
    }

    @Test
    @DisplayName("execute — validación falla — propaga la excepción del servicio")
    void execute_validationFails_propagatesException() {
        CreateGradeLevelRequest request = buildRequest();

        when(academicYearRepository.findByAcademicYearId(any(AcademicYearId.class)))
                .thenReturn(Optional.of(mock(AcademicYear.class)));
        doThrow(new GradeLevelAlreadyExistsException("already exists"))
                .when(validationService).validateGradeLevelCreation(any(), any(YearLevel.class), any(Division.class), eq(null));

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(GradeLevelAlreadyExistsException.class);

        verify(gradeLevelRepository, never()).save(any());
    }
}
