package org.school.management.students.records.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.storage.domain.model.UploadedFile;
import org.school.management.storage.domain.service.StorageService;
import org.school.management.students.records.application.dto.request.UploadDocumentRequest;
import org.school.management.students.records.application.dto.response.RecordDocumentResponse;
import org.school.management.students.records.application.mapper.StudentRecordApplicationMapper;
import org.school.management.students.records.domain.exception.RecordNotFoundException;
import org.school.management.students.records.domain.model.StudentRecord;
import org.school.management.students.records.domain.repository.RecordDocumentRepository;
import org.school.management.students.records.domain.repository.StudentRecordRepository;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("UploadRecordDocumentUseCase")
class UploadRecordDocumentUseCaseTest {

    @Mock private StudentRecordRepository recordRepository;
    @Mock private RecordDocumentRepository documentRepository;
    @Mock private StorageService storageService;
    @Mock private StudentRecordApplicationMapper mapper;

    @InjectMocks private UploadRecordDocumentUseCase useCase;

    private static final UUID RECORD_ID = UUID.randomUUID();

    private UploadDocumentRequest buildRequest() {
        return new UploadDocumentRequest(UUID.randomUUID(), "Certificado", "",
                LocalDate.now(), null, "Colegio");
    }

    @Test
    @DisplayName("execute — flujo feliz — sube documento y lo agrega al legajo")
    void execute_happyPath_uploadsDocument() throws IOException {
        UploadDocumentRequest request = buildRequest();
        MultipartFile file = mock(MultipartFile.class);
        UploadedFile uploaded = new UploadedFile("obj-1", "https://oci/obj-1", "cert.pdf", "application/pdf", 1024L);

        StudentRecord record = mock(StudentRecord.class);
        when(record.getStudentId()).thenReturn(mock(org.school.management.students.personal.domain.valueobject.StudentPersonalDataId.class));
        when(record.getStudentId().value()).thenReturn(UUID.randomUUID());

        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("application/pdf");
        when(file.getSize()).thenReturn(1024L);
        when(file.getOriginalFilename()).thenReturn("cert.pdf");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
        when(recordRepository.findByRecordId(any())).thenReturn(Optional.of(record));
        when(storageService.upload(any(), anyString(), anyString(), anyLong(), anyString())).thenReturn(uploaded);
        doNothing().when(record).addDocument(any());
        when(documentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(recordRepository.save(any(StudentRecord.class))).thenReturn(record);
        when(mapper.toDocumentResponse(any())).thenReturn(mock(RecordDocumentResponse.class));

        RecordDocumentResponse result = useCase.execute(RECORD_ID, request, file, UUID.randomUUID());

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("execute — legajo no encontrado — lanza RecordNotFoundException")
    void execute_notFound_throwsException() {
        UploadDocumentRequest request = buildRequest();
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("application/pdf");
        when(file.getSize()).thenReturn(1024L);

        when(recordRepository.findByRecordId(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(RECORD_ID, request, file, UUID.randomUUID()))
                .isInstanceOf(RecordNotFoundException.class);
    }

    @Test
    @DisplayName("execute — archivo vacío — lanza IllegalArgumentException")
    void execute_emptyFile_throwsException() {
        UploadDocumentRequest request = buildRequest();
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(RECORD_ID, request, file, UUID.randomUUID()))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
