package org.school.management.students.records.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.school.management.shared.infrastructure.persistence.converter.UuidBinaryConverter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "document_types")
public class DocumentTypeEntity {

    @Id
    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "document_type_id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID documentTypeId;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_mandatory", nullable = false)
    private boolean mandatory;

    @Column(name = "category", nullable = false, length = 50)
    private String category;

    @Column(name = "valid_for_years")
    private Integer validForYears;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onPrePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}