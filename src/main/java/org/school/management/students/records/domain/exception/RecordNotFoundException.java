package org.school.management.students.records.domain.exception;

import org.school.management.shared.domain.exception.DomainException;

import java.util.UUID;

public class RecordNotFoundException extends DomainException {

    public RecordNotFoundException(String message) {
        super(message);
    }

    public static RecordNotFoundException byStudentId(UUID studentId) {
        return new RecordNotFoundException(
                "Record not found for studentId: " + studentId
        );
    }

    public static RecordNotFoundException byRecordId(UUID recordId) {
        return new RecordNotFoundException(
                "Record not found with id: " + recordId
        );
    }

    public static RecordNotFoundException byRecordNumber(String recordNumber) {
        return new RecordNotFoundException(
                "Record not found with number: " + recordNumber
        );
    }
}