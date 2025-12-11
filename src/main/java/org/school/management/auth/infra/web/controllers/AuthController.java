package org.school.management.auth.infra.web.controllers;

import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.application.dto.responses.UserResponse;
import org.school.management.auth.application.usecases.admin.*;
import org.school.management.auth.domain.exception.*;
import org.school.management.auth.infra.web.dto.response.*;
import org.school.management.auth.infra.web.dto.requests.*;
import org.school.management.auth.infra.web.mappers.AuthWebMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Endpoints de autenticación y gestión de cuentas")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final ActivateTeacherAccountUseCase activateTeacherAccountUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final GetUserProfileUseCase getUserProfileUseCase;
    private final AuthWebMapper webMapper;

    // ============================================================
    // LOGIN  (PUBLIC)
    // ============================================================
    @Operation(
            summary = "Iniciar sesión",
            description = "Permite autenticar un usuario por DNI y contraseña. Devuelve un JWT si las credenciales son válidas."
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

        log.info("POST /api/auth/login - DNI {}", request.dni());

        var applicationRequest = webMapper.toApplicationDto(request);
        var loginResponse = loginUseCase.execute(applicationRequest);
        var apiResponse = webMapper.toApiResponse(loginResponse);

        return ResponseEntity.ok(apiResponse);
    }

    // ============================================================
    // ACTIVATE ACCOUNT (PUBLIC con token)
    // ============================================================
    @Operation(
            summary = "Activar cuenta de profesor",
            description = "Activa la cuenta de un profesor utilizando el token enviado por email."
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

    // ============================================================
    // CHANGE PASSWORD (PRIVATE - requiere JWT)
    // ============================================================
    @Operation(
            summary = "Cambiar contraseña",
            description = "Permite a un usuario autenticado cambiar su contraseña actual.",
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
            @Valid @RequestBody ChangePasswordApiRequest request) {

        log.info("PUT /api/auth/change-password");

        String userId = getCurrentUserId();

        var appRequest = webMapper.toApplicationDto(request);
        var requestWithUserId = new org.school.management.auth.application.dto.requests.ChangePasswordRequest(
                userId,
                appRequest.currentPassword(),
                appRequest.newPassword()
        );

        var response = changePasswordUseCase.execute(requestWithUserId);

        return ResponseEntity.ok(webMapper.createSuccessResponse(response.message()));
    }

    // ============================================================
    // PROFILE (PRIVATE)
    // ============================================================
    @Operation(
            summary = "Obtener perfil",
            description = "Devuelve la información del usuario autenticado.",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil del usuario",
                    content = @Content(schema = @Schema(implementation = UserApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
    })
    @GetMapping("/profile")
    public ResponseEntity<UserApiResponse> getProfile() {
        log.info("GET /api/auth/profile");

        String userId = getCurrentUserId();
        var user = getUserProfileUseCase.execute(userId);

        return ResponseEntity.ok(webMapper.toApiResponse(user));
    }

    // ============================================================
    // REFRESH TOKEN (PUBLIC)
    // ============================================================
    @Operation(
            summary = "Renovar token JWT",
            description = "Renueva un token JWT expirado utilizando un refresh token válido."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "501", description = "Aún no implementado")
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenApiResponse> refreshToken(
            @Valid @RequestBody RefreshTokenApiRequest request) {

        log.info("POST /api/auth/refresh-token");
        throw new UnsupportedOperationException("Refresh token no implementado aún");
    }

    // ============================================================
    // Helper
    // ============================================================
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Usuario no autenticado");
        }

        org.school.management.auth.domain.model.User user =
                (org.school.management.auth.domain.model.User) authentication.getPrincipal();

        return user.getUserId().asString();
    }

    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) {
            super(message);
        }
    }
}
