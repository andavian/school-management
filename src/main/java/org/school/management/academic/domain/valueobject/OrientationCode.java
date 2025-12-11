package org.school.management.academic.domain.valueobject;

import lombok.Value;

@Value
public class OrientationCode {
    String value;

    public static OrientationCode of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Orientation code cannot be null or empty");
        }

        String normalized = value.trim().toUpperCase();

        if (normalized.length() > 20) {
            throw new IllegalArgumentException("Orientation code cannot exceed 20 characters");
        }

        return new OrientationCode(normalized);
    }
}
