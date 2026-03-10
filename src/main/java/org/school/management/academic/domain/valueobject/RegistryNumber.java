package org.school.management.academic.domain.valueobject;

public record RegistryNumber(String value) {

    public static final int MAX_SEQUENCE_ALLOWED = 999;

    public RegistryNumber {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Registry number cannot be null or empty");
        }
        if (!value.matches("^REG-\\d{4}-\\d{3}$")) {
            throw new IllegalArgumentException(
                    "Invalid registry number format. Expected: REG-YYYY-NNN");
        }
    }

    public static RegistryNumber of(String value) {
        return new RegistryNumber(value);
    }

    public static RegistryNumber generate(int year, int sequence) {
        return new RegistryNumber(String.format("REG-%d-%03d", year, sequence));
    }

    public int extractYear() {
        return Integer.parseInt(value.substring(4, 8));
    }

    public int extractSequence() {
        return Integer.parseInt(value.substring(9));
    }
}
