package org.school.management.geography.domain.valueobject;

import java.util.Set;

public record GeographicName(String value) {

    private static final Set<String> LOWERCASE_WORDS = Set.of(
            "de", "del", "la", "las", "el", "los"
    );

    public GeographicName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Geographic name cannot be null or empty");
        }
        if (value.trim().length() < 2) {
            throw new IllegalArgumentException("Geographic name must have at least 2 characters");
        }
        if (value.trim().length() > 100) {
            throw new IllegalArgumentException("Geographic name cannot exceed 100 characters");
        }
        value = capitalizeWords(value.trim());
    }

    public static GeographicName of(String value) {
        return new GeographicName(value);
    }

    private static String capitalizeWords(String str) {
        String[] words = str.split("\\s+");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (i > 0 && LOWERCASE_WORDS.contains(word.toLowerCase())) {
                result.append(word.toLowerCase());
            } else {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase());
            }
            if (i < words.length - 1) result.append(" ");
        }
        return result.toString();
    }

    @Override
    public String toString() {
        return value;
    }
}