package org.school.management.students.records.domain.exception;

import org.school.management.shared.domain.exception.DomainException;

public class RecordAlreadyApprovedException extends DomainException {
    public RecordAlreadyApprovedException(String message) {
        super(message);
    }
}