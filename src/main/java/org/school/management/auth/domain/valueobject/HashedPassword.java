package org.school.management.auth.domain.valueobject;

import java.util.Objects;

public record HashedPassword(String value) {

    public HashedPassword {
        // Validación de dominio estricta
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Hashed password cannot be null or empty");
        }
    }

    public static HashedPassword of(String hashedValue) {
        return new HashedPassword(hashedValue);
    }

    /**
     * Factory method que asegura que el resultado del hashing sea válido.
     * Esto previene el error java.lang.IllegalArgumentException en los tests.
     */
    public static HashedPassword fromPlain(String plainValue, PasswordEncoder encoder) {
        Objects.requireNonNull(encoder, "PasswordEncoder is required");
        String hashed = encoder.encode(plainValue);

        if (hashed == null || hashed.isBlank()) {
            throw new IllegalStateException("The password encoder returned an invalid hash");
        }

        return new HashedPassword(hashed);
    }

    public boolean matches(String plainPassword, PasswordEncoder encoder) {
        Objects.requireNonNull(encoder, "PasswordEncoder is required");
        return encoder.matches(plainPassword, this.value); //
    }

    @Override
    public String toString() {
        return "HashedPassword{***}"; //
    }

    public interface PasswordEncoder {
        String encode(String plainPassword);
        boolean matches(String plainPassword, String hashedPassword);
    }
}