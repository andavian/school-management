package org.school.management.shared.person.domain.valueobject;


public record DNI(String value) {


    public DNI(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("DNI cannot be null or empty");
        }

        String cleanValue = value.trim().replaceAll("[^0-9]", ""); // Solo números

        if (!isValidArgentinianDNI(cleanValue)) {
            throw new IllegalArgumentException("DNI must be between 7 and 8 digits: " + value);
        }

        this.value = cleanValue;
    }

    public static DNI of(String value) {
        return new DNI(value);
    }

    // Factory methods comunes
    public static DNI of(long dniNumber) {
        return new DNI(String.valueOf(dniNumber));
    }

    private boolean isValidArgentinianDNI(String dni) {
        return dni.matches("^\\d{7,8}$");
    }

    // Métodos de utilidad
    public String getFormatted() {
        // Formatear como XX.XXX.XXX
        if (value.length() == 7) {
            return value.charAt(0) + "." + value.substring(1, 4) + "." + value.substring(4);
        } else if (value.length() == 8) {
            return value.substring(0, 2) + "." + value.substring(2, 5) + "." + value.substring(5);
        }
        return value;
    }

    public boolean isValid() {
        return value != null && value.matches("^\\d{7,8}$");
    }


    @Override
    public String toString() {
        return "DNI{" + getFormatted() + "}";
    }
}
