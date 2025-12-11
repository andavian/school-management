package org.school.management.geography.infra.web.controllers;

import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.infra.web.dto.response.ErrorApiResponse;
import org.school.management.geography.application.dto.request.CreatePlaceRequest;
import org.school.management.geography.application.dto.response.PlaceResponse;
import org.school.management.geography.application.usecases.CreatePlaceUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/admin/geography")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')") // Seguridad global
@Tag(
        name = "Geography - Admin",
        description = "Gestión administrativa de entidades geográficas (países, provincias y localidades)"
)
public class GeographyAdminController {

    private final CreatePlaceUseCase createPlaceUseCase;

    // ============================================================
    // CREATE PLACE
    // ============================================================

    @Operation(
            summary = "Crear nueva localidad",
            description = """
            Permite crear un nuevo lugar (ciudad/localidad/pueblo) dentro de una provincia
            y un país específico. Solo accesible para administradores.
            """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Localidad creada",
                    content = @Content(schema = @Schema(implementation = PlaceResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos o provincia inexistente",
                    content = @Content(schema = @Schema(implementation = ErrorApiResponse.class))
            )
    })
    @PostMapping("/places")
    public ResponseEntity<PlaceResponse> createPlace(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos para crear un lugar",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CreatePlaceRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Alta Gracia",
                                            value = """
                                    {
                                      "name": "Alta Gracia",
                                      "type": "CIUDAD",
                                      "provinceId": "5c2d3e80-8e6b-4cbc-92b0-8967329a1aaf"
                                    }
                                    """
                                    )
                            }
                    )
            )
            @RequestBody CreatePlaceRequest request
    ) {
        log.info("POST /api/admin/geography/places - name={}", request.name());

        PlaceResponse createdPlace = createPlaceUseCase.execute(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdPlace);
    }

    // ============================================================
    // Aquí podemos agregar futuros endpoints ADMIN:
    // - Crear país
    // - Editar país
    // - Crear provincia
    // - Editar provincia
    // ============================================================

}
