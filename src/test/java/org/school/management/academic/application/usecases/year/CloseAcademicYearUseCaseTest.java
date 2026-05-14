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
import org.school.management.academic.domain.exception.AcademicYearNotFoundException;
import org.school.management.academic.domain.model.AcademicYear;
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
@DisplayName("CloseAcademicYearUseCase")
class CloseAcademicYearUseCaseTest {

    @Mock private AcademicYearActivationService activationService;
    @Mock private AcademicApplicationMapper mapper;

    @InjectMocks private CloseAcademicYearUseCase useCase;

    private static final UUID ACADEMIC_YEAR_UUID = UUID.randomUUID();
    private static final String ACADEMIC_YEAR_ID = ACADEMIC_YEAR_UUID.toString();

    private AcademicYear buildClosedAcademicYear() {
        return AcademicYear.builder()
                .academicYearId(new AcademicYearId(ACADEMIC_YEAR_UUID))
                .year(Year.of(2025))
                .startDate(LocalDate.of(2025, 3, 1))
                .endDate(LocalDate.of(2025, 12, 20))
                .status(AcademicYearStatus.CLOSED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("execute — flujo feliz — cierra el año y retorna response")
    void execute_happyPath_closesAndReturnsResponse() {
        AcademicYear closedYear = buildClosedAcademicYear();
        AcademicYearResponse response = new AcademicYearResponse(
                ACADEMIC_YEAR_ID, 2025,
                LocalDate.of(2025, 3, 1), LocalDate.of(2025, 12, 20),
                "CLOSED", LocalDateTime.now(), LocalDateTime.now()
        );

        when(activationService.closeYear(any(AcademicYearId.class))).thenReturn(closedYear);
        when(mapper.toAcademicYearResponse(closedYear)).thenReturn(response);

        AcademicYearResponse result = useCase.execute(ACADEMIC_YEAR_ID);

        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo("CLOSED");

        verify(activationService).closeYear(any(AcademicYearId.class));
    }

    @Test
    @DisplayName("execute — año no encontrado — delega al servicio")
    void execute_notFound_delegatesToService() {
        when(activationService.closeYear(any(AcademicYearId.class)))
                .thenThrow(new AcademicYearNotFoundException("not found"));

        assertThatThrownBy(() -> useCase.execute(ACADEMIC_YEAR_ID))
                .isInstanceOf(AcademicYearNotFoundException.class);
    }
}
