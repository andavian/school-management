package org.school.management.teachers.domain.valueobject;

import java.util.Set;

public record Division(String value) {

    private static final Set<String> ALLOWED = Set.of("A", "B", "C", "D", "E");

    public Division {


        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Division no puede ser nulo ni vacío");
        }
        String normalized = value.trim().toUpperCase();

        if (!ALLOWED.contains(normalized)) {
            throw new IllegalArgumentException("Division inválida: " + value);
        }

        value = normalized;
    }

    public static Division of(String value) {
        return new Division(value);
    }
}
