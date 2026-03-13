package org.school.management.students.records.domain.valueobject;

/**
 * Número de legajo del estudiante.
 * Igual al DNI del estudiante — 8 dígitos numéricos.
 * Es único por estudiante y permanente (no cambia entre años).
 */
public record RecordNumber(String value) {

    public RecordNumber {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Record number cannot be null or empty"
            );
        }
        value = value.trim();
        if (!value.matches("^\\d{8}$")) {
            throw new IllegalArgumentException(
                    "Invalid record number format. Expected 8 digits (DNI), got: " + value
            );
        }
    }

    public static RecordNumber of(String value) {
        return new RecordNumber(value);
    }

    /**
     * Crea el número de legajo a partir del DNI del estudiante.
     * Es el factory method principal para este dominio.
     */
    public static RecordNumber fromDni(String dni) {
        return new RecordNumber(dni);
    }
}