package org.school.management.resources.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.resources.application.dto.request.CreateReservationRequest;
import org.school.management.resources.application.dto.response.ReservationResponse;
import org.school.management.resources.application.mapper.ResourceApplicationMapper;
import org.school.management.resources.domain.exception.ResourceNotAvailableException;
import org.school.management.resources.domain.exception.ResourceNotFoundException;
import org.school.management.resources.domain.exception.ReservationConflictException;
import org.school.management.resources.domain.model.Reservation;
import org.school.management.resources.domain.model.Resource;
import org.school.management.resources.domain.repository.ReservationRepository;
import org.school.management.resources.domain.repository.ResourceRepository;
import org.school.management.resources.domain.valueobject.ResourceId;
import org.school.management.teachers.domain.exception.TeacherNotFoundException;
import org.school.management.teachers.domain.repository.TeacherRepository;
import org.school.management.teachers.domain.valueobject.TeacherId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CreateReservationUseCase {
    private final ResourceRepository resourceRepository;
    private final ReservationRepository reservationRepository;
    private final TeacherRepository teacherRepository;
    private final ResourceApplicationMapper mapper;

    public ReservationResponse execute(UUID resourceId, CreateReservationRequest request, UserId userId) {
        // 1. Obtener TeacherId desde userId
        TeacherId teacherId = teacherRepository.findTeacherIdByUserId(userId)
                .orElseThrow(() -> new TeacherNotFoundException("Teacher not found for user id: " + userId));

        // 2. Verificar que el recurso existe y está disponible
        Resource resource = resourceRepository.findById(ResourceId.of(resourceId))
                .orElseThrow(() -> ResourceNotFoundException.byId(resourceId));
        if (!resource.isAvailable()) {
            throw ResourceNotAvailableException.becauseStatus(resource.getStatus().name());
        }

        // 3. Verificar solapamiento con otras reservas activas
        if (reservationRepository.existsOverlapping(ResourceId.of(resourceId), request.startTime(), request.endTime(), null)) {
            throw ReservationConflictException.overlapping();
        }

        // 4. Crear y guardar reserva
        Reservation reservation = Reservation.create(
                ResourceId.of(resourceId),
                teacherId,
                request.startTime(),
                request.endTime(),
                request.purpose()
        );
        Reservation saved = reservationRepository.save(reservation);
        log.info("Reservation created: {} for resource {}", saved.getReservationId().value(), resourceId);
        return mapper.toResponse(saved);
    }
}