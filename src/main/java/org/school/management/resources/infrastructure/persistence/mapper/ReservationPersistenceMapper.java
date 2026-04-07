package org.school.management.resources.infrastructure.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.school.management.resources.domain.model.Reservation;
import org.school.management.resources.domain.model.ReservationUnit;
import org.school.management.resources.domain.valueobject.ReservationId;
import org.school.management.resources.domain.valueobject.ReservationUnitId;
import org.school.management.resources.domain.valueobject.ResourceId;
import org.school.management.resources.domain.valueobject.UnitId;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.resources.infrastructure.persistence.entity.ReservationEntity;
import org.school.management.resources.infrastructure.persistence.entity.ReservationUnitEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ReservationPersistenceMapper {

    // ─── Reservation Mapping ───────────────────────────────────────────────
    default ReservationEntity toEntity(Reservation domain) {
        if (domain == null) return null;
        ReservationEntity entity = new ReservationEntity();
        entity.setReservationId(domain.getReservationId().value());
        entity.setResourceId(domain.getResourceId().value());
        entity.setRequesterId(domain.getRequesterId().value());
        entity.setRequesterName(domain.getRequesterName());
        entity.setReservationDate(domain.getReservationDate());
        entity.setStartTime(domain.getStartTime());
        entity.setEndTime(domain.getEndTime());
        entity.setQuantityRequested(domain.getQuantityRequested());
        entity.setPurpose(domain.getPurpose());
        entity.setGradeLevelInfo(domain.getGradeLevelInfo());
        entity.setStatus(domain.getStatus());
        entity.setCancellationReason(domain.getCancellationReason());
        entity.setCancelledBy(domain.getCancelledBy() != null ? domain.getCancelledBy().value() : null);
        entity.setReturnObservations(domain.getReturnObservations());
        entity.setReturnedAt(domain.getReturnedAt());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    default Reservation toDomain(ReservationEntity entity) {
        if (entity == null) return null;
        return Reservation.builder()
                .reservationId(ReservationId.of(entity.getReservationId()))
                .resourceId(ResourceId.of(entity.getResourceId()))
                .requesterId(UserId.of(entity.getRequesterId()))
                .requesterName(entity.getRequesterName())
                .reservationDate(entity.getReservationDate())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .quantityRequested(entity.getQuantityRequested())
                .purpose(entity.getPurpose())
                .gradeLevelInfo(entity.getGradeLevelInfo())
                .status(entity.getStatus())
                .cancellationReason(entity.getCancellationReason())
                .cancelledBy(entity.getCancelledBy() != null ? UserId.of(entity.getCancelledBy()) : null)
                .returnObservations(entity.getReturnObservations())
                .returnedAt(entity.getReturnedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    // ─── ReservationUnit Mapping ───────────────────────────────────────────
    default List<ReservationUnitEntity> toUnitEntityList(List<ReservationUnit> units) {
        if (units == null) return new ArrayList<>();
        return units.stream().map(this::toUnitEntity).collect(Collectors.toList());
    }

    default ReservationUnitEntity toUnitEntity(ReservationUnit unit) {
        if (unit == null) return null;
        ReservationUnitEntity entity = new ReservationUnitEntity();
        entity.setReservationUnitId(unit.getReservationUnitId().value());
        entity.setReservationId(unit.getReservationId().value());
        entity.setUnitId(unit.getUnitId().value());
        entity.setCreatedAt(unit.getCreatedAt());
        return entity;
    }

    // ─── List Mapping Helper ───────────────────────────────────────────────
    default List<Reservation> toDomainList(List<ReservationEntity> entities) {
        if (entities == null) return new ArrayList<>();
        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }
}