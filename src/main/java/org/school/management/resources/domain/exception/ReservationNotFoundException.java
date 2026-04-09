// src/main/java/org/school/management/resources/domain/exception/ReservationNotFoundException.java
package org.school.management.resources.domain.exception;

import org.school.management.shared.domain.exception.DomainException;
import org.school.management.resources.domain.valueobject.ReservationId;

import java.util.UUID;

public class ReservationNotFoundException extends DomainException {

    public ReservationNotFoundException(String message) {
        super(message);
    }

    public static ReservationNotFoundException byId(ReservationId id) {
        return new ReservationNotFoundException("Reservation not found with id: " + id.value());
    }

    public static ReservationNotFoundException byId(UUID id) {
        return new ReservationNotFoundException("Reservation not found with id: " + id);
    }
}