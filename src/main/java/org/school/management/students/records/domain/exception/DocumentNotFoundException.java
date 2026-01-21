package org.school.management.students.records.domain.exception;

import org.school.management.shared.domain.exception.DomainException;

public class DocumentNotFoundException extends DomainException {
    public DocumentNotFoundException(String message) {
        super(message);
    }
}