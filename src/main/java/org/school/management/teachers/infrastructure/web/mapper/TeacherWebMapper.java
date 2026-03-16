package org.school.management.teachers.infrastructure.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.school.management.teachers.application.dto.request.CreateTeacherRequest;
import org.school.management.teachers.application.dto.request.UpdateTeacherRequest;
import org.school.management.teachers.application.dto.response.TeacherResponse;
import org.school.management.teachers.application.dto.response.TeacherSummaryResponse;
import org.school.management.teachers.infrastructure.web.dto.TeacherWebDto;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TeacherWebMapper {

    // ── Web Request → Application Request ────────────────────────────────

    @Mapping(source = "address", target = "address", qualifiedByName = "toAppAddressRequest")
    CreateTeacherRequest toApplicationRequest(TeacherWebDto.CreateTeacherWebRequest web);

    @Mapping(source = "address", target = "address", qualifiedByName = "toAppAddressRequest")
    UpdateTeacherRequest toApplicationRequest(TeacherWebDto.UpdateTeacherWebRequest web);

    // ── Application Response → Web Response ──────────────────────────────

    @Mapping(source = "address",      target = "address",      qualifiedByName = "toWebAddressResponse")
    @Mapping(source = "birthPlace",   target = "birthPlace",   qualifiedByName = "toWebPlaceResponse")
    @Mapping(source = "residencePlace", target = "residencePlace", qualifiedByName = "toWebPlaceResponse")
    TeacherWebDto.TeacherWebResponse toWebResponse(TeacherResponse appResponse);

    TeacherWebDto.TeacherSummaryWebResponse toSummaryWebResponse(TeacherSummaryResponse appResponse);

    List<TeacherWebDto.TeacherSummaryWebResponse> toSummaryWebResponseList(
            List<TeacherSummaryResponse> appResponses);

    // ── Named: Address ────────────────────────────────────────────────────

    @Named("toAppAddressRequest")
    default CreateTeacherRequest.AddressRequest toAppAddressRequest(
            TeacherWebDto.AddressWebRequest web) {
        if (web == null) return null;
        return new CreateTeacherRequest.AddressRequest(
                web.street(),
                web.number(),
                web.floor(),
                web.apartment(),
                web.placeId(),
                web.postalCode()
        );
    }

    @Named("toWebAddressResponse")
    default TeacherWebDto.AddressWebResponse toWebAddressResponse(
            TeacherResponse.AddressResponse app) {
        if (app == null) return null;
        return new TeacherWebDto.AddressWebResponse(
                app.street(),
                app.number(),
                app.floor(),
                app.apartment(),
                app.postalCode(),
                app.placeId()
        );
    }

    @Named("toWebPlaceResponse")
    default TeacherWebDto.PlaceWebResponse toWebPlaceResponse(
            TeacherResponse.PlaceResponse app) {
        if (app == null) return null;
        return new TeacherWebDto.PlaceWebResponse(
                app.placeId(),
                app.placeName(),
                app.provinceName(),
                app.countryName()
        );
    }
}