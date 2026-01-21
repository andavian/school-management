package org.school.management.students.records.domain.exception;

import org.school.management.shared.domain.exception.DomainException;

public class DocumentAlreadyApprovedException extends DomainException {
    public DocumentAlreadyApprovedException(String message) {
        super(message);
    }
}