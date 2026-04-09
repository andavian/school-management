// src/main/java/org/school/management/resources/application/mapper/ResourceApplicationMapper.java
package org.school.management.resources.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.school.management.resources.application.dto.response.ReservationResponse;
import org.school.management.resources.application.dto.response.ReservationUnitResponse;
import org.school.management.resources.application.dto.response.ResourceResponse;
import org.school.management.resources.application.dto.response.ResourceUnitResponse;
import org.school.management.resources.domain.model.Reservation;
import org.school.management.resources.domain.model.ReservationUnit;
import org.school.management.resources.domain.model.Resource;
import org.school.management.resources.domain.model.ResourceUnit;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ResourceApplicationMapper {

    @Mapping(target = "resourceId", expression = "java(resource.getResourceId().value())")
    ResourceResponse toResourceResponse(Resource resource);

    @Mapping(target = "unitId", expression = "java(unit.getUnitId().value())")
    @Mapping(target = "resourceId", expression = "java(unit.getResourceId().value())")
    ResourceUnitResponse toResourceUnitResponse(ResourceUnit unit);

    @Mapping(target = "reservationId", expression = "java(reservation.getReservationId().value())")
    @Mapping(target = "resourceId", expression = "java(reservation.getResourceId().value())")
    @Mapping(target = "requesterId", expression = "java(reservation.getRequesterId())")
    @Mapping(target = "cancelledBy", expression = "java(reservation.getCancelledBy())")
    @Mapping(target = "assignedUnits", source = "assignedUnits")
    ReservationResponse toReservationResponse(Reservation reservation);

    @Mapping(target = "reservationUnitId", expression = "java(unit.getReservationUnitId().value())")
    @Mapping(target = "unitId", expression = "java(unit.getUnitId().value())")
    @Mapping(target = "unitCode", ignore = true)
    ReservationUnitResponse toReservationUnitResponse(ReservationUnit unit);

    default List<ReservationUnitResponse> toReservationUnitResponseList(List<ReservationUnit> units) {
        if (units == null) return List.of();
        return units.stream().map(this::toReservationUnitResponse).toList();
    }
}