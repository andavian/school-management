package org.school.management.shared.person.domain.valueobject;

import org.school.management.shared.geography.domain.valueobject.PlaceId;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Value Object inmutable que representa una dirección postal argentina.
 * Pertenece al Shared Kernel → usado por students, teachers, parents, staff, etc.
 * Encapsula calle, número, piso, depto, localidad (PlaceId) y código postal.
 */
public record Address(
        String street,       // obligatorio
        String number,       // obligatorio
        String floor,        // opcional (piso)
        String apartment,    // opcional (depto)
        PlaceId placeId,     // obligatorio → referencia a geography.places
        String postalCode    // opcional (CP)
) {

    public Address {
        Objects.requireNonNull(street, "La calle es obligatoria");
        Objects.requireNonNull(number, "El número es obligatorio");
        Objects.requireNonNull(placeId, "La localidad es obligatoria");

        street = normalizeStreet(street.trim());
        number = number.trim();

        if (number.isBlank()) {
            throw new IllegalArgumentException("El número de calle no puede estar vacío");
        }
        if (!number.matches("[0-9]+[A-Za-z]?")) {
            throw new IllegalArgumentException(
                    "Número de calle inválido. Ejemplos válidos: 1234, 567B, 1000"
            );
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
        if (str == null || str.isBlank()) {
            throw new IllegalArgumentException("Calle no puede ser vacía");
        }
        return Arrays.stream(str.toLowerCase().split("\\s+"))
                .map(word -> switch (word) {
                    case "av", "avenida" -> "Av.";
                    case "dr"            -> "Dr.";
                    case "gral", "general" -> "Gral.";
                    case "ing"           -> "Ing.";
                    case "pte", "presidente" -> "Pte.";
                    case "san"           -> "San";
                    case "santa"         -> "Santa";
                    default -> Character.toUpperCase(word.charAt(0)) + word.substring(1);
                })
                .collect(Collectors.joining(" "));
    }

    /**
     * Formato oficial para documentos y PDFs.
     * Requiere el nombre de localidad resuelto externamente (no se hace query desde VO).
     * Ejemplo: "Av. Colón 1234, Piso 5, Depto A, Córdoba, CP X5000"
     */
    public String toStringFormatted(String localityName) {
        StringBuilder sb = new StringBuilder();
        sb.append(street).append(" ").append(number);
        if (floor != null)     sb.append(", Piso ").append(floor);
        if (apartment != null) sb.append(", Depto ").append(apartment);
        sb.append(", ").append(localityName);
        if (postalCode != null) sb.append(", CP ").append(postalCode);
        return sb.toString();
    }

    public boolean isComplete() {
        return street != null && number != null && placeId != null;
    }


}