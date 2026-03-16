package org.school.management.students.parents.infrastructure.web.mapper;

import org.mapstruct.Mapper;
import org.school.management.students.parents.application.dto.request.CreateParentRequest;
import org.school.management.students.parents.application.dto.request.LinkParentRequest;
import org.school.management.students.parents.application.dto.request.UpdateParentRequest;
import org.school.management.students.parents.application.dto.response.ParentResponse;
import org.school.management.students.parents.application.dto.response.StudentParentResponse;
import org.school.management.students.parents.infrastructure.web.dto.ParentWebDto;

/**
 * Mapper de capa Web: web DTOs ↔ application DTOs.
 * Tercera capa de mappers — nunca saltear a domain directamente.
 */
@Mapper(componentModel = "spring")
public interface ParentWebMapper {

    // ── web request → application request ────────────────────────────────

    CreateParentRequest toApplicationRequest(
            ParentWebDto.CreateParentWebRequest webRequest
    );

    UpdateParentRequest toApplicationRequest(
            ParentWebDto.UpdateParentWebRequest webRequest
    );

    LinkParentRequest toApplicationRequest(
            ParentWebDto.LinkParentWebRequest webRequest
    );

    // ── application response → web response ──────────────────────────────

    default ParentWebDto.ParentWebResponse toWebResponse(ParentResponse response) {
        if (response == null) return null;

        return new ParentWebDto.ParentWebResponse(
                response.parentId(),
                response.userId(),
                response.dni(),
                response.cuil(),            // ← NUEVO
                response.firstName(),
                response.lastName(),
                response.fullName(),
                response.birthDate(),
                response.gender(),
                response.nationality(),
                response.email(),
                response.phone(),
                response.phoneAlt(),
                response.addressStreet(),
                response.addressNumber(),
                response.addressFloor(),
                response.addressApartment(),
                response.placeId(),         // ← fix: era response.residencePlaceId()
                response.postalCode(),
                response.occupation(),
                response.workplace(),
                response.workplacePhone(),
                response.active(),
                response.createdAt(),
                response.updatedAt()
        );
    }

    default ParentWebDto.StudentParentWebResponse toStudentParentWebResponse(
            StudentParentResponse response) {
        if (response == null) return null;

        return new ParentWebDto.StudentParentWebResponse(
                response.studentParentId(),
                response.studentId(),
                response.parentId(),
                response.relationship().name(),
                response.relationshipDisplay(),
                response.primaryContact(),
                response.authorizedPickup(),
                response.emergencyContact(),
                response.notes(),
                toWebResponse(response.parent()),
                response.createdAt()
        );
    }

    default ParentWebDto.ParentSummaryWebResponse toSummaryWebResponse(
            ParentResponse response) {
        if (response == null) return null;

        return new ParentWebDto.ParentSummaryWebResponse(
                response.parentId(),
                response.dni(),
                response.fullName(),
                response.email(),
                response.phone(),
                response.active()
        );
    }
}