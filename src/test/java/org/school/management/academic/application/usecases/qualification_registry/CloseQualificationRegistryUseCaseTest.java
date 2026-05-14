package org.school.management.academic.application.usecases.qualification_registry;

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
import org.school.management.academic.domain.exception.RegistryAlreadyClosedException;
import org.school.management.academic.domain.model.QualificationRegistry;
import org.school.management.academic.domain.repository.QualificationRegistryRepository;
import org.school.management.academic.domain.valueobject.RegistryNumber;
import org.school.management.academic.domain.valueobject.enums.RegistryStatus;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.RegistryId;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("CloseQualificationRegistryUseCase")
class CloseQualificationRegistryUseCaseTest {

    @Mock private QualificationRegistryRepository repository;
    @Mock private AcademicApplicationMapper mapper;

    @InjectMocks private CloseQualificationRegistryUseCase useCase;

    private static final UUID UUID_VAL = UUID.randomUUID();
    private static final String ID = UUID_VAL.toString();

    private QualificationRegistry buildActiveRegistry() {
        return QualificationRegistry.builder()
                .registryId(new RegistryId(UUID_VAL))
                .registryNumber(RegistryNumber.of("REG-2025-001"))
                .academicYearId(new AcademicYearId(UUID.randomUUID()))
                .startFolio(1)
                .endFolio(120)
                .currentFolio(1)
                .maxFolios(120)
                .status(RegistryStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private QualificationRegistry buildClosedRegistry() {
        return QualificationRegistry.builder()
                .registryId(new RegistryId(UUID_VAL))
                .registryNumber(RegistryNumber.of("REG-2025-001"))
                .academicYearId(new AcademicYearId(UUID.randomUUID()))
                .startFolio(1)
                .endFolio(120)
                .currentFolio(1)
                .maxFolios(120)
                .status(RegistryStatus.CLOSED)
                .createdAt(LocalDateTime.now())
                .closedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("execute — flujo feliz — cierra el registro")
    void execute_happyPath_closesRegistry() {
        QualificationRegistry registry = buildActiveRegistry();

        when(repository.findById(any(RegistryId.class))).thenReturn(Optional.of(registry));
        when(repository.save(any(QualificationRegistry.class))).thenAnswer(inv -> inv.getArgument(0));
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

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("execute — ya cerrado — lanza RegistryAlreadyClosedException")
    void execute_alreadyClosed_throwsException() {
        QualificationRegistry registry = buildClosedRegistry();

        when(repository.findById(any(RegistryId.class))).thenReturn(Optional.of(registry));

        assertThatThrownBy(() -> useCase.execute(ID))
                .isInstanceOf(RegistryAlreadyClosedException.class);

        verify(repository, never()).save(any());
    }
}
