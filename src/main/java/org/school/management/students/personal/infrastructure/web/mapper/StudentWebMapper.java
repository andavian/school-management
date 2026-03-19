package org.school.management.students.personal.infrastructure.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.school.management.students.personal.application.dto.request.CreateStudentRequest;
import org.school.management.students.personal.application.dto.request.UpdateStudentRequest;
import org.school.management.students.personal.application.dto.response.StudentResponse;
import org.school.management.students.personal.application.dto.response.StudentSummaryResponse;
import org.school.management.students.personal.infrastructure.web.dto.StudentWebDto;
import org.school.management.students.personal.infrastructure.web.dto.StudentWebDto.*;

import java.util.List;

/**
 * WebMapper MapStruct: web DTOs ↔ application DTOs.
 *
 * Responsabilidades:
 * — CreateStudentWebRequest   → CreateStudentRequest   (entrada al use case)
 * — UpdateStudentWebRequest   → UpdateStudentRequest   (entrada al use case)
 * — StudentResponse           → StudentWebResponse     (salida al cliente)
 * — StudentSummaryResponse    → StudentSummaryWebResponse
 *
 * NO accede al dominio directamente — solo convierte entre capas adyacentes.
 * Tercera capa de mappers del proyecto: persistence ← application ← web.
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface StudentWebMapper {

    // ── Web Request → Application Request ────────────────────────────────

    /**
     * Convierte el body web al DTO de application para CreateStudentUseCase.
     * Los nested records se mapean automáticamente por nombre de campo.
     */
    @Mapping(target = "healthData", source = "healthData")
    @Mapping(target = "parent",     source = "parent")
    CreateStudentRequest toApplicationRequest(CreateStudentWebRequest webRequest);


    CreateStudentRequest.HealthDataRequest toHealthDataRequest(
            CreateStudentWebRequest.HealthDataWebRequest webRequest);

    @Mapping(target = "isPrimaryContact",   source = "isPrimaryContact")
    @Mapping(target = "isAuthorizedPickup", source = "isAuthorizedPickup")
    CreateStudentRequest.ParentRequest toParentRequest(
            CreateStudentWebRequest.ParentWebRequest webRequest);

    UpdateStudentRequest toApplicationRequest(UpdateStudentWebRequest webRequest);

    // ── Application Response → Web Response ──────────────────────────────

    @Mapping(target = "address",       source = "address")
    @Mapping(target = "birthPlace",    source = "birthPlace")
    @Mapping(target = "residencePlace",source = "residencePlace")
    StudentWebResponse toWebResponse(StudentResponse appResponse);

    @Mapping(target = "street",          source = "street")
    @Mapping(target = "number",          source = "number")
    @Mapping(target = "floor",           source = "floor")
    @Mapping(target = "apartment",       source = "apartment")
    @Mapping(target = "postalCode",      source = "postalCode")
    @Mapping(target = "residencePlaceId",source = "residencePlaceId")
    @Mapping(target = "formatted",       source = "formatted")
    StudentWebResponse.AddressWebResponse toAddressWebResponse(StudentResponse.AddressResponse appAddress);

    @Mapping(target = "placeId",     source = "placeId")
    @Mapping(target = "placeName",   source = "placeName")
    @Mapping(target = "provinceName",source = "provinceName")
    @Mapping(target = "countryName", source = "countryName")
    StudentWebResponse.PlaceWebResponse toPlaceWebResponse(StudentResponse.PlaceResponse appPlace);

    StudentSummaryWebResponse toSummaryWebResponse(StudentSummaryResponse appSummary);

    List<StudentSummaryWebResponse> toSummaryWebResponseList(List<StudentSummaryResponse> appSummaries);
}