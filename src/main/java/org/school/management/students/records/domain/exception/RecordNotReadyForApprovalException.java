package org.school.management.students.records.domain.exception;

import org.school.management.shared.domain.exception.DomainException;

public class RecordNotReadyForApprovalException extends DomainException {
    public RecordNotReadyForApprovalException(String message) {
        super(message);
    }
}