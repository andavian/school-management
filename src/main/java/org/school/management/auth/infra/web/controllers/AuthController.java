package org.school.management.auth.infra.web.controllers;

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

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final ActivateTeacherAccountUseCase activateTeacherAccountUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final GetUserProfileUseCase getUserProfileUseCase;
    //ate final LogoutUseCase logoutUseCase;
    private final AuthWebMapper webMapper;

    // ============================================
    // LOGIN - Endpoint público
    // ============================================
    @PostMapping("/login")
    public ResponseEntity<LoginApiResponse> login(@Valid @RequestBody LoginApiRequest request) {
        log.info("POST /api/auth/login - DNI: {}", request.dni());

        try {
            var applicationRequest = webMapper.toApplicationDto(request);
            var loginResponse = loginUseCase.execute(applicationRequest);
            var apiResponse = webMapper.toApiResponse(loginResponse);

            log.info("Login exitoso para DNI: {}", request.dni());
            return ResponseEntity.ok(apiResponse);

        } catch (InvalidPasswordException e) {
            log.warn("Credenciales inválidas para DNI: {}", request.dni());
            throw e;
        } catch (UserNotActiveException e) {
            log.warn("Usuario inactivo intentó login. DNI: {}", request.dni());
            throw e;
        }
    }

    // ============================================
    // ACTIVATE ACCOUNT - Para profesores (endpoint público con token)
    // ============================================
    @PostMapping("/activate-account")
    public ResponseEntity<SuccessApiResponse> activateAccount(
            @Valid @RequestBody ActivateAccountApiRequest request) {

        log.info("POST /api/auth/activate-account");

        try {
            var applicationRequest = new org.school.management.auth.application.dto.requests.ActivateAccountRequest(
                    request.token(),
                    request.newPassword()
            );

            var response = activateTeacherAccountUseCase.execute(applicationRequest);

            var apiResponse = webMapper.createSuccessResponse(response.message());

            log.info("Cuenta activada exitosamente");
            return ResponseEntity.ok(apiResponse);

        } catch (InvalidTokenException e) {
            log.warn("Token de activación inválido");
            throw e;
        } catch (InvalidOperationException e) {
            log.warn("Operación de activación inválida: {}", e.getMessage());
            throw e;
        }
    }

    // ============================================
    // CHANGE PASSWORD - Requiere autenticación
    // ============================================
    @PutMapping("/change-password")
    public ResponseEntity<SuccessApiResponse> changePassword(
            @Valid @RequestBody ChangePasswordApiRequest request) {

        log.info("PUT /api/auth/change-password");

        // Obtener userId del usuario autenticado
        String userId = getCurrentUserId();

        try {
            var applicationRequest = webMapper.toApplicationDto(request);
            // Setear el userId desde el token JWT
            var requestWithUserId = new org.school.management.auth.application.dto.requests.ChangePasswordRequest(
                    userId,
                    applicationRequest.currentPassword(),
                    applicationRequest.newPassword()
            );

            var response = changePasswordUseCase.execute(requestWithUserId);

            var apiResponse = webMapper.createSuccessResponse(response.message());

            log.info("Password cambiado exitosamente para usuario: {}", userId);
            return ResponseEntity.ok(apiResponse);

        } catch (InvalidCurrentPasswordException e) {
            log.warn("Password actual incorrecto para usuario: {}", userId);
            throw e;
        }
    }

    // ============================================
    // GET PROFILE - Usuario autenticado obtiene su perfil
    // ============================================
    @GetMapping("/profile")
    public ResponseEntity<UserApiResponse> getProfile() {
        log.info("GET /api/auth/profile");

        String userId = getCurrentUserId();

        var userResponse = getUserProfileUseCase.execute(userId);
        var apiResponse = webMapper.toApiResponse(userResponse);

        log.debug("Perfil obtenido para usuario: {}", userId);
        return ResponseEntity.ok(apiResponse);
    }

    // ============================================
    // LOGOUT - Invalidar token
    // ============================================
//    @PostMapping("/logout")
//    public ResponseEntity<SuccessApiResponse> logout(
//            @RequestHeader("Authorization") String authorizationHeader) {
//
//        log.info("POST /api/auth/logout");
//
//        try {
//            // Extraer token del header
//            String token = authorizationHeader.replace("Bearer ", "");
//
//            var logoutRequest = new org.school.management.auth.application.dto.requests.LogoutRequest(token);
//            var response = logoutUseCase.execute(logoutRequest);
//
//            var apiResponse = webMapper.createSuccessResponse(response.message());
//
//            log.info("Logout exitoso");
//            return ResponseEntity.ok(apiResponse);
//
//        } catch (Exception e) {
//            log.error("Error durante logout: {}", e.getMessage());
//            // Aún así retornar éxito
//            return ResponseEntity.ok(webMapper.createSuccessResponse("Sesión cerrada"));
//        }
//    }

    // ============================================
    // REFRESH TOKEN - Para renovar token expirado
    // ============================================
    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenApiResponse> refreshToken(
            @Valid @RequestBody RefreshTokenApiRequest request) {

        log.info("POST /api/auth/refresh-token");

        // TODO: Implementar RefreshTokenUseCase
        throw new UnsupportedOperationException("Refresh token no implementado aún");
    }

    // ============================================
    // Helper methods
    // ============================================

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
