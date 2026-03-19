package org.school.management.course.domain.valueobject;

public enum SubjectEnrollmentStatus {
    ENROLLED,      // Inscripto
    ATTENDING,     // Cursando
    PASSED,        // Aprobado
    FAILED,        // Reprobado
    PENDING_EXAM,  // Adeuda examen
    FREE,          // Libre por inasistencias
    WITHDRAWN;     // Retirado

    public boolean isTerminal() {
        return this == PASSED || this == FAILED || this == FREE || this == WITHDRAWN;
    }

    public boolean isActive() {
        return this == ENROLLED || this == ATTENDING;
    }
}