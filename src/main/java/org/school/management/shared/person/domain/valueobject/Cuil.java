// src/main/java/org/school/management/shared/person/domain/valueobject/Cuil.java
package org.school.management.shared.person.domain.valueobject;

import java.util.Set;

/**
 * Value Object inmutable para CUIL argentino (ANSES).
 * Valida prefijos correctos, dígito verificador y compatibilidad con CUIT (AFIP) para entidades.
 * Formato: 11 dígitos, prefijo + DNI + verificador.
 */
public record Cuil(String value) {

    // Prefijos válidos ANSES/AFIP (actualizado 2025)
    private static final Set<String> VALID_PREFIXES = Set.of(
            "20", // Hombres argentinos
            "27", // Mujeres argentinas
            "23", // Empresas (CUIT)
            "24", // Extranjeros Mercosur
            "30", // Sociedades / Entidades (CUIT)
            "33", // Hombres extranjeros / especiales
            "34"  // Mujeres extranjeras / especiales
    );

    // Pesos para dígito verificador oficial
    private static final int[] WEIGHTS = {5, 4, 3, 2, 7, 6, 5, 4, 3, 2};

    public Cuil {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("El CUIL no puede estar vacío");
        }

        String cleaned = value.trim().replaceAll("[^0-9]", "");

        if (cleaned.length() != 11) {
            throw new IllegalArgumentException("CUIL debe tener exactamente 11 dígitos: " + value);
        }

        String prefix = cleaned.substring(0, 2);
        if (!VALID_PREFIXES.contains(prefix)) {
            throw new IllegalArgumentException("Prefijo CUIL inválido: " + prefix + " (válidos: 20,23,24,27,30,33,34)");
        }

        if (!isValidCheckDigit(cleaned)) {
            throw new IllegalArgumentException("CUIL inválido: dígito verificador incorrecto: " + value);
        }

        value = cleaned;
    }

    private static boolean isValidCheckDigit(String cuil11) {
        int sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += Character.getNumericValue(cuil11.charAt(i)) * WEIGHTS[i];
        }
        int checkDigit = (11 - (sum % 11)) % 11;
        if (checkDigit == 10) checkDigit = 9; // Regla especial ANSES/AFIP
        return checkDigit == Character.getNumericValue(cuil11.charAt(10));
    }

    public static Cuil of(String value) {
        return new Cuil(value);
    }

    public static Cuil of(long number) {
        return new Cuil(String.valueOf(number));
    }

    /**
     * Extrae el DNI del CUIL (sin prefijo ni verificador)
     */
    public Dni extractDni() {
        String dniPart = value.substring(2, 10);
        return Dni.of(dniPart);
    }

    /**
     * Formato oficial con guiones: 20-30123457-8
     */
    public String formatted() {
        return value.substring(0, 2) + "-" +
                value.substring(2, 10) + "-" +
                value.charAt(10);
    }

    /**
     * Tipo de CUIL según prefijo ANSES/AFIP
     */
    public CuilType getType() {
        return switch (value.substring(0, 2)) {
            case "20" -> CuilType.MALE_ARGENTINEAN;
            case "27" -> CuilType.FEMALE_ARGENTINEAN;
            case "24" -> CuilType.FOREIGN_MERCOSUR;
            case "23", "30" -> CuilType.LEGAL_ENTITY;
            case "33" -> CuilType.MALE_FOREIGN;
            case "34" -> CuilType.FEMALE_FOREIGN;
            default -> CuilType.UNKNOWN;
        };
    }

    public boolean isPerson() {
        return getType() == CuilType.MALE_ARGENTINEAN ||
                getType() == CuilType.FEMALE_ARGENTINEAN ||
                getType() == CuilType.MALE_FOREIGN ||
                getType() == CuilType.FEMALE_FOREIGN ||
                getType() == CuilType.FOREIGN_MERCOSUR;
    }

    public boolean isEntity() {
        return getType() == CuilType.LEGAL_ENTITY;
    }

    public boolean isForeign() {
        return getType() == CuilType.FOREIGN_MERCOSUR ||
                getType() == CuilType.MALE_FOREIGN ||
                getType() == CuilType.FEMALE_FOREIGN;
    }

    @Override
    public String toString() {
        return "CUIL[" + formatted() + "]";
    }
}

