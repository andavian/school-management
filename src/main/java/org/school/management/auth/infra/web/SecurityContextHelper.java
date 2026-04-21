package org.school.management.auth.infra.web;

import org.school.management.auth.domain.model.User;
import org.school.management.auth.infra.security.UserPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

/**
 * Utilidad estática para extraer información del usuario autenticado
 * desde el contexto de seguridad de Spring.
 *
 * <p>Centraliza el cast de {@link UserDetails} → {@link User} (domain model de auth/)
 * que todos los controllers protegidos necesitan. Sin este helper, el mismo
 * bloque de código estaría duplicado en cada controller del proyecto.</p>
 *
 * <p><strong>Por qué es un cruce de infraestructura aceptado:</strong>
 * Spring Security garantiza que {@code @AuthenticationPrincipal} es el objeto
 * devuelto por {@code CustomUserDetailsService.loadUserByUsername()}, que en
 * este proyecto siempre devuelve {@link User}. El cast es seguro en todos los
 * endpoints protegidos con JWT.</p>
 *
 * <p><strong>Por qué es estático y no un {@code @Component}:</strong>
 * No tiene estado ni dependencias de Spring — una clase utilitaria pura
 * es más honesta que un bean sin razón de serlo.</p>
 *
 * <p>Ubicación: {@code auth/infra/web/} — misma excepción de paquete que el
 * resto del módulo auth (usa {@code infra} en lugar de {@code infrastructure}).</p>
 */
public final class SecurityContextHelper {

    private SecurityContextHelper() {
        // Clase utilitaria — no instanciar
    }

    /**
     * Extrae el {@link UUID} del usuario autenticado a partir del principal
     * inyectado por {@code @AuthenticationPrincipal}.
     *
     * @param userDetails el principal inyectado por Spring Security
     * @return el UUID interno del {@link User} autenticado
     * @throws IllegalStateException si el principal no es una instancia de {@link User},
     *                               lo que indicaría una configuración de seguridad inesperada
     */
    public static UUID extractUserId(UserDetails userDetails) {
        if (userDetails instanceof UserPrincipal principal) {
            return principal.user().getUserId().value();
        }
        if (userDetails instanceof User user) {
            return user.getUserId().value();
        }

        throw new IllegalStateException(
                "Principal inesperado en el contexto de seguridad: "
                        + userDetails.getClass().getName()
                        + ". Se esperaba org.school.management.auth.domain.model.User"
        );
    }
}