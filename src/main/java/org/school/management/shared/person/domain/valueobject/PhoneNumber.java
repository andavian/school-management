// src/main/java/org/school/management/shared/person/domain/valueobject/PhoneNumber.java
package org.school.management.shared.person.domain.valueobject;

import lombok.ToString;

import java.util.regex.Pattern;

/**
 * Value Object inmutable que representa un número telefónico argentino válido.
 * Soporta todos los formatos reales del país (2025).
 */

public record PhoneNumber(String value) {

    private static final Pattern ARGENTINE_PHONE_PATTERN = Pattern.compile(
            // +54 9 261 1234567   |   261 15 1234567   |   011 12345678   |   15 1234567890
            "^(\\+?54)?\\s?9?\\s?(\\d{3,4})\\s?\\d{6,8}$|^0?15?\\s?d{10}$"
    );

    private static final int MIN_DIGITS = 10;
    private static final int MAX_DIGITS = 13; // +54 9 999 9999999

    public PhoneNumber {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El teléfono no puede estar vacío");
        }

        String cleaned = value.replaceAll("[^\\d+]", "");

        if (cleaned.length() < MIN_DIGITS || cleaned.length() > MAX_DIGITS) {
            throw new IllegalArgumentException(
                    "Teléfono inválido: debe tener entre 10 y 13 dígitos (ej: 2611234567 o +5492611234567)"
            );
        }

        if (!ARGENTINE_PHONE_PATTERN.matcher(value).matches() &&
                !ARGENTINE_PHONE_PATTERN.matcher(cleaned).matches()) {
            throw new IllegalArgumentException("Formato de teléfono argentino no válido");
        }

        // Normalización final: guardamos siempre con +54 al inicio si es argentino
        if (cleaned.startsWith("549") && !cleaned.startsWith("+549")) {
            cleaned = "+" + cleaned;
        } else if (cleaned.startsWith("54") && !cleaned.startsWith("+54")) {
            cleaned = "+" + cleaned;
        } else if (cleaned.matches("^\\d{10,11}$")) {
            cleaned = "+54" + cleaned.replaceFirst("^0", ""); // quita el 0 inicial si lo tiene
        }

        value = cleaned;
    }

    /**
     * Formato oficial argentino para documentos y WhatsApp
     * Ejemplos:
     *   +54 9 261 123-4567  (móvil)
     *   +54 261 123-4567    (fijo)
     */
    public String formatted() {
        String digits = value.replaceAll("\\D", ""); // solo números

        if (value.contains("+54")) {
            if (digits.length() == 13 && digits.startsWith("549")) {
                // +54 9 261 1234567
                String area = digits.substring(3, 6);   // 261
                String part1 = digits.substring(6, 10); // 1234
                String part2 = digits.substring(10);    // 567
                return String.format("+54 9 %s %s-%s", area, part1, part2);
            } else if (digits.length() == 12 && digits.startsWith("54")) {
                // +54 261 1234567
                String area = digits.substring(2, 5);
                String part1 = digits.substring(5, 9);
                String part2 = digits.substring(9);
                return String.format("+54 %s %s-%s", area, part1, part2);
            }
        }
        return value; // fallback
    }

    public boolean isMobile() {
        String digits = value.replaceAll("\\D", "");
        return digits.length() >= 12 && (digits.startsWith("549") || digits.matches(".*15.*"));
    }

    public boolean isLandline() {
        return !isMobile();
    }

    public String whatsappLink() {
        return "https://wa.me/" + value.replaceAll("\\D", "");
    }

    public static PhoneNumber of(String value) {
        return new PhoneNumber(value);
    }

    @Override
    public String toString() {
        return formatted();
    }
}