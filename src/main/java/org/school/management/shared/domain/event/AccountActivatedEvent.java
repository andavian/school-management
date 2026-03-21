package org.school.management.shared.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Evento publicado cuando un usuario activa su cuenta exitosamente.
 *
 * <p>Cada bounded context que necesite reaccionar a la activación de cuenta
 * registra su propio listener — sin que {@code auth/} conozca a nadie.</p>
 *
 * <p>Actualmente los listeners conocidos son:</p>
 * <ul>
 *   <li>{@code teachers/infrastructure/event/TeacherAccountActivatedListener}
 *       — activa la entidad {@code Teacher} cuando {@code roleName = "TEACHER"}.</li>
 * </ul>
 *
 * <p>Cuando se implemente activación para otros roles (padres, etc.) solo hay
 * que agregar un nuevo listener — sin modificar este evento ni {@code auth/}.</p>
 *
 * @param eventId     identificador único del evento para trazabilidad
 * @param occurredOn  momento en que ocurrió la activación
 * @param userId      UUID del {@code User} activado
 * @param dni         DNI del usuario — usado por los BCs para buscar su entidad
 * @param roleName    nombre del rol principal — permite a cada listener filtrar
 *                    si le corresponde reaccionar
 */
public record AccountActivatedEvent(
        UUID eventId,
        LocalDateTime occurredOn,
        UUID userId,
        String dni,
        String roleName
) implements DomainEvent {

    /**
     * Factory method principal — genera {@code eventId} y {@code occurredOn}
     * automáticamente para que el publicador no tenga que hacerlo.
     */
    public static AccountActivatedEvent of(UUID userId, String dni, String roleName) {
        return new AccountActivatedEvent(
                UUID.randomUUID(),
                LocalDateTime.now(),
                userId,
                dni,
                roleName
        );
    }
}