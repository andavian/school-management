package org.school.management.geography.domain.valueobject;

import lombok.Value;

@Value
public class PhoneCode {
    String value;

    public static PhoneCode of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Phone code cannot be null or empty");
        }

        String normalized = value.trim();

        // Agregar + si no lo tiene
        if (!normalized.startsWith("+")) {
            normalized = "+" + normalized;
        }

        // Validar formato: +seguido de 1-4 dígitos
        if (!normalized.matches("^\\+[0-9]{1,4}$")) {
            throw new IllegalArgumentException("Invalid phone code format. Expected: +XX or +XXX");
        }

        return new PhoneCode(normalized);
    }

    @Override
    public String toString() {
        return value;
    }
}

