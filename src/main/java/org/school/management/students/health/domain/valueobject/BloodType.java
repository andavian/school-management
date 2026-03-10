package org.school.management.students.health.domain.valueobject;

import lombok.Getter;

/**
 * Value Object: Tipo de sangre
 * Grupos sanguíneos según sistema ABO y factor Rh
 */
@Getter
public enum BloodType {

    A_POSITIVE("A+"),
    A_NEGATIVE("A-"),
    B_POSITIVE("B+"),
    B_NEGATIVE("B-"),
    AB_POSITIVE("AB+"),
    AB_NEGATIVE("AB-"),
    O_POSITIVE("O+"),
    O_NEGATIVE("O-");

    private final String displayName;

    BloodType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Valida si este tipo de sangre puede donar a otro
     */
    public boolean canDonateTo(BloodType recipient) {
        // O- es donante universal
        if (this == O_NEGATIVE) {
            return true;
        }

        // AB+ es receptor universal
        if (recipient == AB_POSITIVE) {
            return true;
        }

        // Lógica de compatibilidad ABO y Rh
        return switch (this) {
            case A_POSITIVE -> recipient == A_POSITIVE || recipient == AB_POSITIVE;
            case A_NEGATIVE -> recipient == A_POSITIVE || recipient == A_NEGATIVE ||
                    recipient == AB_POSITIVE || recipient == AB_NEGATIVE;
            case B_POSITIVE -> recipient == B_POSITIVE || recipient == AB_POSITIVE;
            case B_NEGATIVE -> recipient == B_POSITIVE || recipient == B_NEGATIVE ||
                    recipient == AB_POSITIVE || recipient == AB_NEGATIVE;
            case AB_POSITIVE -> recipient == AB_POSITIVE;
            case AB_NEGATIVE -> recipient == AB_POSITIVE || recipient == AB_NEGATIVE;
            case O_POSITIVE -> recipient == O_POSITIVE || recipient == A_POSITIVE ||
                    recipient == B_POSITIVE || recipient == AB_POSITIVE;
            case O_NEGATIVE -> true; // Ya manejado arriba
        };
    }

    /**
     * Busca un BloodType por su displayName (A+, B-, AB+, etc.)
     * Es el método de entrada desde la API — el DTO envía el valor legible.
     */
    public static BloodType fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Blood type cannot be null or empty");
        }
        String normalized = value.trim().toUpperCase();
        for (BloodType type : values()) {
            if (type.displayName.equalsIgnoreCase(normalized)) {
                return type;
            }
        }
        throw new IllegalArgumentException(
                "Invalid blood type: " + value +
                        ". Valid values: A+, A-, B+, B-, AB+, AB-, O+, O-"
        );
    }

    @Override
    public String toString() {
        return displayName;
    }
}