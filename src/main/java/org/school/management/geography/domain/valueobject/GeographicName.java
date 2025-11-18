package org.school.management.geography.domain.valueobject;

import lombok.Value;

@Value
public class GeographicName {
    String value;

    public static GeographicName of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Geographic name cannot be null or empty");
        }

        String normalized = value.trim();

        if (normalized.length() < 2) {
            throw new IllegalArgumentException("Geographic name must have at least 2 characters");
        }

        if (normalized.length() > 100) {
            throw new IllegalArgumentException("Geographic name cannot exceed 100 characters");
        }

        // Capitalizar cada palabra
        normalized = capitalizeWords(normalized);

        return new GeographicName(normalized);
    }

    private static String capitalizeWords(String str) {
        String[] words = str.split("\\s+");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            String word = words[i];

            // Palabras especiales que van en minúscula (excepto al inicio)
            if (i > 0 && (word.equalsIgnoreCase("de") ||
                    word.equalsIgnoreCase("del") ||
                    word.equalsIgnoreCase("la") ||
                    word.equalsIgnoreCase("las") ||
                    word.equalsIgnoreCase("el") ||
                    word.equalsIgnoreCase("los"))) {
                result.append(word.toLowerCase());
            } else {
                // Capitalizar primera letra
                result.append(word.substring(0, 1).toUpperCase())
                        .append(word.substring(1).toLowerCase());
            }

            if (i < words.length - 1) {
                result.append(" ");
            }
        }

        return result.toString();
    }

    @Override
    public String toString() {
        return value;
    }
}
