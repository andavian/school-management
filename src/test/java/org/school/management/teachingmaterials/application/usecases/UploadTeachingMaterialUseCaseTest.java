package org.school.management.teachingmaterials.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.storage.domain.model.UploadedFile;
import org.school.management.storage.domain.service.StorageService;
import org.school.management.teachingmaterials.application.dto.request.UploadMaterialRequest;
import org.school.management.teachingmaterials.application.dto.response.TeachingMaterialResponse;
import org.school.management.teachingmaterials.application.mapper.TeachingMaterialApplicationMapper;
import org.school.management.teachingmaterials.domain.model.TeachingMaterial;
import org.school.management.teachingmaterials.domain.repository.TeachingMaterialRepository;
import org.school.management.teachingmaterials.domain.valueobject.MaterialType;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("UploadTeachingMaterialUseCase")
class UploadTeachingMaterialUseCaseTest {

    @Mock private TeachingMaterialRepository materialRepository;
    @Mock private StorageService storageService;
    @Mock private TeachingMaterialApplicationMapper mapper;

    @InjectMocks private UploadTeachingMaterialUseCase useCase;

    private static final UUID TEACHER_ID = UUID.randomUUID();
    private static final UUID COURSE_SUBJECT_ID = UUID.randomUUID();
    private static final UUID SUBJECT_ID = UUID.randomUUID();
    private static final UUID ACADEMIC_YEAR_ID = UUID.randomUUID();

    private UploadMaterialRequest buildRequest() {
        return new UploadMaterialRequest(COURSE_SUBJECT_ID, SUBJECT_ID,
                ACADEMIC_YEAR_ID, "Guía 1", "Descripción", MaterialType.GUIA, true);
    }

    private UploadedFile buildUploadedFile() {
        return new UploadedFile("materials/obj-1", "https://oci.example.com/obj-1",
                "guia1.pdf", "application/pdf", 1024L);
    }

    @Test
    @DisplayName("execute — flujo feliz — sube archivo y persiste material")
    void execute_happyPath_uploadsAndPersists() throws IOException {
        UploadMaterialRequest request = buildRequest();
        MultipartFile file = mock(MultipartFile.class);
        UploadedFile uploaded = buildUploadedFile();

        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("application/pdf");
        when(file.getSize()).thenReturn(1024L);
        when(file.getOriginalFilename()).thenReturn("guia1.pdf");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
        when(storageService.upload(any(), anyString(), anyString(), anyLong(), anyString()))
                .thenReturn(uploaded);
        when(materialRepository.save(any(TeachingMaterial.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toResponse(any(TeachingMaterial.class))).thenReturn(mock(TeachingMaterialResponse.class));

        TeachingMaterialResponse result = useCase.execute(request, file, TEACHER_ID);

        assertThat(result).isNotNull();
        verify(storageService).upload(any(), anyString(), anyString(), anyLong(), anyString());
        verify(materialRepository).save(any(TeachingMaterial.class));
    }

    @Test
    @DisplayName("execute — archivo vacío — lanza IllegalArgumentException")
    void execute_emptyFile_throwsException() {
        UploadMaterialRequest request = buildRequest();
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(request, file, TEACHER_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("empty");

        verifyNoInteractions(storageService, materialRepository);
    }

    @Test
    @DisplayName("execute — tipo MIME no permitido — lanza IllegalArgumentException")
    void execute_invalidMimeType_throwsException() {
        UploadMaterialRequest request = buildRequest();
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("application/msword");

        assertThatThrownBy(() -> useCase.execute(request, file, TEACHER_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not allowed");

        verifyNoInteractions(storageService, materialRepository);
    }
}
