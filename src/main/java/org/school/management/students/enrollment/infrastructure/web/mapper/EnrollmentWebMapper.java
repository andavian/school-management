package org.school.management.students.enrollment.infrastructure.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.school.management.students.enrollment.application.dto.request.UpdateEnrollmentRequest;
import org.school.management.students.enrollment.application.dto.response.EnrollmentResponse;
import org.school.management.students.enrollment.infrastructure.web.dto.EnrollmentWebDto;

/**
 * Mapper de capa Web: web DTOs ↔ application DTOs.
 * Tercera capa de mappers — nunca saltear a domain directamente.
 */
@Mapper(componentModel = "spring")
public interface EnrollmentWebMapper {

    // web request → application request
    UpdateEnrollmentRequest toApplicationRequest(
            EnrollmentWebDto.UpdateEnrollmentWebRequest webRequest
    );

    // application response → web response
    @Mapping(target = "enrollmentType", expression = "java(response.enrollmentType().name())")
    @Mapping(target = "status",         expression = "java(response.status().name())")
    EnrollmentWebDto.EnrollmentWebResponse toWebResponse(EnrollmentResponse response);

    // application response → summary web response
    @Mapping(target = "status", expression = "java(response.status().name())")
    EnrollmentWebDto.EnrollmentSummaryWebResponse toSummaryWebResponse(EnrollmentResponse response);
}