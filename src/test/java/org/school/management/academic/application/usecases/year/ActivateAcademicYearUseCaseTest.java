package org.school.management.academic.application.usecases.year;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.academic.application.dto.response.AcademicYearResponse;
import org.school.management.academic.application.mappers.AcademicApplicationMapper;
import org.school.management.academic.domain.exception.AcademicYearAlreadyActiveException;
import org.school.management.academic.domain.exception.AcademicYearNotFoundException;
import org.school.management.academic.domain.model.AcademicYear;
import org.school.management.academic.domain.repository.AcademicYearRepository;
import org.school.management.academic.domain.service.AcademicYearActivationService;
import org.school.management.academic.domain.valueobject.enums.AcademicYearStatus;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.Year;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("ActivateAcademicYearUseCase")
class ActivateAcademicYearUseCaseTest {

    @Mock private AcademicYearActivationService activationService;
    @Mock private AcademicApplicationMapper mapper;

    @InjectMocks private ActivateAcademicYearUseCase useCase;

    private static final UUID ACADEMIC_YEAR_UUID = UUID.randomUUID();
    private static final String ACADEMIC_YEAR_ID = ACADEMIC_YEAR_UUID.toString();

    private AcademicYear buildAcademicYear() {
        return AcademicYear.builder()
                .academicYearId(new AcademicYearId(ACADEMIC_YEAR_UUID))
                .year(Year.of(2025))
                .startDate(LocalDate.of(2025, 3, 1))
                .endDate(LocalDate.of(2025, 12, 20))
                .status(AcademicYearStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("execute — flujo feliz — activa el año y retorna response")
    void execute_happyPath_activatesAndReturnsResponse() {
        AcademicYear academicYear = buildAcademicYear();
        AcademicYearResponse response = new AcademicYearResponse(
                ACADEMIC_YEAR_ID, 2025,
                LocalDate.of(2025, 3, 1), LocalDate.of(2025, 12, 20),
                "ACTIVE", LocalDateTime.now(), LocalDateTime.now()
        );

        when(activationService.activateYear(any(AcademicYearId.class))).thenReturn(academicYear);
        when(mapper.toAcademicYearResponse(academicYear)).thenReturn(response);

        AcademicYearResponse result = useCase.execute(ACADEMIC_YEAR_ID);

        assertThat(result).isNotNull();
        assertThat(result.academicYearId()).isEqualTo(ACADEMIC_YEAR_ID);

        verify(activationService).activateYear(any(AcademicYearId.class));
    }

    @Test
    @DisplayName("execute — año no encontrado — delega al servicio")
    void execute_notFound_delegatesToService() {
        when(activationService.activateYear(any(AcademicYearId.class)))
                .thenThrow(new AcademicYearNotFoundException("Academic year not found"));

        assertThatThrownBy(() -> useCase.execute(ACADEMIC_YEAR_ID))
                .isInstanceOf(AcademicYearNotFoundException.class);
    }

    @Test
    @DisplayName("execute — año ya activo — delega al servicio")
    void execute_alreadyActive_delegatesToService() {
        when(activationService.activateYear(any(AcademicYearId.class)))
                .thenThrow(new AcademicYearAlreadyActiveException("already active"));

        assertThatThrownBy(() -> useCase.execute(ACADEMIC_YEAR_ID))
                .isInstanceOf(AcademicYearAlreadyActiveException.class);
    }
}
