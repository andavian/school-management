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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("GetAcademicYearUseCase")
class GetAcademicYearUseCaseTest {

    @Mock private AcademicYearRepository academicYearRepository;
    @Mock private AcademicApplicationMapper mapper;

    @InjectMocks private GetAcademicYearUseCase useCase;

    private static final UUID UUID_VAL = UUID.randomUUID();
    private static final String ID = UUID_VAL.toString();

    private AcademicYear buildYear() {
        return AcademicYear.builder()
                .academicYearId(new AcademicYearId(UUID_VAL))
                .year(Year.of(2025))
                .startDate(LocalDate.of(2025, 3, 1))
                .endDate(LocalDate.of(2025, 12, 20))
                .status(AcademicYearStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("execute — flujo feliz — encuentra el año")
    void execute_happyPath_returnsAcademicYear() {
        AcademicYear year = buildYear();
        AcademicYearResponse response = new AcademicYearResponse(
                ID, 2025, LocalDate.of(2025, 3, 1), LocalDate.of(2025, 12, 20),
                "ACTIVE", LocalDateTime.now(), LocalDateTime.now()
        );

        when(academicYearRepository.findByAcademicYearId(any(AcademicYearId.class))).thenReturn(Optional.of(year));
        when(mapper.toAcademicYearResponse(year)).thenReturn(response);

        AcademicYearResponse result = useCase.execute(ID);

        assertThat(result).isNotNull();
        assertThat(result.academicYearId()).isEqualTo(ID);
    }

    @Test
    @DisplayName("execute — año no encontrado — lanza AcademicYearNotFoundException")
    void execute_notFound_throwsException() {
        when(academicYearRepository.findByAcademicYearId(any(AcademicYearId.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(ID))
                .isInstanceOf(AcademicYearNotFoundException.class);
    }
}
