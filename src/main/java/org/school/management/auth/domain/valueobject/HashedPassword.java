package org.school.management.auth.domain.valueobject;

public record HashedPassword(String value) {

    public HashedPassword {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Hashed password cannot be null or empty");
        }
    }

    public static HashedPassword of(String hashedValue) {
        return new HashedPassword(hashedValue);
    }

    public boolean matches(String plainPassword, PasswordEncoder encoder) {
        return encoder.matches(plainPassword, this.value);
    }

    // Seguridad — nunca exponer el hash
    @Override
    public String toString() {
        return "HashedPassword{***}";
    }

    public interface PasswordEncoder {
        String encode(String plainPassword);

        boolean matches(String plainPassword, String hashedPassword);
    }
}
