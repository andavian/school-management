package org.school.management.resources.domain.repository;

import org.school.management.resources.domain.model.Reservation;
import org.school.management.resources.domain.valueobject.ReservationId;
import org.school.management.resources.domain.valueobject.ResourceId;
import org.school.management.teachers.domain.valueobject.TeacherId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository {
    Reservation save(Reservation reservation);
    Optional<Reservation> findById(ReservationId id);
    List<Reservation> findByResourceId(ResourceId resourceId);
    List<Reservation> findByTeacherId(TeacherId teacherId);
    List<Reservation> findActiveByResourceIdAndTimeRange(ResourceId resourceId, LocalDateTime start, LocalDateTime end);
    boolean existsOverlapping(ResourceId resourceId, LocalDateTime start, LocalDateTime end, ReservationId excludeId);
    void delete(ReservationId id);
}