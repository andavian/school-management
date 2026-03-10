package org.school.management.geography.domain.valueobject;

public record PhoneCode(String value) {

    public PhoneCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Phone code cannot be null or empty");
        }

        value = value.trim();

        if (!value.startsWith("+")) {
            value = "+" + value;
        }

        if (!value.matches("^\\+[0-9]{1,4}$")) {
            throw new IllegalArgumentException("Invalid phone code format. Expected: +XX or +XXX");
        }
    }

    public static PhoneCode of(String value) {
        return new PhoneCode(value);
    }

    @Override
    public String toString() {
        return value;
    }
}