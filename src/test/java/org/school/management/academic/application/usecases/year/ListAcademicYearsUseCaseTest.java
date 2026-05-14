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
import org.school.management.academic.domain.model.AcademicYear;
import org.school.management.academic.domain.repository.AcademicYearRepository;
import org.school.management.academic.domain.valueobject.enums.AcademicYearStatus;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.Year;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("ListAcademicYearsUseCase")
class ListAcademicYearsUseCaseTest {

    @Mock private AcademicYearRepository academicYearRepository;
    @Mock private AcademicApplicationMapper mapper;

    @InjectMocks private ListAcademicYearsUseCase useCase;

    private AcademicYear buildYear(int yearValue) {
        return AcademicYear.builder()
                .academicYearId(new AcademicYearId(UUID.randomUUID()))
                .year(Year.of(yearValue))
                .startDate(LocalDate.of(yearValue, 3, 1))
                .endDate(LocalDate.of(yearValue, 12, 20))
                .status(AcademicYearStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private AcademicYearResponse buildResponse(AcademicYear year) {
        return new AcademicYearResponse(
                year.getAcademicYearId().value().toString(), year.getYearValue(),
                year.getStartDate(), year.getEndDate(),
                year.getStatus().name(), year.getCreatedAt(), year.getUpdatedAt()
        );
    }

    @Test
    @DisplayName("execute — flujo feliz — lista todos los años")
    void execute_happyPath_listsAllYears() {
        AcademicYear y2024 = buildYear(2024);
        AcademicYear y2025 = buildYear(2025);

        when(academicYearRepository.findAll()).thenReturn(List.of(y2024, y2025));
        when(mapper.toAcademicYearResponse(y2024)).thenReturn(buildResponse(y2024));
        when(mapper.toAcademicYearResponse(y2025)).thenReturn(buildResponse(y2025));

        List<AcademicYearResponse> result = useCase.execute();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).year()).isEqualTo(2024);
        assertThat(result.get(1).year()).isEqualTo(2025);
    }

    @Test
    @DisplayName("execute — lista vacía — retorna lista vacía")
    void execute_emptyList_returnsEmptyList() {
        when(academicYearRepository.findAll()).thenReturn(List.of());

        List<AcademicYearResponse> result = useCase.execute();

        assertThat(result).isEmpty();
    }
}
