package org.school.management.resources.domain.exception;

import org.school.management.shared.domain.exception.DomainException;

// ReservationAccessDeniedException.java
public class ReservationAccessDeniedException extends DomainException {
    public ReservationAccessDeniedException(String message) {
        super(message);
    }

    public static ReservationAccessDeniedException notOwner() {
        return new ReservationAccessDeniedException("You are not the owner of this reservation");
    }
}
