package org.school.management.geography.infra.persistence.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "places")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceEntity {
    @Id
    @Column(name = "place_id")
    private UUID placeId;

    @Column(name = "province_id", nullable = false)
    private UUID provinceId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

