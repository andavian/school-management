package org.school.management.geography.infra.persistence.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "places",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_place_name_province",
                        columnNames = {"province_id", "name"})
        },
        indexes = {
                @Index(name = "idx_places_province", columnList = "province_id"),
                @Index(name = "idx_places_name", columnList = "name"),
                @Index(name = "idx_places_type", columnList = "type"),
                @Index(name = "idx_places_postal_code", columnList = "postal_code")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceEntity {

    @Id
    @Column(name = "place_id", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID placeId;

    @Column(name = "province_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID provinceId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private PlaceTypeEnum type;

    @Column(name = "postal_code", length = 10)
    private String postalCode;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Relación opcional para queries (no es parte del aggregate)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "province_id", insertable = false, updatable = false)
    private ProvinceEntity province;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlaceEntity that)) return false;
        return placeId != null && placeId.equals(that.placeId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * Enum para JPA (se mapea desde/hacia PlaceType del dominio)
     */
    public enum PlaceTypeEnum {
        CIUDAD, LOCALIDAD, MUNICIPIO, PARAJE, COMUNA
    }
}

