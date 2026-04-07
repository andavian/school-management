package org.school.management.resources.domain.valueobject;

/**
 * Estado físico / condición de la unidad.
 * No determina disponibilidad (eso lo maneja UnitStatus).
 */
public enum ConditionStatus {
    GOOD,   // Buen estado
    FAIR,   // Estado regular, funciona pero requiere atención
    POOR;   // Mal estado, requiere mantenimiento o baja
}