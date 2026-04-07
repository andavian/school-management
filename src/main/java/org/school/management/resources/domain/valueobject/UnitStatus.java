package org.school.management.resources.domain.valueobject;

/**
 * Ciclo de vida operativo de la unidad física.
 * Las transiciones se validan en ResourceUnit.
 */
public enum UnitStatus {
    AVAILABLE,    // Disponible para reservar
    IN_USE,       // Actualmente asignada a reserva activa
    MAINTENANCE,  // En revisión/mantenimiento (no reservable)
    ON_LOAN,      // Préstamo externo (sale de la institución)
    RETIRED;      // Baja definitiva

    public boolean isAvailableForReservation() {
        return this == AVAILABLE;
    }

    public boolean isTerminal() {
        return this == RETIRED;
    }
}