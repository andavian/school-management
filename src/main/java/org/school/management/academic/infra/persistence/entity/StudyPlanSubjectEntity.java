package org.school.management.academic.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "study_plan_subjects")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyPlanSubjectEntity {
    @Id
    @Column(name = "study_plan_subject_id", columnDefinition = "BINARY(16)")
    private UUID studyPlanSubjectId;

    @Column(name = "study_plan_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID studyPlanId;

    @Column(name = "subject_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID subjectId;

    @Column(name = "is_mandatory", nullable = false)
    private Boolean isMandatory;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
