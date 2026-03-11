package org.school.management.students.health.domain.exception;

import org.school.management.shared.domain.exception.DomainException;

import java.util.UUID;

public class HealthRecordNotFoundException extends DomainException {

    public HealthRecordNotFoundException(String message) {
        super(message);
    }

    public static HealthRecordNotFoundException byStudentId(UUID studentId) {
        return new HealthRecordNotFoundException(
                "Health record not found for student with id: " + studentId
        );
    }

    public static HealthRecordNotFoundException byHealthRecordId(UUID healthRecordId) {
        return new HealthRecordNotFoundException(
                "Health record not found with id: " + healthRecordId
        );
    }
}