package org.school.management.auth.application.dto.requests;

/**
 * Request para crear un {@code User} con un rol dado.
 *
 * <p>{@code startActive} controla si el usuario queda activo inmediatamente
 * o requiere activación posterior via link en email:</p>
 * <ul>
 *   <li>{@code true} — estudiantes y padres (activos desde el inicio)</li>
 *   <li>{@code false} — teachers (requieren activar cuenta via email)</li>
 * </ul>
 *
 * @param dni           DNI del usuario (8 dígitos, sin puntos)
 * @param plainPassword contraseña en texto plano — el use case la hashea internamente
 * @param roleName      nombre del rol: "ROLE_TEACHER", "ROLE_STUDENT", "ROLE_PARENT", etc.
 * @param startActive   si {@code false}, el usuario se crea con {@code active = false}
 */
public record CreateUserRequest(
        String dni,
        String plainPassword,
        String roleName,
        boolean startActive
) {
    /** Factory method para usuarios que inician activos (students, parents). */
    public static CreateUserRequest active(String dni, String plainPassword, String roleName) {
        return new CreateUserRequest(dni, plainPassword, roleName, true);
    }

    /** Factory method para usuarios que requieren activación posterior (teachers). */
    public static CreateUserRequest inactive(String dni, String plainPassword, String roleName) {
        return new CreateUserRequest(dni, plainPassword, roleName, false);
    }
}