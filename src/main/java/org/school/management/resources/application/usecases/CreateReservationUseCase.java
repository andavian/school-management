package org.school.management.resources.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.resources.application.dto.request.CreateReservationRequest;
import org.school.management.resources.application.dto.response.ReservationResponse;
import org.school.management.resources.application.mapper.ResourceApplicationMapper;
import org.school.management.resources.domain.model.Reservation;
import org.school.management.resources.domain.model.Resource;
import org.school.management.resources.domain.repository.ReservationRepository;
import org.school.management.resources.domain.repository.ResourceRepository;
import org.school.management.resources.domain.service.ReservationDomainService;
import org.school.management.resources.domain.valueobject.ResourceId;
import org.school.management.auth.domain.valueobject.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateReservationUseCase {

    private final ResourceRepository resourceRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationDomainService reservationDomainService;
    private final ResourceApplicationMapper mapper;

    @Transactional
    public ReservationResponse execute(CreateReservationRequest request, UUID requesterId, String requesterName) {
        Resource resource = resourceRepository.findByResourceId(ResourceId.of(request.resourceId()))
                .orElseThrow(() -> new IllegalArgumentException("Recurso no encontrado"));

        if (!resource.isReservable()) {
            throw new IllegalStateException("El recurso no permite reservas");
        }

        // 1. Crear agregado de dominio (estado inicial: CONFIRMED)
        Reservation reservation = Reservation.create(
                resource.getResourceId(),
                UserId.of(requesterId),
                requesterName,
                request.reservationDate(),
                request.startTime(),
                request.endTime(),
                request.quantityRequested(),
                request.purpose(),
                request.gradeLevelInfo()
        );

        // 2. Domain Service valida disponibilidad en el rango horario y asigna unidades físicas
        reservationDomainService.confirmAndAssignUnits(reservation);

        // 3. Persistir cabecera + relaciones ReservationUnit (todo en una transacción)
        Reservation saved = reservationRepository.save(reservation);

        log.info("Reserva creada exitosamente: {} | Recurso: {} | Unidades: {}",
                saved.getReservationId(), resource.getCode(), saved.getAssignedUnits().size());

        return mapper.toResponse(saved, resource.getCode());
    }
}