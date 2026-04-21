package org.school.management.students.records.domain.exception;

import org.school.management.shared.domain.exception.DomainException;

public class DocumentTypeAlreadyExistsException extends DomainException {

    public DocumentTypeAlreadyExistsException(String message) {
        super(message);
    }

    public static DocumentTypeAlreadyExistsException withCode(String code) {
        return new DocumentTypeAlreadyExistsException(
                "Document type already exists with code: " + code
        );
    }

    public static DocumentTypeAlreadyExistsException withName(String name) {
        return new DocumentTypeAlreadyExistsException(
                "Document type already exists with name: " + name
        );
    }
}