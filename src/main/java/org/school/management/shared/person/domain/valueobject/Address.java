// src/main/java/org/school/management/shared/person/domain/valueobject/Address.java
package org.school.management.shared.person.domain.valueobject;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Value Object inmutable que representa una dirección postal argentina.
 * Pertenece al Shared Kernel → usado por students, teachers, parents, staff, etc.
 */
public record Address(
        String street,      // obligatorio
        String number,      // obligatorio
        String floor,       // opcional (piso)
        String apartment,   // opcional (depto)
        ResidencePlaceId residencePlaceId,    // obligatorio → referencia a geography.places
        String postalCode   // opcional (CP)
) {

    public Address {
        Objects.requireNonNull(street, "La calle es obligatoria");
        Objects.requireNonNull(number, "El número es obligatorio");
        Objects.requireNonNull(residencePlaceId, "La localidad es obligatoria");

        // Normalización estricta (formato oficial argentino)
        street = normalizeStreet(street.trim());
        number = number.trim();

        if (number.isBlank()) {
            throw new IllegalArgumentException("El número de calle no puede estar vacío");
        }
        if (!number.matches("[0-9]+[A-Za-z]?")) {
            throw new IllegalArgumentException("Número de calle inválido. Ejemplos válidos: 1234, 567B, 1000");
        }

        if (floor != null) {
            floor = floor.trim().toUpperCase();
            if (floor.isBlank()) floor = null;
        }
        if (apartment != null) {
            apartment = apartment.trim().toUpperCase();
            if (apartment.isBlank()) apartment = null;
        }
        if (postalCode != null) {
            postalCode = postalCode.trim();
            if (postalCode.isBlank()) postalCode = null;
        }
    }

    private static String normalizeStreet(String str) {
        if (str == null || str.isBlank()) throw new IllegalArgumentException("Calle no puede ser vacía");

        return Arrays.stream(str.toLowerCase().split("\\s+"))
                .map(word -> {
                    // Palabras que siempre van en mayúsculas: Av., Dr., Gral., etc.
                    return switch (word) {
                        case "av", "avenida" -> "Av.";
                        case "dr" -> "Dr.";
                        case "gral", "general" -> "Gral.";
                        case "ing" -> "Ing.";
                        case "pte", "presidente" -> "Pte.";
                        case "san" -> "San";
                        case "santa" -> "Santa";
                        default -> Character.toUpperCase(word.charAt(0)) + word.substring(1);
                    };
                })
                .collect(Collectors.joining(" "));
    }

    /**
     * Formato oficial para documentos, planillas y PDFs
     * Ejemplo: "Av. Colón 1234, Piso 5, Depto A, Córdoba, CP X5000"
     */
    public String toStringFormatted(String localityName) {
        StringBuilder sb = new StringBuilder();
        sb.append(street).append(" ").append(number);

        if (floor != null) sb.append(", Piso ").append(floor);
        if (apartment != null) sb.append(", Depto ").append(apartment);

        sb.append(", ").append(localityName);

        if (postalCode != null) {
            sb.append(", CP ").append(postalCode);
        }

        return sb.toString();
    }

    /**
     * Útil para validar formularios progresivos
     */
    public boolean isComplete() {
        return street != null && number != null && residencePlaceId != null;
    }

    /**
     * Comparación semántica: dos direcciones son iguales si tienen misma calle, número y localidad
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address that)) return false;
        return street.equals(that.street) &&
                number.equals(that.number) &&
                residencePlaceId.equals(that.residencePlaceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, number, residencePlaceId);
    }
}