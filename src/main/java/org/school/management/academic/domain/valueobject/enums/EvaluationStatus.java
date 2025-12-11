package org.school.management.academic.domain.valueobject.enums;

public enum EvaluationStatus {
    PENDING,    // Pendiente de tomar
    TAKEN,      // Tomada, pendiente calificar
    GRADED,     // Calificada
    VALIDATED,  // Validada
    CANCELLED   // Cancelada
}
