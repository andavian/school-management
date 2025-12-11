// src/main/java/org/school/management/shared/person/domain/valueobject/PhoneNumber.java
package org.school.management.shared.person.domain.valueobject;

import org.school.management.shared.domain.exception.DomainException;

public record PhoneNumber(String value) {

    private static final String PHONE_REGEX = "^\\+?54\\s?9?\\s?(\\d{3,4})\\s?\\d{6,8}$|^0?15?\\s?\\d{10}$";

    public PhoneNumber {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Teléfono no puede estar vacío");
        }
        String cleaned = value.replaceAll("[^\\d+]", "");
        if (!cleaned.matches(".{10,}")) {
            throw new IllegalArgumentException("Teléfono debe tener al menos 10 dígitos");
        }
        value = cleaned;
    }

    public String formatted() {
        // +54 9 261 123-4567
        if (value.startsWith("54")) {
            return value.replaceFirst("(\\d{2})(\\d{1})?(\\d{3})(\\d{4})(\\d+)", "+$1 9 $3 $4-$5");
        }
        return value;
    }
}