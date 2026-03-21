package org.school.management.shared.domain.event;

/**
 * Puerto del dominio para publicar eventos de dominio.
 *
 * <p>Al ser una interfaz en {@code shared/domain/}, los use cases pueden
 * inyectarla sin importar nada de Spring — el dominio permanece puro.</p>
 *
 * <p>La implementación concreta {@code SpringDomainEventPublisher} vive en
 * {@code shared/infrastructure/event/} y delega en
 * {@code ApplicationEventPublisher} de Spring.</p>
 *
 * <p>Uso en un use case:</p>
 * <pre>{@code
 * // inyectado via constructor (puerto — no la implementación)
 * private final DomainEventPublisher eventPublisher;
 *
 * eventPublisher.publish(AccountActivatedEvent.of(userId, dni, roleName));
 * }</pre>
 */
public interface DomainEventPublisher {

    /**
     * Publica un evento de dominio para que los listeners interesados reaccionen.
     *
     * @param event el evento ocurrido — debe ser inmutable
     */
    void publish(DomainEvent event);
}