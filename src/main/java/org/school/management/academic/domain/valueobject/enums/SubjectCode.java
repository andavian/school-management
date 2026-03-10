package org.school.management.academic.domain.valueobject.enums;

public record SubjectCode(String value) {

    public SubjectCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Subject code cannot be null or empty");
        }
        value = value.trim().toUpperCase();
        if (value.length() > 20) {
            throw new IllegalArgumentException("Subject code cannot exceed 20 characters");
        }
    }

    public static SubjectCode of(String value) {
        return new SubjectCode(value);
    }
}
