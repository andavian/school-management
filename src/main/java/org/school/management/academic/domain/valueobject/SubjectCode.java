package org.school.management.academic.domain.valueobject;

import lombok.Value;

@Value
public class SubjectCode {
    String value;

    public static SubjectCode of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Subject code cannot be null or empty");
        }

        String normalized = value.trim().toUpperCase();

        if (normalized.length() > 20) {
            throw new IllegalArgumentException("Subject code cannot exceed 20 characters");
        }

        return new SubjectCode(normalized);
    }
}