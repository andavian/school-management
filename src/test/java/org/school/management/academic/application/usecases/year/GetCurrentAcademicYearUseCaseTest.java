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
import org.school.management.academic.domain.repository.AcademicYearRepository;
import org.school.management.academic.domain.valueobject.enums.AcademicYearStatus;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.Year;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("GetCurrentAcademicYearUseCase")
class GetCurrentAcademicYearUseCaseTest {

    @Mock private AcademicYearRepository academicYearRepository;
    @Mock private AcademicApplicationMapper mapper;

    @InjectMocks private GetCurrentAcademicYearUseCase useCase;

    private AcademicYear buildYear() {
        return AcademicYear.builder()
                .academicYearId(new AcademicYearId(UUID.randomUUID()))
                .year(Year.of(2025))
                .startDate(LocalDate.of(2025, 3, 1))
                .endDate(LocalDate.of(2025, 12, 20))
                .status(AcademicYearStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("execute — flujo feliz — retorna el año actual")
    void execute_happyPath_returnsCurrentYear() {
        AcademicYear year = buildYear();
        AcademicYearResponse response = new AcademicYearResponse(
                year.getAcademicYearId().value().toString(), 2025,
                LocalDate.of(2025, 3, 1), LocalDate.of(2025, 12, 20),
                "ACTIVE", LocalDateTime.now(), LocalDateTime.now()
        );

        when(academicYearRepository.findCurrentYear()).thenReturn(Optional.of(year));
        when(mapper.toAcademicYearResponse(year)).thenReturn(response);

        AcademicYearResponse result = useCase.execute();

        assertThat(result).isNotNull();
        assertThat(result.year()).isEqualTo(2025);
    }

    @Test
    @DisplayName("execute — no hay año actual — lanza AcademicYearNotFoundException")
    void execute_noCurrentYear_throwsException() {
        when(academicYearRepository.findCurrentYear()).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute())
                .isInstanceOf(AcademicYearNotFoundException.class)
                .hasMessageContaining("No current academic year");
    }
}
