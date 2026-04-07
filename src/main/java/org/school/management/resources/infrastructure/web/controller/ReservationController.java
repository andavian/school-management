package org.school.management.resources.infrastructure.web.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.infra.web.SecurityContextHelper;
import org.school.management.resources.application.dto.response.ReservationResponse;
import org.school.management.resources.application.usecases.CreateReservationUseCase;
import org.school.management.resources.application.usecases.GetResourceAvailabilityUseCase;
import org.school.management.resources.application.usecases.MarkReservationInUseUseCase;
import org.school.management.resources.domain.valueobject.ResourceId;
import org.school.management.resources.infrastructure.web.dto.ReservationWebDto;
import org.school.management.resources.infrastructure.web.mapper.ResourcesWebMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/resources/reservations")
@RequiredArgsConstructor @Slf4j @Validated
@Tag(name = "Reservations") @SecurityRequirement(name = "bearerAuth")
public class ReservationController {

    private final CreateReservationUseCase createReservationUseCase;
    private final GetResourceAvailabilityUseCase availabilityUseCase;
    private final MarkReservationInUseUseCase markInUseUseCase;
    private final ResourcesWebMapper webMapper;

    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'STAFF')")
    public ResponseEntity<ReservationWebDto.ReservationWebResponse> create(
            @Valid @RequestBody ReservationWebDto.CreateReservationWebRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID userId = SecurityContextHelper.extractUserId(userDetails);
        // Nota: En producción, inyectar GetProfileUseCase para obtener nombre completo desnormalizado
        String requesterName = userDetails.getUsername();

        ReservationResponse response = createReservationUseCase.execute(request, userId, requesterName);
        return ResponseEntity.status(HttpStatus.CREATED).body(webMapper.toReservationWebResponse(response));
    }

    @GetMapping("/availability")
    public ResponseEntity<GetResourceAvailabilityUseCase.AvailabilityInfo> getAvailability(
            @RequestParam UUID resourceId,
            @RequestParam LocalDate date,
            @RequestParam LocalTime start,
            @RequestParam LocalTime end) {

        var info = availabilityUseCase.execute(ResourceId.of(resourceId), date, start, end);
        return ResponseEntity.ok(info);
    }

    @PatchMapping("/{reservationId}/start")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ReservationWebDto.ReservationWebResponse> markInUse(
            @PathVariable UUID reservationId,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID actorId = SecurityContextHelper.extractUserId(userDetails);
        ReservationResponse response = markInUseUseCase.execute(reservationId, actorId);
        return ResponseEntity.ok(webMapper.toReservationWebResponse(response));
    }
}