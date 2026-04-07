package org.school.management.resources.domain.exception;

import org.school.management.shared.domain.exception.DomainException;

// ReservationConflictException.java
public class ReservationConflictException extends DomainException {
    public ReservationConflictException(String message) {
        super(message);
    }

    public static ReservationConflictException overlapping() {
        return new ReservationConflictException("The resource is already reserved for the requested time slot");
    }
}
