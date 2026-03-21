package org.school.management.shared.infrastructure.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.shared.domain.event.DomainEvent;
import org.school.management.shared.domain.event.DomainEventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Implementación de {@link DomainEventPublisher} que delega en el
 * {@link ApplicationEventPublisher} de Spring.
 *
 * <p>Los listeners registran con {@code @TransactionalEventListener(phase = BEFORE_COMMIT)}
 * para garantizar que reaccionan dentro de la misma transacción que publicó el evento.
 * Si el listener falla, toda la transacción se revierte — atomicidad garantizada.</p>
 *
 * <p>Esta clase vive en {@code shared/infrastructure/} — es el único lugar
 * del proyecto que conoce tanto {@link DomainEvent} como Spring.</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SpringDomainEventPublisher implements DomainEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(DomainEvent event) {
        log.debug("Publishing domain event: {} — eventId: {}",
                event.getClass().getSimpleName(), event.eventId());
        applicationEventPublisher.publishEvent(event);
    }
}