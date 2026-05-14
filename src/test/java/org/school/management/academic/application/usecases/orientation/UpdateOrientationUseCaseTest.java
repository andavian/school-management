package org.school.management.academic.application.usecases.orientation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.academic.application.dto.request.UpdateOrientationRequest;
import org.school.management.academic.application.dto.response.OrientationResponse;
import org.school.management.academic.application.mappers.AcademicApplicationMapper;
import org.school.management.academic.domain.exception.OrientationNotFoundException;
import org.school.management.academic.domain.model.Orientation;
import org.school.management.academic.domain.repository.OrientationRepository;
import org.school.management.academic.domain.valueobject.OrientationCode;
import org.school.management.academic.domain.valueobject.YearLevel;
import org.school.management.academic.domain.valueobject.ids.OrientationId;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("UpdateOrientationUseCase")
class UpdateOrientationUseCaseTest {

    @Mock private OrientationRepository orientationRepository;
    @Mock private AcademicApplicationMapper mapper;

    @InjectMocks private UpdateOrientationUseCase useCase;

    private static final UUID UUID_VAL = UUID.randomUUID();
    private static final String ID = UUID_VAL.toString();

    private Orientation buildOrientation() {
        return Orientation.builder()
                .orientationId(new OrientationId(UUID_VAL))
                .name("Informática")
                .code(OrientationCode.of("INFO"))
                .availableFromYear(YearLevel.of(4))
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("execute — flujo feliz — actualiza nombre")
    void execute_happyPath_updatesName() {
        Orientation orientation = buildOrientation();
        UpdateOrientationRequest request = new UpdateOrientationRequest("Informática Avanzada", "", YearLevel.of(4));

        when(orientationRepository.findById(any(OrientationId.class))).thenReturn(Optional.of(orientation));
        when(orientationRepository.save(any(Orientation.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toOrientationResponse(any(Orientation.class))).thenReturn(mock(OrientationResponse.class));

        OrientationResponse result = useCase.execute(ID, request);

        assertThat(result).isNotNull();
        verify(orientationRepository).save(argThat(o -> "Informática Avanzada".equals(o.getName())));
    }

    @Test
    @DisplayName("execute — valores nulos — mantiene existentes")
    void execute_nullFields_preservesExisting() {
        Orientation orientation = buildOrientation();
        UpdateOrientationRequest request = new UpdateOrientationRequest(null, null, null);

        when(orientationRepository.findById(any(OrientationId.class))).thenReturn(Optional.of(orientation));
        when(orientationRepository.save(any(Orientation.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toOrientationResponse(any(Orientation.class))).thenReturn(mock(OrientationResponse.class));

        OrientationResponse result = useCase.execute(ID, request);

        assertThat(result).isNotNull();
        verify(orientationRepository).save(argThat(o -> "Informática".equals(o.getName())));
    }

    @Test
    @DisplayName("execute — no encontrada — lanza OrientationNotFoundException")
    void execute_notFound_throwsException() {
        UpdateOrientationRequest request = new UpdateOrientationRequest("Nuevo", "", null);

        when(orientationRepository.findById(any(OrientationId.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(ID, request))
                .isInstanceOf(OrientationNotFoundException.class);

        verify(orientationRepository, never()).save(any());
    }
}
