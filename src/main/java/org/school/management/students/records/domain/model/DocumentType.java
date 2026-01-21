package org.school.management.students.records.domain.model;

import lombok.*;
import org.school.management.students.records.domain.valueobject.DocumentCategory;
import org.school.management.students.records.domain.valueobject.DocumentTypeId;

import java.util.Objects;

/**
 * Entity: Tipo de Documento
 *
 * Catálogo de tipos de documentos que pueden formar parte del legajo
 * Ejemplos: DNI_FRENTE, DNI_DORSO, ACTA_NACIMIENTO, CERTIFICADO_SALUD, etc.
 */
@Getter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class DocumentType {

    @EqualsAndHashCode.Include
    private final DocumentTypeId documentTypeId;
    private String name;
    private String code;
    private DocumentCategory category;
    private boolean isMandatory;
    private String description;
    private Integer validForYears;  // NULL = permanente

    // ============ Domain Logic ============

    public void update(
            String name,
            DocumentCategory category,
            boolean isMandatory,
            String description,
            Integer validForYears) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        Objects.requireNonNull(category, "Category cannot be null");

        if (validForYears != null && validForYears <= 0) {
            throw new IllegalArgumentException("Valid years must be positive");
        }

        this.name = name;
        this.category = category;
        this.isMandatory = isMandatory;
        this.description = description;
        this.validForYears = validForYears;
    }

    public void markAsMandatory() {
        this.isMandatory = true;
    }

    public void markAsOptional() {
        this.isMandatory = false;
    }

    public boolean isPermanent() {
        return validForYears == null;
    }

    public boolean hasExpiry() {
        return validForYears != null;
    }

    // Builder custom para validaciones
    public static class DocumentTypeBuilder {
        public DocumentType build() {
            Objects.requireNonNull(documentTypeId, "DocumentTypeId cannot be null");

            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Name cannot be null or empty");
            }
            if (code == null || code.isBlank()) {
                throw new IllegalArgumentException("Code cannot be null or empty");
            }
            Objects.requireNonNull(category, "Category cannot be null");

            if (!code.matches("^[A-Z_]+$")) {
                throw new IllegalArgumentException(
                        "Code must be in UPPERCASE_SNAKE_CASE format: " + code
                );
            }

            if (validForYears != null && validForYears <= 0) {
                throw new IllegalArgumentException("Valid years must be positive");
            }

            return new DocumentType(
                    documentTypeId, name, code, category, isMandatory, description, validForYears
            );
        }
    }
}

