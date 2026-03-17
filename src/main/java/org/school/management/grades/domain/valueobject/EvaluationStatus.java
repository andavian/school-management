package org.school.management.grades.domain.valueobject;

public enum EvaluationStatus {
    PENDING,    // Pendiente de tomar
    TAKEN,      // Tomada, pendiente calificar
    GRADED,     // Calificada
    VALIDATED,  // Validada
    CANCELLED   // Cancelada
}
