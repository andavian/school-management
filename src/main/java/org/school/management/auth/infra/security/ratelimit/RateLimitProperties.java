package org.school.management.auth.infra.security.ratelimit;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Propiedades de rate limiting leídas desde {@code application.yml}.
 *
 * <pre>
 * app:
 *   rate-limit:
 *     enabled: true
 *     login:
 *       capacity: 5
 *       refill-tokens: 5
 *       refill-seconds: 60
 * </pre>
 */
@Component
@ConfigurationProperties(prefix = "app.rate-limit")
@Getter
@Setter
public class RateLimitProperties {

    /** Desactivar globalmente — útil en tests de integración. */
    private boolean enabled = true;

    private EndpointLimit login           = new EndpointLimit(5,  5,  60);
    private EndpointLimit activateAccount = new EndpointLimit(3,  3,  60);
    private EndpointLimit refreshToken    = new EndpointLimit(10, 10, 60);

    @Getter
    @Setter
    public static class EndpointLimit {

        /** Capacidad máxima del bucket (= tokens iniciales). */
        private int capacity;

        /** Tokens recargados cada {@code refillSeconds}. */
        private int refillTokens;

        /** Intervalo de recarga en segundos. */
        private int refillSeconds;

        public EndpointLimit() {}

        public EndpointLimit(int capacity, int refillTokens, int refillSeconds) {
            this.capacity      = capacity;
            this.refillTokens  = refillTokens;
            this.refillSeconds = refillSeconds;
        }
    }
}