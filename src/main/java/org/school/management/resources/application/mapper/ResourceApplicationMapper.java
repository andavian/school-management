package org.school.management.resources.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.school.management.resources.application.dto.request.CreateReservationRequest;
import org.school.management.resources.application.dto.response.ReservationResponse;
import org.school.management.resources.application.dto.response.ResourceResponse;
import org.school.management.resources.application.dto.response.ResourceUnitResponse;
import org.school.management.resources.domain.model.Reservation;
import org.school.management.resources.domain.model.Resource;
import org.school.management.resources.domain.model.ResourceUnit;
import org.school.management.resources.domain.valueobject.ResourceId;
import org.school.management.auth.domain.valueobject.UserId;

import java.util.UUID;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ResourceApplicationMapper {

    // ─── Resource: Domain → Response ─────────────────────────────
    @Mapping(target = "resourceId", expression = "java(resource.getResourceId().value().toString())")
    ResourceResponse toResourceResponse(Resource resource);

    // ─── Reservation: Domain → Response ──────────────────────────
    @Mapping(target = "reservationId", expression = "java(reservation.getReservationId().value().toString())")
    @Mapping(target = "resourceId", expression = "java(reservation.getResourceId().value().toString())")
    @Mapping(target = "requesterId", expression = "java(reservation.getRequesterId().value().toString())")
    @Mapping(target = "resourceCode", ignore = true) // Se inyecta desde el UseCase si es necesario
    ReservationResponse toReservationResponse(Reservation reservation);

    @Mapping(target = "unitId", expression = "java(unit.getUnitId().value())")
    @Mapping(target = "resourceId", expression = "java(unit.getResourceId().value())")
    ResourceUnitResponse toResourceUnitResponse(ResourceUnit unit);

    // ─── Request → Domain helpers (para campos simples) ──────────
    default ResourceId toResourceId(UUID uuid) {
        return uuid != null ? ResourceId.of(uuid) : null;
    }

    default UserId toUserId(UUID uuid) {
        return uuid != null ? UserId.of(uuid) : null;
    }
}