package org.school.management.resources.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.resources.application.dto.response.ReservationResponse;
import org.school.management.resources.application.mapper.ResourceApplicationMapper;
import org.school.management.resources.domain.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetMyReservationsUseCase {

    private final ReservationRepository reservationRepository;
    private final ResourceApplicationMapper mapper;

    /**
     * Retorna todas las reservas asociadas a un solicitante.
     * Útil para que docentes/admin vean su historial y estado actual.
     */
    public List<ReservationResponse> execute(UUID requesterId) {
        log.debug("Fetching reservations for requester: {}", requesterId);
        return reservationRepository.findByRequesterId(requesterId).stream()
                .map(mapper::toReservationResponse)
                .collect(Collectors.toList());
    }
}