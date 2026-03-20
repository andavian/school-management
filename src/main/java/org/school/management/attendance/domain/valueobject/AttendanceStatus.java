// org.school.management.attendance.domain.valueobject.AttendanceStatus
package org.school.management.attendance.domain.valueobject;

/**
 * Estado de asistencia con su peso en faltas según reglas IPET 132.
 * ABSENT=1.0 | JUSTIFIED=1.0 | LATE=0.2 | WITHDRAWN=0.2 | PRESENT=0.0
 */
public enum AttendanceStatus {

    PRESENT(0.0),
    ABSENT(1.0),
    JUSTIFIED(1.0),   // Justificada — igual peso que ABSENT, solo registra el motivo
    LATE(0.2),        // Tardanza — 5 tardanzas = 1 falta
    WITHDRAWN(0.2);   // Retiro anticipado — mismo peso que tardanza

    private final double absenceWeight;

    AttendanceStatus(double absenceWeight) {
        this.absenceWeight = absenceWeight;
    }

    public double getAbsenceWeight() {
        return absenceWeight;
    }

    public boolean isAbsent() {
        return this == ABSENT || this == JUSTIFIED;
    }

    public boolean canBeJustified() {
        return this == ABSENT;
    }
}