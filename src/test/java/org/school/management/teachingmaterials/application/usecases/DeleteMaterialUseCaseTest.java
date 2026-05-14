package org.school.management.teachingmaterials.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.storage.domain.service.StorageService;
import org.school.management.teachingmaterials.domain.exception.TeachingMaterialAccessDeniedException;
import org.school.management.teachingmaterials.domain.exception.TeachingMaterialNotFoundException;
import org.school.management.teachingmaterials.domain.model.TeachingMaterial;
import org.school.management.teachingmaterials.domain.repository.TeachingMaterialRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("DeleteMaterialUseCase")
class DeleteMaterialUseCaseTest {

    @Mock private TeachingMaterialRepository materialRepository;
    @Mock private StorageService storageService;

    @InjectMocks private DeleteMaterialUseCase useCase;

    private static final UUID MATERIAL_ID = UUID.randomUUID();
    private static final UUID TEACHER_ID = UUID.randomUUID();

    @Test
    @DisplayName("execute — flujo feliz — elimina de OCI y BD")
    void execute_happyPath_deletesFromOciAndDb() {
        TeachingMaterial material = mock(TeachingMaterial.class);
        when(material.belongsToTeacher(any())).thenReturn(true);
        when(material.getFilePath()).thenReturn("materials/obj-1");

        when(materialRepository.findById(any())).thenReturn(Optional.of(material));
        doNothing().when(storageService).delete(anyString());
        doNothing().when(materialRepository).delete(any());

        assertThatCode(() -> useCase.execute(MATERIAL_ID, TEACHER_ID))
                .doesNotThrowAnyException();

        verify(storageService).delete("materials/obj-1");
        verify(materialRepository).delete(any());
    }

    @Test
    @DisplayName("execute — null teacherId = ADMIN bypass — elimina sin chequear propiedad")
    void execute_adminBypass_deletesMaterial() {
        TeachingMaterial material = mock(TeachingMaterial.class);
        when(material.getFilePath()).thenReturn("materials/obj-1");

        when(materialRepository.findById(any())).thenReturn(Optional.of(material));
        doNothing().when(storageService).delete(anyString());
        doNothing().when(materialRepository).delete(any());

        assertThatCode(() -> useCase.execute(MATERIAL_ID, null))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("execute — no es dueño — lanza TeachingMaterialAccessDeniedException")
    void execute_notOwner_throwsException() {
        TeachingMaterial material = mock(TeachingMaterial.class);
        when(material.belongsToTeacher(any())).thenReturn(false);

        when(materialRepository.findById(any())).thenReturn(Optional.of(material));

        assertThatThrownBy(() -> useCase.execute(MATERIAL_ID, TEACHER_ID))
                .isInstanceOf(TeachingMaterialAccessDeniedException.class);

        verifyNoInteractions(storageService);
    }

    @Test
    @DisplayName("execute — material no encontrado — lanza TeachingMaterialNotFoundException")
    void execute_notFound_throwsException() {
        when(materialRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(MATERIAL_ID, TEACHER_ID))
                .isInstanceOf(TeachingMaterialNotFoundException.class);

        verifyNoInteractions(storageService);
    }
}
