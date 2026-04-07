package org.school.management.resources.infrastructure.persistence.repository;

import org.school.management.resources.infrastructure.persistence.entity.ReservationUnitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationUnitJpaRepository extends JpaRepository<ReservationUnitEntity, UUID> {

    List<ReservationUnitEntity> findByReservationId(UUID reservationId);

    // Útil para limpieza o auditoría, aunque no requerido para el flujo principal
    List<ReservationUnitEntity> findByUnitId(UUID unitId);
}