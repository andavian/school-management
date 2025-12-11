package org.school.management.students.domain.model;


import lombok.Builder;
import lombok.Value;
import org.school.management.parents.domain.valueobject.StudentId;

import java.time.LocalDateTime;

@Value
@Builder
public class StudentParent {
    StudentParentId id;
    StudentId studentId;
    ParentId parentId;

    Relationship relationship;
    boolean isPrimaryContact;
    boolean isAuthorizedPickup;
    boolean isEmergencyContact;
    String notes;

    LocalDateTime createdAt;

    public static StudentParent link(
            StudentId studentId,
            ParentId parentId,
            Relationship relationship,
            boolean isPrimary
    ) {
        return StudentParent.builder()
                .id(StudentParentId.generate())
                .studentId(studentId)
                .parentId(parentId)
                .relationship(relationship)
                .isPrimaryContact(isPrimary)
                .isAuthorizedPickup(true)
                .isEmergencyContact(true)
                .createdAt(LocalDateTime.now())
                .build();
    }
}