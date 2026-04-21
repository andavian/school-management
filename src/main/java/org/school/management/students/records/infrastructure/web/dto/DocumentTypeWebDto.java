package org.school.management.students.records.infrastructure.web.dto;

import jakarta.validation.constraints.*;
import org.school.management.students.records.domain.valueobject.DocumentCategory;

import java.util.UUID;

public final class DocumentTypeWebDto {

    private DocumentTypeWebDto() {}

    public record CreateDocumentTypeWebRequest(

            @NotBlank(message = "Name is required")
            @Size(max = 100)
            String name,

            @NotBlank(message = "Code is required")
            @Size(max = 20)
            @Pattern(
                    regexp = "^[A-Z_]+$",
                    message = "Code must be in UPPERCASE_SNAKE_CASE format"
            )
            String code,

            @NotNull(message = "Category is required")
            DocumentCategory category,

            boolean mandatory,

            @Size(max = 1000)
            String description,

            @Min(1)
            Integer validForYears
    ) {}

    public record UpdateDocumentTypeWebRequest(

            @NotBlank(message = "Name is required")
            @Size(max = 100)
            String name,

            @NotNull(message = "Category is required")
            DocumentCategory category,

            boolean mandatory,

            @Size(max = 1000)
            String description,

            @Min(1)
            Integer validForYears
    ) {}

    public record DocumentTypeWebResponse(
            UUID documentTypeId,
            String name,
            String code,
            DocumentCategory category,
            boolean mandatory,
            String description,
            Integer validForYears,
            boolean permanent,
            boolean active
    ) {}
}