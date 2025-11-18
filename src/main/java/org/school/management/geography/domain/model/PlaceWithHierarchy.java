package org.school.management.geography.domain.model;

import lombok.Builder;
import lombok.Value;

/**
 * Value Object que representa un lugar con su jerarquía completa
 * Útil para respuestas de API y visualización
 */
@Value
@Builder
public class PlaceWithHierarchy {
    Place place;
    Province province;
    Country country;

    /**
     * Obtener dirección completa
     */
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append(place.getNameAsString());
        sb.append(", ");
        sb.append(province.getNameAsString());
        sb.append(", ");
        sb.append(country.getNameAsString());
        return sb.toString();
    }

    /**
     * Obtener descripción completa
     */
    public String getFullDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append(place.getFullDescription());
        sb.append(" - ");
        sb.append(province.getNameAsString());
        if (province.getCodeAsString() != null) {
            sb.append(" (").append(province.getCodeAsString()).append(")");
        }
        sb.append(" - ");
        sb.append(country.getNameAsString());
        return sb.toString();
    }
}
