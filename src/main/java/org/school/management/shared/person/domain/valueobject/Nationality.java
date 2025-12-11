// src/main/java/org/school/management/shared/person/domain/valueobject/Nationality.java
package org.school.management.shared.person.domain.valueobject;

public record Nationality(String value) {

    public Nationality {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Nacionalidad requerida");
        }
        value = value.trim().toUpperCase();
    }

    public static final Nationality ARGENTINA = new Nationality("ARGENTINA");
}