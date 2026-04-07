package org.school.management.resources.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.school.management.resources.domain.model.Reservation;
import org.school.management.resources.domain.model.ReservationUnit;
import org.school.management.resources.domain.repository.ReservationRepository;
import org.school.management.resources.domain.valueobject.*;
import org.school.management.resources.infrastructure.persistence.entity.ReservationEntity;
import org.school.management.resources.infrastructure.persistence.entity.ReservationUnitEntity;
import org.school.management.resources.infrastructure.persistence.mapper.ReservationPersistenceMapper;
import org.school.management.resources.infrastructure.persistence.repository.ReservationJpaRepository;
import org.school.management.resources.infrastructure.persistence.repository.ReservationUnitJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReservationRepositoryAdapter implements ReservationRepository {

    private final ReservationJpaRepository reservationJpa;
    private final ReservationUnitJpaRepository reservationUnitJpa;
    private final ReservationPersistenceMapper mapper;

    @Override
    public Optional<Reservation> findByReservationId(ReservationId id) {
        return reservationJpa.findById(id.value()).map(entity -> {
            // 1. Reconstruir la cabecera
            Reservation reservation = mapper.toDomain(entity);

            // 2. Reconstruir las unidades asignadas (relación 1:N manual)
            List<ReservationUnitEntity> unitEntities = reservationUnitJpa.findByReservationId(id.value());
            unitEntities.forEach(e -> {
                ReservationUnit unit = ReservationUnit.builder()
                        .reservationUnitId(ReservationUnitId.of(e.getReservationUnitId()))
                        .reservationId(reservation.getReservationId())
                        .unitId(UnitId.of(e.getUnitId()))
                        .createdAt(e.getCreatedAt())
                        .build();
                reservation.getAssignedUnits().add(unit);
            });
            return reservation;
        });
    }

    @Override
    public List<Reservation> findByResourceIdAndDate(ResourceId resourceId, LocalDate date) {
        return reservationJpa.findByResourceIdAndReservationDate(resourceId.value(), date).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findByRequesterId(UUID requesterId) {
        return reservationJpa.findByRequesterId(requesterId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findAllActive() {
        // Implementación básica; para producción masiva agregar método findByStatusIn en JPA
        return reservationJpa.findAll().stream()
                .filter(e -> e.getStatus() == ReservationStatus.CONFIRMED || e.getStatus() == ReservationStatus.IN_USE)
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Set<UnitId> findReservedUnitIdsForDateRange(ResourceId resourceId, LocalDate date, LocalTime start, LocalTime end) {
        return reservationJpa.findReservedUnitIdsForDateRange(
                resourceId.value(), date, start, end,
                List.of(ReservationStatus.CONFIRMED, ReservationStatus.IN_USE)
        ).stream().map(UnitId::from).collect(Collectors.toSet());
    }

    @Override
    public Reservation save(Reservation reservation) {
        // 1. Guardar cabecera
        ReservationEntity entity = mapper.toEntity(reservation);
        ReservationEntity savedEntity = reservationJpa.save(entity);

        // 2. Guardar enlaces de unidades físicas (ReservationUnit)
        List<ReservationUnit> units = reservation.getAssignedUnits();
        if (units != null && !units.isEmpty()) {
            List<ReservationUnitEntity> unitEntities = units.stream().map(u -> {
                ReservationUnitEntity ue = new ReservationUnitEntity();
                ue.setReservationUnitId(u.getReservationUnitId().value());
                ue.setReservationId(savedEntity.getReservationId());
                ue.setUnitId(u.getUnitId().value());
                ue.setCreatedAt(u.getCreatedAt());
                return ue;
            }).collect(Collectors.toList());
            reservationUnitJpa.saveAll(unitEntities);
        }

        return mapper.toDomain(savedEntity);
    }
}