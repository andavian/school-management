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
@Table(name = "provinces",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_province_name_country",
                        columnNames = {"country_id", "name"})
        },
        indexes = {
                @Index(name = "idx_provinces_country", columnList = "country_id"),
                @Index(name = "idx_provinces_name", columnList = "name"),
                @Index(name = "idx_provinces_code", columnList = "code")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProvinceEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "province_id", updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID provinceId;

    @Column(name = "country_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID countryId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "code", length = 10)
    private String code;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Relación opcional para queries (no es parte del aggregate)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", insertable = false, updatable = false)
    private CountryEntity country;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProvinceEntity that)) return false;
        return provinceId != null && provinceId.equals(that.provinceId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
