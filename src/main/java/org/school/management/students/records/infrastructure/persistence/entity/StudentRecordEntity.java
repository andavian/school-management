package org.school.management.students.records.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.school.management.shared.infrastructure.persistence.converter.UuidBinaryConverter;
import org.school.management.students.records.domain.valueobject.RecordStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "student_records",
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_student_record",  columnNames = {"student_id"}),
                @UniqueConstraint(name = "unique_record_number",   columnNames = {"record_number"})
        }
)
public class StudentRecordEntity {

    @Id
    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "record_id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID recordId;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "student_id", columnDefinition = "BINARY(16)", nullable = false, unique = true)
    private UUID studentId;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "academic_year_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID academicYearId;

    // Número de legajo = DNI del estudiante
    @Column(name = "record_number", nullable = false, length = 8, unique = true)
    private String recordNumber;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "registry_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID registryId;

    @Column(name = "folio_number", nullable = false)
    private Integer folioNumber;

    // Estado
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RecordStatus status;

    @Column(name = "completeness_percentage", precision = 5, scale = 2)
    private BigDecimal completenessPercentage;

    // Revisión administrativa
    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "reviewed_by", columnDefinition = "BINARY(16)")
    private UUID reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "review_observations", columnDefinition = "TEXT")
    private String reviewObservations;

    // Documentos — relación OneToMany con cascade
    @OneToMany(
            mappedBy = "recordId",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<RecordDocumentEntity> documents = new ArrayList<>();

    // Auditoría
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onPrePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
    }

    @PreUpdate
    protected void onPreUpdate() {
        updatedAt = LocalDateTime.now();
    }
}