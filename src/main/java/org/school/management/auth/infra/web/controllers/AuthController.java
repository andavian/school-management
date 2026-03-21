package org.school.management.auth.infra.web.controllers;

import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.application.usecases.ActivateAccountUseCase;
import org.school.management.auth.application.usecases.ChangePasswordUseCase;
import org.school.management.auth.application.usecases.GetUserProfileUseCase;
import org.school.management.auth.application.usecases.LoginUseCase;
import org.school.management.auth.infra.web.SecurityContextHelper;
import org.school.management.auth.infra.web.dto.response.*;
import org.school.management.auth.infra.web.dto.requests.*;
import org.school.management.auth.infra.web.mappers.AuthWebMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

/**
 * Controller de autenticación y gestión de cuenta propia.
 *
 * <p>Responsabilidad exclusiva: operaciones que el usuario hace sobre
 * su propia sesión e identidad — login, activar cuenta, cambiar password,
 * ver perfil. No gestiona otros usuarios ni entidades de dominio.</p>
 *
 * <p>Base path: {@code /api/auth}</p>
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Endpoints de autenticación y gestión de cuenta propia")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final ActivateAccountUseCase activateTeacherAccountUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final GetUserProfileUseCase getUserProfileUseCase;
    private final AuthWebMapper webMapper;

    // ── POST /api/auth/login ──────────────────────────────────────────────

    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica un usuario por DNI y contraseña. Devuelve JWT si las credenciales son válidas."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login exitoso",
                    content = @Content(schema = @Schema(implementation = LoginApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Credenciales inválidas",
                    content = @Content(schema = @Schema(implementation = ErrorApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Usuario inactivo",
                    content = @Content(schema = @Schema(implementation = ErrorApiResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<LoginApiResponse> login(
            @Valid @RequestBody LoginApiRequest request) {

        log.info("POST /api/auth/login — DNI: {}", request.dni());

        var applicationRequest = webMapper.toApplicationDto(request);
        var loginResponse = loginUseCase.execute(applicationRequest);
        return ResponseEntity.ok(webMapper.toApiResponse(loginResponse));
    }

    // ── POST /api/auth/activate-account ──────────────────────────────────

    @Operation(
            summary = "Activar cuenta de profesor",
            description = "Activa la cuenta de un profesor usando el token enviado por email."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cuenta activada correctamente",
                    content = @Content(schema = @Schema(implementation = SuccessApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Token inválido o expirado",
                    content = @Content(schema = @Schema(implementation = ErrorApiResponse.class)))
    })
    @PostMapping("/activate-account")
    public ResponseEntity<SuccessApiResponse> activateAccount(
            @Valid @RequestBody ActivateAccountApiRequest request) {

        log.info("POST /api/auth/activate-account");

        var applicationRequest = new org.school.management.auth.application.dto.requests.ActivateAccountRequest(
                request.token(),
                request.newPassword()
        );
        var response = activateTeacherAccountUseCase.execute(applicationRequest);
        return ResponseEntity.ok(webMapper.createSuccessResponse(response.message()));
    }

    // ── PUT /api/auth/change-password ─────────────────────────────────────

    @Operation(
            summary = "Cambiar contraseña",
            description = "Permite al usuario autenticado cambiar su contraseña actual.",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contraseña cambiada exitosamente",
                    content = @Content(schema = @Schema(implementation = SuccessApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "Contraseña actual incorrecta",
                    content = @Content(schema = @Schema(implementation = ErrorApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
    })
    @PutMapping("/change-password")
    public ResponseEntity<SuccessApiResponse> changePassword(
            @Valid @RequestBody ChangePasswordApiRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        log.info("PUT /api/auth/change-password");

        var appRequest = new org.school.management.auth.application.dto.requests.ChangePasswordRequest(
                SecurityContextHelper.extractUserId(userDetails).toString(),
                webMapper.toApplicationDto(request).currentPassword(),
                webMapper.toApplicationDto(request).newPassword()
        );
        var response = changePasswordUseCase.execute(appRequest);
        return ResponseEntity.ok(webMapper.createSuccessResponse(response.message()));
    }

    // ── GET /api/auth/profile ─────────────────────────────────────────────

    @Operation(
            summary = "Obtener perfil propio",
            description = "Devuelve la información del usuario autenticado.",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil del usuario",
                    content = @Content(schema = @Schema(implementation = UserApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
    })
    @GetMapping("/profile")
    public ResponseEntity<UserApiResponse> getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {

        log.info("GET /api/auth/profile");

        var userId = SecurityContextHelper.extractUserId(userDetails).toString();
        var user = getUserProfileUseCase.execute(userId);
        return ResponseEntity.ok(webMapper.toApiResponse(user));
    }

    // ── POST /api/auth/refresh-token ──────────────────────────────────────

    @Operation(summary = "Renovar token JWT",
            description = "Renueva un token JWT expirado usando un refresh token válido.")
    @ApiResponses({
            @ApiResponse(responseCode = "501", description = "Aún no implementado")
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenApiResponse> refreshToken(
            @Valid @RequestBody RefreshTokenApiRequest request) {

        log.info("POST /api/auth/refresh-token");
        throw new UnsupportedOperationException("Refresh token no implementado aún");
    }
}