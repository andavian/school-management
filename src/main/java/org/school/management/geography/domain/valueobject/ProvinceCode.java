package org.school.management.geography.domain.valueobject;

import java.util.Optional;

public record ProvinceCode(String value) {

    public ProvinceCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ProvinceCode cannot be null or blank");
        }

        value = value.trim().toUpperCase();

        if (value.length() > 10) {
            throw new IllegalArgumentException("Province code cannot exceed 10 characters");
        }
    }


    public static ProvinceCode of(String value) {
        return new ProvinceCode(value);
    }


    public static Optional<ProvinceCode> ofNullable(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(new ProvinceCode(value));
    }

    @Override
    public String toString() {
        return value;
    }
}