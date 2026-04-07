package org.school.management.resources.infrastructure.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.school.management.resources.application.dto.response.ReservationResponse;
import org.school.management.resources.application.dto.response.ResourceUnitResponse;
import org.school.management.resources.domain.model.Resource;
import org.school.management.resources.domain.model.ResourceUnit;
import org.school.management.resources.infrastructure.web.dto.ResourceWebDto;
import org.school.management.resources.infrastructure.web.dto.ReservationWebDto;

import java.util.UUID;

/**
 * Mapper entre Application DTOs y Web DTOs.
 * Sigue la regla: Application ↔ Web (nunca Domain ↔ Web directo).
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ResourcesWebMapper {

    @Mapping(target = "resourceId", expression = "java(resource.getResourceId().value())")
    ResourceWebDto.ResourceResponse toResourceResponse(Resource resource);

    @Mapping(target = "unitId", expression = "java(unit.getUnitId().value())")
    ResourceWebDto.ResourceUnitResponse toResourceUnitResponse(ResourceUnit unit);

    ResourceWebDto.ResourceUnitWebResponse toResourceUnitWebResponse(ResourceUnitResponse response);

    @Mapping(target = "assignedUnits", ignore = true) // Se puede expandir si el frontend requiere detalle físico
    ReservationWebDto.ReservationWebResponse toReservationWebResponse(ReservationResponse response);
}