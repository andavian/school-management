package org.school.management.auth.infra.web.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.infra.web.dto.requests.*;
import org.school.management.auth.infra.web.dto.response.*;
import org.school.management.auth.infra.web.mappers.AuthWebMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final AuthWebMapper webMapper;

    // ============================================
    // UPDATE MY PROFILE - Cualquier usuario autenticado
    // ============================================
    @PutMapping("/profile")
    public ResponseEntity<UserApiResponse> updateMyProfile(
            @Valid @RequestBody UpdateProfileApiRequest request) {

        log.info("PUT /api/users/profile");

        String userId = getCurrentUserId();

        // TODO: Implementar UpdateProfileUseCase
        throw new UnsupportedOperationException("Endpoint no implementado aún");
    }

    // ============================================
    // GET MY PROFILE - Cualquier usuario autenticado (alias de /api/auth/profile)
    // ============================================
    @GetMapping("/profile")
    public ResponseEntity<UserApiResponse> getMyProfile() {
        log.info("GET /api/users/profile");

        // Redirigir a AuthController.getProfile() o duplicar lógica
        throw new UnsupportedOperationException("Usar /api/auth/profile en su lugar");
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        org.school.management.auth.domain.model.User user =
                (org.school.management.auth.domain.model.User) authentication.getPrincipal();
        return user.getUserId().asString();
    }
}