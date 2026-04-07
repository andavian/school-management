package org.school.management.resources.infrastructure.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.school.management.resources.application.dto.response.ReservationResponse;
import org.school.management.resources.application.dto.response.ResourceResponse;
import org.school.management.resources.application.dto.response.ResourceUnitResponse;
import org.school.management.resources.infrastructure.web.dto.ResourceWebDto;
import org.school.management.resources.infrastructure.web.dto.ReservationWebDto;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ResourcesWebMapper {

    // ─── Resource: Application → Web ───────────────────────────────
    // Mapea el DTO de aplicación al contenedor Web
    @Mapping(target = "units", ignore = true) // Se llena vía UseCase si se requiere lista de unidades
    ResourceWebDto.ResourceWebResponse toResourceWebResponse(ResourceResponse response);

    // ─── ResourceUnit: Application → Web ───────────────────────────
    ResourceWebDto.ResourceUnitWebResponse toResourceUnitWebResponse(ResourceUnitResponse response);

    // ─── Reservation: Application → Web ────────────────────────────
    @Mapping(target = "assignedUnits", ignore = true)
    ReservationWebDto.ReservationWebResponse toReservationWebResponse(ReservationResponse response);
}