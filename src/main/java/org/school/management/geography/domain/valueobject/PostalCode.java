package org.school.management.geography.domain.valueobject;

import lombok.Value;

@Value
public class PostalCode {
    String value;

    public static PostalCode of(String value) {
        if (value == null || value.isBlank()) {
            return null; // Código postal es opcional
        }

        String normalized = value.trim();

        // Validar formato argentino (4 dígitos generalmente, pero puede variar)
        if (!normalized.matches("^[0-9A-Z]{4,10}$")) {
            throw new IllegalArgumentException("Invalid postal code format");
        }

        return new PostalCode(normalized);
    }

    @Override
    public String toString() {
        return value;
    }
}
