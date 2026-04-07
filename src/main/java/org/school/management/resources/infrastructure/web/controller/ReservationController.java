package org.school.management.resources.infrastructure.web.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.infra.web.SecurityContextHelper;
import org.school.management.resources.application.dto.request.CancelReservationRequest;
import org.school.management.resources.application.dto.request.CreateReservationRequest;
import org.school.management.resources.application.dto.request.ReturnReservationRequest;
import org.school.management.resources.application.dto.response.ReservationResponse;
import org.school.management.resources.application.usecases.*;
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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/resources/reservations")
@RequiredArgsConstructor @Slf4j @Validated
@Tag(name = "Reservations", description = "Gestión de reservas de recursos didácticos")
@SecurityRequirement(name = "bearerAuth")
public class ReservationController {

    private final CreateReservationUseCase createReservationUseCase;
    private final CancelReservationUseCase cancelReservationUseCase;
    private final ReturnReservationUseCase returnReservationUseCase;
    private final MarkReservationInUseUseCase markInUseUseCase;
    private final GetMyReservationsUseCase getMyReservationsUseCase;
    private final GetResourceAvailabilityUseCase availabilityUseCase;
    private final ResourcesWebMapper webMapper;

    // ─── CREAR RESERVA ─────────────────────────────────────────────────
    @PostMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'STAFF')")
    public ResponseEntity<ReservationWebDto.ReservationWebResponse> create(
            @Valid @RequestBody ReservationWebDto.CreateReservationWebRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID userId = SecurityContextHelper.extractUserId(userDetails);
        String requesterName = userDetails.getUsername(); // Temporal: idealmente inyectar GetProfileUseCase

        CreateReservationRequest appRequest = new CreateReservationRequest(
                request.resourceId(), request.reservationDate(), request.startTime(), request.endTime(),
                request.quantityRequested(), request.purpose(), request.gradeLevelInfo()
        );

        ReservationResponse response = createReservationUseCase.execute(appRequest, userId, requesterName);
        return ResponseEntity.status(HttpStatus.CREATED).body(webMapper.toReservationWebResponse(response));
    }

    // ─── MIS RESERVAS ──────────────────────────────────────────────────
    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'STAFF')")
    public ResponseEntity<List<ReservationWebDto.ReservationWebResponse>> getMyReservations(
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID userId = SecurityContextHelper.extractUserId(userDetails);
        List<ReservationResponse> responses = getMyReservationsUseCase.execute(userId);
        List<ReservationWebDto.ReservationWebResponse> webResponses = responses.stream()
                .map(webMapper::toReservationWebResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(webResponses);
    }

    // ─── CONSULTAR DISPONIBILIDAD ──────────────────────────────────────
    @GetMapping("/availability")
    public ResponseEntity<GetResourceAvailabilityUseCase.AvailabilityInfo> getAvailability(
            @RequestParam UUID resourceId,
            @RequestParam LocalDate date,
            @RequestParam LocalTime start,
            @RequestParam LocalTime end) {

        var info = availabilityUseCase.execute(ResourceId.of(resourceId), date, start, end);
        return ResponseEntity.ok(info);
    }

    // ─── MARCAR COMO EN USO (RETIRO) ───────────────────────────────────
    @PatchMapping("/{reservationId}/start")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ReservationWebDto.ReservationWebResponse> markAsInUse(
            @PathVariable UUID reservationId,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID actorId = SecurityContextHelper.extractUserId(userDetails);
        ReservationResponse response = markInUseUseCase.execute(reservationId, actorId);
        return ResponseEntity.ok(webMapper.toReservationWebResponse(response));
    }

    // ─── REGISTRAR DEVOLUCIÓN ──────────────────────────────────────────
    @PatchMapping("/{reservationId}/return")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ReservationWebDto.ReservationWebResponse> markAsReturned(
            @PathVariable UUID reservationId,
            @Valid @RequestBody ReservationWebDto.ReturnReservationWebRequest request) {

        ReturnReservationRequest appRequest = new ReturnReservationRequest(request.observations());
        ReservationResponse response = returnReservationUseCase.execute(reservationId, appRequest);
        return ResponseEntity.ok(webMapper.toReservationWebResponse(response));
    }

    // ─── CANCELAR RESERVA ──────────────────────────────────────────────
    @PatchMapping("/{reservationId}/cancel")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'STAFF')")
    public ResponseEntity<ReservationWebDto.ReservationWebResponse> cancel(
            @PathVariable UUID reservationId,
            @Valid @RequestBody ReservationWebDto.CancelReservationWebRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID actorId = SecurityContextHelper.extractUserId(userDetails);
        ReservationResponse response = cancelReservationUseCase.execute(reservationId, actorId, request.reason());
        return ResponseEntity.ok(webMapper.toReservationWebResponse(response));
    }
}