package org.school.management.geography.infra.web.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.geography.application.dto.request.CreatePlaceRequest;
import org.school.management.geography.application.dto.response.PlaceResponse;
import org.school.management.geography.application.usecases.CreatePlaceUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/geography")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class GeographyAdminController {

    private final CreatePlaceUseCase createPlaceUseCase;

    /**
     * Crear nuevo lugar
     * POST /api/admin/geography/places
     */
    @PostMapping("/places")
    public ResponseEntity<PlaceResponse> createPlace(
            @RequestBody CreatePlaceRequest request
    ) {
        log.info("POST /api/admin/geography/places - name: {}", request.name());

        PlaceResponse place = createPlaceUseCase.execute(request);

        return ResponseEntity.ok(place);
    }

    // TODO: Agregar endpoints para crear/editar Countries y Provinces si es necesario
}
