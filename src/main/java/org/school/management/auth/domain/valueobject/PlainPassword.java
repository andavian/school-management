package org.school.management.auth.domain.valueobject;

import lombok.*;
import java.util.regex.Pattern;

@Value                                     // Inmutable
public class PlainPassword {
    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 128;
    private static final Pattern HAS_UPPER = Pattern.compile("[A-Z]");
    private static final Pattern HAS_LOWER = Pattern.compile("[a-z]");
    private static final Pattern HAS_DIGIT = Pattern.compile("\\d");
    private static final Pattern HAS_SPECIAL = Pattern.compile("[!@#$%^&*(),.?\":{}|<>]");

    String value;

    private PlainPassword(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        if (value.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("Password must be at least " + MIN_LENGTH + " characters long");
        }

        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Password cannot exceed " + MAX_LENGTH + " characters");
        }

        validateStrength(value);
        this.value = value;
    }

    private void validateStrength(String password) {
        if (!HAS_UPPER.matcher(password).find()) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter");
        }

        if (!HAS_LOWER.matcher(password).find()) {
            throw new IllegalArgumentException("Password must contain at least one lowercase letter");
        }

        if (!HAS_DIGIT.matcher(password).find()) {
            throw new IllegalArgumentException("Password must contain at least one digit");
        }

        if (!HAS_SPECIAL.matcher(password).find()) {
            throw new IllegalArgumentException("Password must contain at least one special character");
        }
    }

    public static PlainPassword of(String value) {
        return new PlainPassword(value);
    }

    public HashedPassword hash(HashedPassword.PasswordEncoder encoder) {
        return HashedPassword.of(encoder.encode(this.value));
    }

    // Override toString para seguridad
    @Override
    public String toString() {
        return "PlainPassword{***}";
    }
}

