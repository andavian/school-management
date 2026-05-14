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
import org.school.management.academic.domain.model.QualificationRegistry;
import org.school.management.academic.domain.repository.QualificationRegistryRepository;
import org.school.management.academic.domain.valueobject.enums.RegistryStatus;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("SearchQualificationRegistriesUseCase")
class SearchQualificationRegistriesUseCaseTest {

    @Mock private QualificationRegistryRepository repository;
    @Mock private AcademicApplicationMapper mapper;

    @InjectMocks private SearchQualificationRegistriesUseCase useCase;

    @Test
    @DisplayName("execute — sin filtros → findAll")
    void execute_noFilters_usesFindAll() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        List<QualificationRegistryResponse> result = useCase.execute(null, null);

        assertThat(result).isEmpty();
        verify(repository).findAll();
    }

    @Test
    @DisplayName("execute — año + status → findByAcademicYear + filtro")
    void execute_yearAndStatus_filters() {
        when(repository.findByAcademicYear(any())).thenReturn(Collections.emptyList());

        List<QualificationRegistryResponse> result = useCase.execute(
                java.util.UUID.randomUUID().toString(), "ACTIVE");

        assertThat(result).isEmpty();
        verify(repository).findByAcademicYear(any());
    }

    @Test
    @DisplayName("execute — solo status → findByStatus")
    void execute_onlyStatus_usesFindByStatus() {
        when(repository.findByStatus(RegistryStatus.CLOSED)).thenReturn(Collections.emptyList());

        List<QualificationRegistryResponse> result = useCase.execute(null, "CLOSED");

        assertThat(result).isEmpty();
        verify(repository).findByStatus(RegistryStatus.CLOSED);
    }
}
