package org.school.management.teachingmaterials.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.teachingmaterials.application.dto.response.TeachingMaterialResponse;
import org.school.management.teachingmaterials.application.mapper.TeachingMaterialApplicationMapper;
import org.school.management.teachingmaterials.domain.repository.TeachingMaterialRepository;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("GetMaterialsForStudentUseCase")
class GetMaterialsForStudentUseCaseTest {

    @Mock private TeachingMaterialRepository materialRepository;
    @Mock private TeachingMaterialApplicationMapper mapper;

    @InjectMocks private GetMaterialsForStudentUseCase useCase;

    @Test
    @DisplayName("execute — flujo feliz — lista materiales visibles")
    void execute_happyPath_listsVisibleMaterials() {
        when(materialRepository.findVisibleByCourseSubjectIds(any())).thenReturn(Collections.emptyList());

        List<TeachingMaterialResponse> result = useCase.execute(List.of(UUID.randomUUID()));

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("execute — lista vacía de cursos — retorna lista vacía sin consultar")
    void execute_emptyCourseList_returnsEmpty() {
        List<TeachingMaterialResponse> result = useCase.execute(List.of());

        assertThat(result).isEmpty();
        verifyNoInteractions(materialRepository);
    }
}
