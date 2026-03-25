package org.school.management.auth.domain.valueobject;

import java.util.regex.Pattern;

public record PlainPassword(String value) {

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 128;
    private static final Pattern HAS_UPPER = Pattern.compile("[A-Z]");
    private static final Pattern HAS_LOWER = Pattern.compile("[a-z]");
    private static final Pattern HAS_DIGIT = Pattern.compile("\\d");
    private static final Pattern HAS_SPECIAL = Pattern.compile("[!@#$%^&*(),.?\":{}|<>]");

    public PlainPassword {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        if (value.length() < MIN_LENGTH) {
            throw new IllegalArgumentException(
                    "Password must be at least " + MIN_LENGTH + " characters long");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "Password cannot exceed " + MAX_LENGTH + " characters");
        }
        if (!HAS_UPPER.matcher(value).find()) {
            throw new IllegalArgumentException(
                    "Password must contain at least one uppercase letter");
        }
        if (!HAS_LOWER.matcher(value).find()) {
            throw new IllegalArgumentException(
                    "Password must contain at least one lowercase letter");
        }
        if (!HAS_DIGIT.matcher(value).find()) {
            throw new IllegalArgumentException(
                    "Password must contain at least one digit");
        }
        if (!HAS_SPECIAL.matcher(value).find()) {
            throw new IllegalArgumentException(
                    "Password must contain at least one special character");
        }
    }

    public static PlainPassword of(String value) {
        return new PlainPassword(value);
    }

    public HashedPassword hash(HashedPassword.PasswordEncoder encoder) {

        return HashedPassword.fromPlain(this.value, encoder);
    }

    // Seguridad — nunca exponer la contraseña en texto plano
    @Override
    public String toString() {
        return "PlainPassword{***}";
    }
}
