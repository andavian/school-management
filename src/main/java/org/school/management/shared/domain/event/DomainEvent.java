package org.school.management.shared.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Contrato base para todos los eventos de dominio del sistema.
 *
 * <p>Un evento de dominio representa algo que ocurrió en el dominio y que
 * otros bounded contexts pueden necesitar conocer. Son inmutables por diseño
 * — describen un hecho del pasado, no una intención futura.</p>
 *
 * <p>Convenciones del proyecto:</p>
 * <ul>
 *   <li>Los eventos se implementan como {@code record} de Java 17.</li>
 *   <li>Todos los eventos tienen un factory method estático {@code of(...)}.</li>
 *   <li>El nombre siempre en pasado: {@code AccountActivatedEvent},
 *       {@code StudentEnrolledEvent}, etc.</li>
 *   <li>Viven en {@code shared/domain/event/} — son transversales a los BCs.</li>
 * </ul>
 */
public interface DomainEvent {

    /**
     * Identificador único del evento — permite deduplicación y trazabilidad.
     */
    UUID eventId();

    /**
     * Momento exacto en que ocurrió el evento en el dominio.
     */
    LocalDateTime occurredOn();
}