package org.school.management.grades.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.school.management.shared.infrastructure.persistence.converter.UuidBinaryConverter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "evaluation_types")
public class EvaluationTypeEntity {

    @Id
    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "evaluation_type_id", columnDefinition = "BINARY(16)",
            updatable = false, nullable = false)
    private UUID evaluationTypeId;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "weight_percentage", precision = 5, scale = 2)
    private BigDecimal weightPercentage;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onPrePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
