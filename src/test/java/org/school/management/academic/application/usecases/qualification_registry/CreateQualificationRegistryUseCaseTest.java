package org.school.management.academic.application.usecases.qualification_registry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.academic.application.dto.request.CreateQualificationRegistryRequest;
import org.school.management.academic.application.dto.response.QualificationRegistryResponse;
import org.school.management.academic.application.mappers.AcademicApplicationMapper;
import org.school.management.academic.domain.model.QualificationRegistry;
import org.school.management.academic.domain.repository.QualificationRegistryRepository;
import org.school.management.academic.domain.service.QualificationRegistryFactory;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("CreateQualificationRegistryUseCase")
class CreateQualificationRegistryUseCaseTest {

    @Mock private QualificationRegistryFactory factory;
    @Mock private QualificationRegistryRepository repository;
    @Mock private AcademicApplicationMapper mapper;

    @InjectMocks private CreateQualificationRegistryUseCase useCase;

    private static final String ACADEMIC_YEAR_ID = UUID.randomUUID().toString();

    @Test
    @DisplayName("execute — flujo feliz — crea registro de calificación")
    void execute_happyPath_createsRegistry() {
        CreateQualificationRegistryRequest request = new CreateQualificationRegistryRequest(ACADEMIC_YEAR_ID, 1, 120, 120);
        QualificationRegistry registry = mock(QualificationRegistry.class);

        when(factory.create(any(AcademicYearId.class))).thenReturn(registry);
        when(repository.save(registry)).thenReturn(registry);
        when(mapper.toQualificationRegistryResponse(registry)).thenReturn(mock(QualificationRegistryResponse.class));

        QualificationRegistryResponse result = useCase.execute(request);

        assertThat(result).isNotNull();
        verify(factory).create(any(AcademicYearId.class));
        verify(repository).save(registry);
    }
}
