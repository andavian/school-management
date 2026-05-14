package org.school.management.academic.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.academic.application.dto.response.QualificationRegistryResponse;
import org.school.management.academic.application.mappers.AcademicApplicationMapper;
import org.school.management.academic.domain.exception.QualificationRegistryNotFoundException;
import org.school.management.academic.domain.model.QualificationRegistry;
import org.school.management.academic.domain.repository.QualificationRegistryRepository;
import org.school.management.academic.domain.valueobject.ids.RegistryId;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("GetQualificationRegistryUseCase")
class GetQualificationRegistryUseCaseTest {

    @Mock private QualificationRegistryRepository repository;
    @Mock private AcademicApplicationMapper mapper;

    @InjectMocks private GetQualificationRegistryUseCase useCase;

    private static final String ID = UUID.randomUUID().toString();

    @Test
    @DisplayName("execute — flujo feliz — encuentra el registro")
    void execute_happyPath_returnsRegistry() {
        when(repository.findById(any(RegistryId.class))).thenReturn(Optional.of(mock(QualificationRegistry.class)));
        when(mapper.toQualificationRegistryResponse(any(QualificationRegistry.class))).thenReturn(mock(QualificationRegistryResponse.class));

        QualificationRegistryResponse result = useCase.execute(ID);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("execute — no encontrado — lanza QualificationRegistryNotFoundException")
    void execute_notFound_throwsException() {
        when(repository.findById(any(RegistryId.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(ID))
                .isInstanceOf(QualificationRegistryNotFoundException.class);
    }
}
