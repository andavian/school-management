package org.school.management.resources.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.resources.domain.exception.ReservationAccessDeniedException;
import org.school.management.resources.domain.exception.ReservationNotFoundException;
import org.school.management.resources.domain.model.Reservation;
import org.school.management.resources.domain.repository.ReservationRepository;
import org.school.management.resources.domain.valueobject.ReservationId;
import org.school.management.teachers.domain.repository.TeacherRepository;
import org.school.management.teachers.domain.valueobject.TeacherId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CancelReservationUseCase {
    private final ReservationRepository reservationRepository;
    private final TeacherRepository teacherRepository;

    public void execute(UUID reservationId, UUID userId, boolean isAdmin) {
        Reservation reservation = reservationRepository.findById(ReservationId.of(reservationId))
                .orElseThrow(() -> ReservationNotFoundException.byId(reservationId));

        if (!isAdmin) {
            TeacherId teacherId = teacherRepository.findTeacherIdByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Teacher not found for user"));
            if (!reservation.getTeacherId().equals(teacherId)) {
                throw ReservationAccessDeniedException.notOwner();
            }
        }

        reservation.cancel();
        reservationRepository.save(reservation);
        log.info("Reservation cancelled: {}", reservationId);
    }
}