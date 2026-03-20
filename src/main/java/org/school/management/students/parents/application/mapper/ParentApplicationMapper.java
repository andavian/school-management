package org.school.management.students.parents.application.mapper;

import org.mapstruct.Mapper;
import org.school.management.students.parents.application.dto.response.ParentResponse;
import org.school.management.students.parents.application.dto.response.StudentParentResponse;
import org.school.management.students.parents.domain.model.Parent;
import org.school.management.students.parents.domain.model.StudentParent;

/**
 * Mapper de capa Application: domain → response DTO.
 * Usa métodos default por la complejidad de los VOs.
 */
@Mapper(componentModel = "spring")
public interface ParentApplicationMapper {

    default ParentResponse toParentResponse(Parent parent) {
        if (parent == null) return null;

        return new ParentResponse(
                parent.getParentId().value(),
                parent.getUserId().value(),
                parent.getDni().value(),
                parent.getCuil().formatted(),
                parent.getFullName().firstName(),
                parent.getFullName().lastName(),
                parent.getFullName().firstNameFirst(),
                parent.getBirthDate(),
                parent.getGender() != null ? parent.getGender().name() : null,
                parent.getNationality() != null ? parent.getNationality().value() : null,
                parent.getEmail().value(),
                parent.getPhone().value(),
                parent.getPhoneAlt() != null ? parent.getPhoneAlt().value() : null,
                parent.getAddress() != null ? parent.getAddress().street() : null,
                parent.getAddress() != null ? parent.getAddress().number() : null,
                parent.getAddress() != null ? parent.getAddress().floor() : null,
                parent.getAddress() != null ? parent.getAddress().apartment() : null,
                parent.getAddress() != null ? parent.getAddress().placeId().value() : null,
                parent.getAddress() != null ? parent.getAddress().postalCode() : null,
                parent.getOccupation(),
                parent.getWorkplace(),
                parent.getWorkplacePhone() != null ? parent.getWorkplacePhone().value() : null,
                parent.isActive(),
                parent.getCreatedAt(),
                parent.getUpdatedAt()
        );
    }

    default StudentParentResponse toStudentParentResponse(
            StudentParent studentParent,
            Parent parent) {
        if (studentParent == null) return null;

        return new StudentParentResponse(
                studentParent.getStudentParentId().value(),
                studentParent.getStudentId().value(),
                studentParent.getParentId().value(),
                studentParent.getRelationship(),
                studentParent.getRelationship().getDisplayName(),
                studentParent.isPrimaryContact(),
                studentParent.isAuthorizedPickup(),
                studentParent.isEmergencyContact(),
                studentParent.getNotes(),
                toParentResponse(parent),
                studentParent.getCreatedAt()
        );
    }
}