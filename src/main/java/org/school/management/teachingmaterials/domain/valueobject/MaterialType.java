package org.school.management.teachingmaterials.domain.valueobject;

/**
 * Tipo de material pedagógico.
 * Enum fijo — los tipos son estables y no requieren persistencia en tabla separada.
 */
public enum MaterialType {
    APUNTE,
    EJERCICIO,
    EXAMEN,
    GUIA,
    VIDEO,
    OTRO
}