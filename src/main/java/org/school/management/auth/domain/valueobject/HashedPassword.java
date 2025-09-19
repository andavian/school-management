package org.school.management.auth.domain.valueobject;

import lombok.*;

@Value                                     // Inmutable
public class HashedPassword {
    String value;

    private HashedPassword(String hashedValue) {
        if (hashedValue == null || hashedValue.trim().isEmpty()) {
            throw new IllegalArgumentException("Hashed password cannot be null or empty");
        }
        this.value = hashedValue;
    }

    public static HashedPassword of(String hashedValue) {
        return new HashedPassword(hashedValue);
    }

    public boolean matches(String plainPassword, PasswordEncoder encoder) {
        return encoder.matches(plainPassword, this.value);
    }

    // Override toString para seguridad
    @Override
    public String toString() {
        return "HashedPassword{***}";
    }

    public interface PasswordEncoder {
        String encode(String plainPassword);
        boolean matches(String plainPassword, String hashedPassword);
    }
}
