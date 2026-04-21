package org.school.management.students.records.application.dto.request;

import jakarta.validation.constraints.*;
import org.school.management.students.records.domain.valueobject.DocumentCategory;

public final class DocumentTypeRequest {

    private DocumentTypeRequest() {}

    public record CreateDocumentTypeRequest(

            @NotBlank(message = "Name is required")
            @Size(max = 100, message = "Name must not exceed 100 characters")
            String name,

            @NotBlank(message = "Code is required")
            @Size(max = 20, message = "Code must not exceed 20 characters")
            @Pattern(
                    regexp = "^[A-Z_]+$",
                    message = "Code must be in UPPERCASE_SNAKE_CASE format"
            )
            String code,

            @NotNull(message = "Category is required")
            DocumentCategory category,

            boolean mandatory,

            @Size(max = 1000, message = "Description must not exceed 1000 characters")
            String description,

            @Min(value = 1, message = "Valid years must be at least 1")
            Integer validForYears  // NULL = permanente
    ) {}

    public record UpdateDocumentTypeRequest(

            @NotBlank(message = "Name is required")
            @Size(max = 100, message = "Name must not exceed 100 characters")
            String name,

            @NotNull(message = "Category is required")
            DocumentCategory category,

            boolean mandatory,

            @Size(max = 1000, message = "Description must not exceed 1000 characters")
            String description,

            @Min(value = 1, message = "Valid years must be at least 1")
            Integer validForYears
    ) {}
}