package org.school.management.students.records.domain.exception;

import org.school.management.shared.domain.exception.DomainException;

import java.util.UUID;

public class DocumentNotFoundException extends DomainException {

    public DocumentNotFoundException(String message) {
        super(message);
    }

    public static DocumentNotFoundException byId(UUID documentId) {
        return new DocumentNotFoundException(
                "Document not found with id: " + documentId
        );
    }
}
