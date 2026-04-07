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
import org.school.management.resources.domain.valueobject.ResourceId;
import org.school.management.auth.domain.valueobject.UserId;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ResourceApplicationMapper {

    // ─── Resource: Domain → Application ────────────────────────────
    @Mapping(target = "resourceId", expression = "java(resource.getResourceId().value())")
    ResourceResponse toResourceResponse(Resource resource);

    // ─── ResourceUnit: Domain → Application ────────────────────────
    @Mapping(target = "unitId", expression = "java(unit.getUnitId().value())")
    @Mapping(target = "resourceId", expression = "java(unit.getResourceId().value())")
    ResourceUnitResponse toResourceUnitResponse(ResourceUnit unit);

    // ─── Reservation: Domain → Application ─────────────────────────────
    @Mapping(target = "reservationId", expression = "java(reservation.getReservationId().value())")
    @Mapping(target = "resourceId", expression = "java(reservation.getResourceId().value())")
    @Mapping(target = "requesterId", expression = "java(reservation.getRequesterId().value())")
    @Mapping(target = "assignedUnits", source = "assignedUnits")
    ReservationResponse toReservationResponse(Reservation reservation);

    // ─── ReservationUnit: Domain → Application ─────────────────────────
    @Mapping(target = "reservationUnitId", expression = "java(unit.getReservationUnitId().value())")
    @Mapping(target = "unitId", expression = "java(unit.getUnitId().value())")
    @Mapping(target = "unitCode", ignore = true) // Se resuelve vía join con ResourceUnit en el UseCase si es necesario
    ReservationUnitResponse toReservationUnitResponse(ReservationUnit unit);

    // ─── Helper para lista ─────────────────────────────────────────────
    default List<ReservationUnitResponse> toReservationUnitResponseList(List<ReservationUnit> units) {
        if (units == null) return java.util.Collections.emptyList();
        return units.stream().map(this::toReservationUnitResponse).collect(java.util.stream.Collectors.toList());
    }

    // ─── Helpers para IDs ──────────────────────────────────────────
    default ResourceId toResourceId(UUID uuid) {
        return uuid != null ? ResourceId.of(uuid) : null;
    }

    default UserId toUserId(UUID uuid) {
        return uuid != null ? UserId.of(uuid) : null;
    }
}