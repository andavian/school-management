package org.school.management.auth.infra.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.application.usecases.*;
import org.school.management.auth.infra.web.SecurityContextHelper;
import org.school.management.auth.infra.web.dto.requests.*;
import org.school.management.auth.infra.web.dto.response.*;
import org.school.management.auth.infra.web.mappers.AuthWebMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * Controller de autenticación y gestión de cuenta propia.
 *
 * <p>Responsabilidad exclusiva: operaciones que el usuario hace sobre
 * su propia sesión e identidad — login, activar cuenta, cambiar password,
 * ver perfil, logout.</p>
 *
 * <p>Base path: {@code /api/auth}</p>
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Endpoints de autenticación y gestión de cuenta propia")
public class AuthController {

    private final LoginUseCase              loginUseCase;
    private final ActivateAccountUseCase    activateAccountUseCase;
    private final ChangePasswordUseCase     changePasswordUseCase;
    private final GetUserProfileUseCase     getUserProfileUseCase;
    private final RefreshTokenUseCase       refreshTokenUseCase;
    private final LogoutUseCase             logoutUseCase;
    private final AuthWebMapper             webMapper;

    // ── POST /api/auth/login ──────────────────────────────────────────────

    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica un usuario por DNI y contraseña. " +
                    "Devuelve access token JWT y refresh token opaco."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login exitoso",
                    content = @Content(schema = @Schema(implementation = LoginApiResponse.class))),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas",
                    content = @Content(schema = @Schema(implementation = ErrorApiResponse.class))),
            @ApiResponse(responseCode = "403", description = "Usuario inactivo",
                    content = @Content(schema = @Schema(implementation = ErrorApiResponse.class))),
            @ApiResponse(responseCode = "429", description = "Demasiados intentos fallidos")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginApiResponse> login(
            @Valid @RequestBody LoginApiRequest request,
            @RequestHeader(value = "X-Forwarded-For", required = false) String forwardedFor,
            @RequestHeader(value = "User-Agent", required = false) String userAgent,
            jakarta.servlet.http.HttpServletRequest httpRequest) {

        String clientIp = extractClientIp(forwardedFor, httpRequest);
        log.info("POST /api/auth/login — DNI: {} ip={}", request.dni(), clientIp);

        var loginResponse = loginUseCase.execute(
                webMapper.toApplicationDto(request),
                clientIp,
                userAgent
        );

        return ResponseEntity.ok(webMapper.toApiResponse(loginResponse));
    }

    // ── POST /api/auth/activate-account ──────────────────────────────────

    @Operation(
            summary = "Activar cuenta",
            description = "Activa la cuenta de un usuario usando el token opaco " +
                    "enviado por email. El token es de un solo uso y expira en 48 horas."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cuenta activada correctamente"),
            @ApiResponse(responseCode = "400", description = "Token inválido, ya utilizado o expirado",
                    content = @Content(schema = @Schema(implementation = ErrorApiResponse.class)))
    })
    @PostMapping("/activate-account")
    public ResponseEntity<SuccessApiResponse> activateAccount(
            @Valid @RequestBody ActivateAccountApiRequest request) {

        log.info("POST /api/auth/activate-account");

        var response = activateAccountUseCase.execute(
                new org.school.management.auth.application.dto.requests.ActivateAccountRequest(
                        request.token(),
                        request.newPassword()
                )
        );

        return ResponseEntity.ok(webMapper.createSuccessResponse(response.message()));
    }

    // ── POST /api/auth/refresh-token ──────────────────────────────────────

    @Operation(
            summary = "Renovar access token",
            description = "Genera un nuevo access token mediante rotación segura del refresh token. " +
                    "El refresh token anterior queda revocado y se emite uno nuevo " +
                    "(Token Rotation — OWASP)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token renovado correctamente",
                    content = @Content(schema = @Schema(implementation = RefreshTokenApiResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "Refresh token inválido, expirado o reutilizado (posible robo de sesión)",
                    content = @Content(schema = @Schema(implementation = ErrorApiResponse.class)))
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenApiResponse> refreshToken(
            @Valid @RequestBody RefreshTokenApiRequest request,
            @RequestHeader(value = "X-Forwarded-For", required = false) String forwardedFor,
            @RequestHeader(value = "User-Agent", required = false) String userAgent,
            jakarta.servlet.http.HttpServletRequest httpRequest) {

        String clientIp = extractClientIp(forwardedFor, httpRequest);
        log.info("POST /api/auth/refresh-token — ip={}", clientIp);

        var response = refreshTokenUseCase.execute(
                request.refreshToken(),
                clientIp,
                userAgent
        );

        return ResponseEntity.ok(webMapper.toApiResponse(response));
    }

    // ── POST /api/auth/logout ─────────────────────────────────────────────

    @Operation(
            summary = "Cerrar sesión",
            description = "Revoca el refresh token en BD. " +
                    "Si se envía el access token, también se blacklistea para " +
                    "invalidación inmediata sin esperar su expiración natural.",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sesión cerrada correctamente"),
            @ApiResponse(responseCode = "401", description = "Refresh token inválido")
    })
    @PostMapping("/logout")
    public ResponseEntity<SuccessApiResponse> logout(
            @Valid @RequestBody LogoutApiRequest request) {

        log.info("POST /api/auth/logout");

        logoutUseCase.logout(request.refreshToken(), request.accessToken());

        return ResponseEntity.ok(
                webMapper.createSuccessResponse("Sesión cerrada correctamente")
        );
    }

    // ── POST /api/auth/logout-all ─────────────────────────────────────────

    @Operation(
            summary = "Cerrar todas las sesiones",
            description = "Revoca todos los refresh tokens activos del usuario en todos " +
                    "los dispositivos. Útil si el usuario sospecha que su cuenta fue comprometida.",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Todas las sesiones cerradas"),
            @ApiResponse(responseCode = "401", description = "Refresh token inválido")
    })
    @PostMapping("/logout-all")
    public ResponseEntity<SuccessApiResponse> logoutAllDevices(
            @Valid @RequestBody RefreshTokenApiRequest request) {

        log.info("POST /api/auth/logout-all");

        logoutUseCase.logoutAllDevices(request.refreshToken());

        return ResponseEntity.ok(
                webMapper.createSuccessResponse("Todas las sesiones cerradas correctamente")
        );
    }

    // ── PUT /api/auth/change-password ─────────────────────────────────────

    @Operation(
            summary = "Cambiar contraseña",
            description = "Permite al usuario autenticado cambiar su contraseña actual.",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contraseña cambiada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Contraseña actual incorrecta"),
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
        var user   = getUserProfileUseCase.execute(userId);
        return ResponseEntity.ok(webMapper.toApiResponse(user));
    }

    // ── helpers ───────────────────────────────────────────────────────────

    private String extractClientIp(String forwardedFor,
                                   jakarta.servlet.http.HttpServletRequest request) {
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}