package org.school.management.students.parents.domain.exception;

import org.school.management.shared.domain.exception.DomainException;

import java.util.UUID;

public class DuplicatePrimaryContactException extends DomainException {

    public DuplicatePrimaryContactException(String message) {
        super(message);
    }

    public static DuplicatePrimaryContactException forStudent(UUID studentId) {
        return new DuplicatePrimaryContactException(
                "Student already has a primary contact: " + studentId
        );
    }
}