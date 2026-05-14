package org.school.management.academic.application.usecases.qualification_registry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.academic.application.dto.request.InitRegistrySequenceRequest;
import org.school.management.academic.application.dto.response.QualificationRegistryResponse;
import org.school.management.academic.application.mappers.AcademicApplicationMapper;
import org.school.management.academic.domain.model.QualificationRegistry;
import org.school.management.academic.domain.repository.QualificationRegistryRepository;
import org.school.management.academic.domain.service.QualificationRegistryFactory;
import org.school.management.academic.domain.service.RegistryNumberGenerator;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("InitializeRegistrySequenceUseCase")
class InitializeRegistrySequenceUseCaseTest {

    @Mock private QualificationRegistryRepository registryRepository;
    @Mock private QualificationRegistryFactory factory;
    @Mock private RegistryNumberGenerator numberGenerator;
    @Mock private AcademicApplicationMapper mapper;

    @InjectMocks private InitializeRegistrySequenceUseCase useCase;

    private static final String ACADEMIC_YEAR_ID = UUID.randomUUID().toString();

    @Test
    @DisplayName("execute — flujo feliz — inicializa secuencia")
    void execute_happyPath_initializesSequence() {
        InitRegistrySequenceRequest request = new InitRegistrySequenceRequest(ACADEMIC_YEAR_ID, 50);
        QualificationRegistry registry = mock(QualificationRegistry.class);

        when(registryRepository.countAll()).thenReturn(0L);
        when(numberGenerator.formatNumber(50, 51)).thenReturn("REG-2025-051");
        when(factory.createWithNumber(any(AcademicYearId.class), eq("REG-2025-051"))).thenReturn(registry);
        when(registryRepository.save(registry)).thenReturn(registry);
        when(mapper.toQualificationRegistryResponse(registry)).thenReturn(mock(QualificationRegistryResponse.class));

        QualificationRegistryResponse result = useCase.execute(request);

        assertThat(result).isNotNull();
        verify(factory).createWithNumber(any(AcademicYearId.class), eq("REG-2025-051"));
    }

    @Test
    @DisplayName("execute — ya existen registros — lanza IllegalStateException")
    void execute_registriesAlreadyExist_throwsException() {
        InitRegistrySequenceRequest request = new InitRegistrySequenceRequest(ACADEMIC_YEAR_ID, 50);

        when(registryRepository.countAll()).thenReturn(5L);

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already exist");

        verify(factory, never()).createWithNumber(any(), any());
    }
}
