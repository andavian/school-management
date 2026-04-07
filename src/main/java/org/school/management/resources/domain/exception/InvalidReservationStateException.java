package org.school.management.resources.domain.exception;

import org.school.management.shared.domain.exception.DomainException;
import org.school.management.resources.domain.valueobject.ReservationId;
import org.school.management.resources.domain.valueobject.ReservationStatus;

public class InvalidReservationStateException extends DomainException {

    public InvalidReservationStateException(String message) {
        super(message);
    }

    public static InvalidReservationStateException invalidTransition(ReservationId id, ReservationStatus current, String requestedAction) {
        return new InvalidReservationStateException(
                String.format("Invalid transition for reservation %s: cannot %s while status is %s",
                        id.value().toString(), requestedAction, current));
    }
}