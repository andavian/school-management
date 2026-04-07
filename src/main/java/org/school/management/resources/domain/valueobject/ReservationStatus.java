package org.school.management.resources.domain.valueobject;

/**
 * Ciclo de vida de la reserva.
 * CONFIRMED → IN_USE → RETURNED
 *      ↘ CANCELLED (antes de IN_USE)
 */
public enum ReservationStatus {
    CONFIRMED,
    IN_USE,
    RETURNED,
    CANCELLED;

    public boolean isTerminal() {
        return this == RETURNED || this == CANCELLED;
    }

    public boolean isCancelable() {
        return this == CONFIRMED;
    }
}