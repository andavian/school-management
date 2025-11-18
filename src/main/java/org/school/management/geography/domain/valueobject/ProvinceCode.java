package org.school.management.geography.domain.valueobject;

import lombok.Value;

@Value
public class ProvinceCode {
    String value;

    public static ProvinceCode of(String value) {
        if (value == null || value.isBlank()) {
            return null; // El código de provincia es opcional
        }

        String normalized = value.trim().toUpperCase();

        if (normalized.length() > 10) {
            throw new IllegalArgumentException("Province code cannot exceed 10 characters");
        }

        return new ProvinceCode(normalized);
    }

    @Override
    public String toString() {
        return value;
    }
}

