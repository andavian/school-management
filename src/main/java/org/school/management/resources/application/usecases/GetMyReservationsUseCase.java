// src/main/java/org/school/management/resources/application/usecases/GetMyReservationsUseCase.java
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

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetMyReservationsUseCase {

    private final ReservationRepository reservationRepository;
    private final ResourceApplicationMapper mapper;

    public List<ReservationResponse> execute(UUID requesterId) {
        log.debug("Obteniendo reservas del usuario: {}", requesterId);

        List<ReservationResponse> responses = reservationRepository.findByRequesterId(requesterId).stream()
                .map(mapper::toReservationResponse)
                .toList();

        log.info("Se encontraron {} reservas para el usuario {}", responses.size(), requesterId);
        return responses;
    }
}