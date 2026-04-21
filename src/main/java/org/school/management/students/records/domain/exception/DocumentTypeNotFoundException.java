package org.school.management.students.records.domain.exception;

import org.school.management.shared.domain.exception.DomainException;

import java.util.UUID;

public class DocumentTypeNotFoundException extends DomainException {

    public DocumentTypeNotFoundException(String message) {
        super(message);
    }

    public static DocumentTypeNotFoundException byId(UUID id) {
        return new DocumentTypeNotFoundException(
                "Document type not found with id: " + id
        );
    }

    public static DocumentTypeNotFoundException byCode(String code) {
        return new DocumentTypeNotFoundException(
                "Document type not found with code: " + code
        );
    }
}