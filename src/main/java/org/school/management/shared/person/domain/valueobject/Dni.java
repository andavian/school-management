// src/main/java/org/school/management/shared/person/domain/valueobject/Dni.java
package org.school.management.shared.person.domain.valueobject;

/**
 * Value Object inmutable para DNI argentino.
 * Valida dígito verificador oficial + tipos especiales (menores, extranjeros).
 */
public record Dni(String value) {

    private static final int[] WEIGHTS = {2, 7, 6, 5, 4, 3, 2};

    public Dni {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El DNI no puede estar vacío");
        }

        String cleaned = value.trim().replaceAll("[^0-9]", "");

        if (!cleaned.matches("^\\d{7,8}$")) {
            throw new IllegalArgumentException("DNI argentino debe tener 7 u 8 dígitos: " + value);
        }

        // Validación del dígito verificador (solo para DNIs de 8 dígitos emitidos en Argentina)
        if (cleaned.length() == 8) {
            int prefix = Integer.parseInt(cleaned.substring(0, 2));
            // Solo validamos verificador si es DNI argentino (no 90-99)
            if (prefix < 90 && !isValidCheckDigit(cleaned)) {
                throw new IllegalArgumentException("DNI inválido: dígito verificador incorrecto: " + value);
            }
        }

        // Normalización final: quitamos ceros a la izquierda (excepto si es 0)
        if (cleaned.length() == 8 && cleaned.startsWith("0")) {
            cleaned = cleaned.substring(1);
        }

        value = cleaned;
    }

    private static boolean isValidCheckDigit(String dni8) {
        char[] digits = dni8.toCharArray();
        int sum = 0;
        for (int i = 0; i < 7; i++) {
            sum += Character.getNumericValue(digits[i]) * WEIGHTS[i];
        }
        int checkDigit = (10 - (sum % 10)) % 10;
        return checkDigit == Character.getNumericValue(digits[7]);
    }

    public static Dni of(String value) {
        return new Dni(value);
    }

    public static Dni of(long number) {
        return new Dni(String.valueOf(number));
    }

    /** Formato oficial argentino con puntos */
    public String formatted() {
        return switch (value.length()) {
            case 7 -> value.charAt(0) + "." + value.substring(1, 4) + "." + value.substring(4);
            case 8 -> value.substring(0, 2) + "." + value.substring(2, 5) + "." + value.substring(5);
            default -> value;
        };
    }

    /** DNI de menor (91xxxxxx, 92xxxxxx, etc) */
    public boolean isMinor() {
        if (value.length() != 8) return false;
        int prefix = Integer.parseInt(value.substring(0, 2));
        return prefix >= 91 && prefix <= 95;
    }

    /** DNI de extranjero (90xxxxxx, 94xxxxxx, etc) */
    public boolean isForeigner() {
        if (value.length() != 8) return false;
        int prefix = Integer.parseInt(value.substring(0, 2));
        return prefix == 90 || prefix >= 94;
    }

    /** DNI argentino común (00–89) */
    public boolean isArgentinean() {
        return !isMinor() && !isForeigner();
    }

    public long asLong() {
        return Long.parseLong(value);
    }

    @Override
    public String toString() {
        return "DNI[" + formatted() + "]";
    }
}