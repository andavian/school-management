package org.school.management.students.parents.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.school.management.shared.infrastructure.persistence.converter.UuidBinaryConverter;
import org.school.management.students.parents.domain.valueobject.ParentRelationship;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "student_parents",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_student_parent",
                        columnNames = {"student_id", "parent_id"}
                )
        }
)
public class StudentParentEntity {

    @Id
    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "student_parent_id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID studentParentId;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "student_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID studentId;

    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "parent_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID parentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "relationship", nullable = false, length = 20)
    private ParentRelationship relationship;

    @Column(name = "is_primary_contact", nullable = false)
    private boolean isPrimaryContact = false;

    @Column(name = "is_authorized_pickup", nullable = false)
    private boolean isAuthorizedPickup = true;

    @Column(name = "is_emergency_contact", nullable = false)
    private boolean isEmergencyContact = true;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onPrePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}