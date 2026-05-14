package org.school.management.academic.application.usecases.year;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.academic.application.dto.request.CreateAcademicYearRequest;
import org.school.management.academic.application.dto.response.AcademicYearResponse;
import org.school.management.academic.application.mappers.AcademicApplicationMapper;
import org.school.management.academic.domain.exception.AcademicYearAlreadyExistsException;
import org.school.management.academic.domain.model.AcademicYear;
import org.school.management.academic.domain.repository.AcademicYearRepository;
import org.school.management.academic.domain.valueobject.enums.AcademicYearStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("CreateAcademicYearUseCase")
class CreateAcademicYearUseCaseTest {

    @Mock private AcademicYearRepository academicYearRepository;
    @Mock private AcademicApplicationMapper mapper;

    @InjectMocks private CreateAcademicYearUseCase useCase;

    // ── helpers ───────────────────────────────────────────────────────────

    private CreateAcademicYearRequest buildRequest() {
        return new CreateAcademicYearRequest(2025, LocalDate.of(2025, 3, 1), LocalDate.of(2025, 12, 20), false);
    }

    private AcademicYearResponse buildResponse(AcademicYear saved) {
        return new AcademicYearResponse(
                saved.getAcademicYearId().value().toString(),
                saved.getYearValue(),
                saved.getStartDate(), saved.getEndDate(),
                saved.getStatus().name(),
                saved.getCreatedAt(), saved.getUpdatedAt()
        );
    }

    // ── tests ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("execute — flujo feliz — crea año académico en estado PENDING")
    void execute_isCurrentFalse_createsPendingAcademicYear() {
        CreateAcademicYearRequest request = buildRequest();

        when(academicYearRepository.existsByYear(2025)).thenReturn(false);
        when(academicYearRepository.save(any(AcademicYear.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toAcademicYearResponse(any(AcademicYear.class))).thenAnswer(inv -> {
            AcademicYear a = inv.getArgument(0);
            return buildResponse(a);
        });

        AcademicYearResponse result = useCase.execute(request);

        assertThat(result).isNotNull();
        assertThat(result.year()).isEqualTo(2025);
        assertThat(result.status()).isEqualTo("PENDING");

        verify(academicYearRepository).save(any(AcademicYear.class));
    }

    @Test
    @DisplayName("execute — isCurrent=true pero ya hay año ACTIVO → lo fuerza a PENDING")
    void execute_isCurrentTrueButAnotherActive_forcesPending() {
        CreateAcademicYearRequest request = new CreateAcademicYearRequest(2025,
                LocalDate.of(2025, 3, 1), LocalDate.of(2025, 12, 20), true);

        when(academicYearRepository.existsByYear(2025)).thenReturn(false);
        when(academicYearRepository.existsByStatus(AcademicYearStatus.ACTIVE)).thenReturn(true);
        when(academicYearRepository.save(any(AcademicYear.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toAcademicYearResponse(any(AcademicYear.class))).thenAnswer(inv -> {
            AcademicYear a = inv.getArgument(0);
            return buildResponse(a);
        });

        AcademicYearResponse result = useCase.execute(request);

        assertThat(result.status()).isEqualTo("PENDING");
    }

    @Test
    @DisplayName("execute — isCurrent=true sin otro año activo → crea ACTIVO")
    void execute_isCurrentTrueNoOtherActive_createsActive() {
        CreateAcademicYearRequest request = new CreateAcademicYearRequest(2025,
                LocalDate.of(2025, 3, 1), LocalDate.of(2025, 12, 20), true);

        when(academicYearRepository.existsByYear(2025)).thenReturn(false);
        when(academicYearRepository.existsByStatus(AcademicYearStatus.ACTIVE)).thenReturn(false);
        when(academicYearRepository.save(any(AcademicYear.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toAcademicYearResponse(any(AcademicYear.class))).thenAnswer(inv -> {
            AcademicYear a = inv.getArgument(0);
            return buildResponse(a);
        });

        AcademicYearResponse result = useCase.execute(request);

        assertThat(result.status()).isEqualTo("ACTIVE");
    }

    @Test
    @DisplayName("execute — año ya existe — lanza AcademicYearAlreadyExistsException")
    void execute_yearAlreadyExists_throwsException() {
        CreateAcademicYearRequest request = buildRequest();

        when(academicYearRepository.existsByYear(2025)).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(AcademicYearAlreadyExistsException.class)
                .hasMessageContaining("2025");

        verify(academicYearRepository, never()).save(any());
    }
}
