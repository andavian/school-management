package org.school.management.teachingmaterials.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.teachingmaterials.application.dto.request.UpdateMaterialRequest;
import org.school.management.teachingmaterials.application.dto.response.TeachingMaterialResponse;
import org.school.management.teachingmaterials.application.mapper.TeachingMaterialApplicationMapper;
import org.school.management.teachingmaterials.domain.exception.TeachingMaterialAccessDeniedException;
import org.school.management.teachingmaterials.domain.exception.TeachingMaterialNotFoundException;
import org.school.management.teachingmaterials.domain.model.TeachingMaterial;
import org.school.management.teachingmaterials.domain.repository.TeachingMaterialRepository;
import org.school.management.teachingmaterials.domain.valueobject.MaterialType;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("UpdateMaterialUseCase")
class UpdateMaterialUseCaseTest {

    @Mock private TeachingMaterialRepository materialRepository;
    @Mock private TeachingMaterialApplicationMapper mapper;

    @InjectMocks private UpdateMaterialUseCase useCase;

    private static final UUID MATERIAL_ID = UUID.randomUUID();
    private static final UUID TEACHER_ID = UUID.randomUUID();

    @Test
    @DisplayName("execute — flujo feliz — actualiza material del profesor")
    void execute_happyPath_updatesOwnMaterial() {
        UpdateMaterialRequest request = new UpdateMaterialRequest(null, null, null, null);
        TeachingMaterial material = mock(TeachingMaterial.class);
        when(material.belongsToTeacher(any())).thenReturn(true);
        when(material.getTitle()).thenReturn("Viejo título");
        when(material.getDescription()).thenReturn("Vieja descripción");
        when(material.getMaterialType()).thenReturn(MaterialType.GUIA);
        when(material.isVisibleToStudents()).thenReturn(true);

        when(materialRepository.findById(any())).thenReturn(Optional.of(material));
        when(materialRepository.save(any(TeachingMaterial.class))).thenReturn(material);
        when(mapper.toResponse(any(TeachingMaterial.class))).thenReturn(mock(TeachingMaterialResponse.class));

        TeachingMaterialResponse result = useCase.execute(MATERIAL_ID, request, TEACHER_ID);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("execute — null teacherId = ADMIN bypass — actualiza sin chequear propiedad")
    void execute_adminBypass_updatesMaterial() {
        UpdateMaterialRequest request = new UpdateMaterialRequest(null, null, null, null);
        TeachingMaterial material = mock(TeachingMaterial.class);
        when(material.getTitle()).thenReturn("Viejo título");
        when(material.getDescription()).thenReturn("Vieja descripción");
        when(material.getMaterialType()).thenReturn(MaterialType.GUIA);
        when(material.isVisibleToStudents()).thenReturn(true);

        when(materialRepository.findById(any())).thenReturn(Optional.of(material));
        when(materialRepository.save(any(TeachingMaterial.class))).thenReturn(material);
        when(mapper.toResponse(any(TeachingMaterial.class))).thenReturn(mock(TeachingMaterialResponse.class));

        TeachingMaterialResponse result = useCase.execute(MATERIAL_ID, request, null);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("execute — no es dueño — lanza TeachingMaterialAccessDeniedException")
    void execute_notOwner_throwsException() {
        UpdateMaterialRequest request = new UpdateMaterialRequest("Nuevo", "", null, null);
        TeachingMaterial material = mock(TeachingMaterial.class);
        when(material.belongsToTeacher(any())).thenReturn(false);

        when(materialRepository.findById(any())).thenReturn(Optional.of(material));

        assertThatThrownBy(() -> useCase.execute(MATERIAL_ID, request, TEACHER_ID))
                .isInstanceOf(TeachingMaterialAccessDeniedException.class);

        verifyNoInteractions(mapper);
    }

    @Test
    @DisplayName("execute — material no encontrado — lanza TeachingMaterialNotFoundException")
    void execute_notFound_throwsException() {
        UpdateMaterialRequest request = new UpdateMaterialRequest("Nuevo", "", null, null);

        when(materialRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(MATERIAL_ID, request, TEACHER_ID))
                .isInstanceOf(TeachingMaterialNotFoundException.class);
    }
}
