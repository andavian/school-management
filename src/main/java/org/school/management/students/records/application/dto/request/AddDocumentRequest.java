package org.school.management.students.records.application.dto.request;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Request para agregar un documento al legajo del estudiante.
 */
public record AddDocumentRequest(

        @NotNull(message = "El tipo de documento es obligatorio")
        UUID documentTypeId,

        @NotBlank(message = "El título es obligatorio")
        @Size(max = 200, message = "El título no puede superar los 200 caracteres")
        String title,

        @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
        String description,

        // Archivo
        @NotBlank(message = "La ruta del archivo es obligatoria")
        @Size(max = 500)
        String filePath,

        @NotBlank(message = "El nombre del archivo es obligatorio")
        @Size(max = 255)
        String fileName,

        @NotNull(message = "El tamaño del archivo es obligatorio")
        @Min(value = 1, message = "El tamaño del archivo debe ser mayor a 0")
        Long fileSizeBytes,

        @NotBlank(message = "El tipo MIME es obligatorio")
        @Size(max = 100)
        String mimeType,

        // Metadatos opcionales
        @PastOrPresent(message = "La fecha de emisión no puede ser futura")
        LocalDate issueDate,

        LocalDate expiryDate,

        @Size(max = 200, message = "La autoridad emisora no puede superar los 200 caracteres")
        String issuingAuthority
) {}