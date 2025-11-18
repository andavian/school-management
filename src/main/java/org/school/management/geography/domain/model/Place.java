package org.school.management.geography.domain.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import org.school.management.geography.domain.valueobject.*;

import java.time.LocalDateTime;

@Value
@Builder(toBuilder = true)
public class Place {
    PlaceId placeId;
    ProvinceId provinceId;
    GeographicName name;
    @With
    PlaceType type;
    @With
    PostalCode postalCode;
    LocalDateTime createdAt;

    /**
     * Factory method para crear un nuevo lugar
     */
    public static Place create(
            String name,
            PlaceType type,
            ProvinceId provinceId
    ) {
        if (provinceId == null) {
            throw new IllegalArgumentException("Province is required for place");
        }
        if (type == null) {
            throw new IllegalArgumentException("Place type is required");
        }

        return Place.builder()
                .placeId(PlaceId.generate())
                .provinceId(provinceId)
                .name(GeographicName.of(name))
                .type(type)
                .postalCode(null)
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * Factory method con código postal
     */
    public static Place create(
            String name,
            PlaceType type,
            ProvinceId provinceId,
            String postalCode
    ) {
        Place place = create(name, type, provinceId);
        return place.withPostalCode(
                postalCode != null ? PostalCode.of(postalCode) : null
        );
    }

    /**
     * Actualizar tipo de lugar
     */
    public Place updateType(PlaceType newType) {
        if (newType == null) {
            throw new IllegalArgumentException("Place type cannot be null");
        }
        return this.withType(newType);
    }

    /**
     * Actualizar código postal
     */
    public Place updatePostalCode(String newPostalCode) {
        return this.withPostalCode(
                newPostalCode != null ? PostalCode.of(newPostalCode) : null
        );
    }

    /**
     * Verificar si pertenece a una provincia específica
     */
    public boolean belongsToProvince(ProvinceId provinceId) {
        return this.provinceId.equals(provinceId);
    }

    /**
     * Obtener nombre como String
     */
    public String getNameAsString() {
        return name.getValue();
    }

    /**
     * Obtener tipo como String
     */
    public String getTypeAsString() {
        return type.name();
    }

    /**
     * Obtener nombre del tipo como String
     */
    public String getTypeDisplayName() {
        return type.getDisplayName();
    }

    /**
     * Obtener código postal como String
     */
    public String getPostalCodeAsString() {
        return postalCode != null ? postalCode.getValue() : null;
    }

    /**
     * Obtener descripción completa del lugar
     */
    public String getFullDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append(name.getValue());
        sb.append(" (").append(type.getDisplayName()).append(")");
        if (postalCode != null) {
            sb.append(" - CP: ").append(postalCode.getValue());
        }
        return sb.toString();
    }
}
