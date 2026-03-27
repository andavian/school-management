package org.school.management.auth.infra.security.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Filtro de rate limiting basado en Bucket4j (in-memory, sin Redis).
 *
 * <p>Aplica límites por IP para los endpoints de autenticación más sensibles:</p>
 * <ul>
 *   <li>{@code POST /api/auth/login} — evita fuerza bruta</li>
 *   <li>{@code POST /api/auth/activate-account} — evita intentos masivos</li>
 *   <li>{@code POST /api/auth/refresh-token} — evita abuso de renovación</li>
 * </ul>
 *
 * <p>Cuando se supera el límite responde {@code 429 Too Many Requests}
 * con {@code Retry-After} en segundos y cuerpo JSON compatible con
 * el formato {@code ErrorApiResponse} del proyecto.</p>
 *
 * <p>Los buckets se almacenan en {@link ConcurrentHashMap} — apropiado para
 * un único nodo. Si en el futuro se escala a múltiples instancias, migrar
 * a Bucket4j + Redis o Hazelcast.</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

    private static final String LOGIN_PATH           = "/api/auth/login";
    private static final String ACTIVATE_ACCOUNT_PATH = "/api/auth/activate-account";
    private static final String REFRESH_TOKEN_PATH   = "/api/auth/refresh-token";

    private final RateLimitProperties properties;

    // Un ConcurrentHashMap por endpoint: key = IP, value = Bucket
    private final Map<String, Bucket> loginBuckets           = new ConcurrentHashMap<>();
    private final Map<String, Bucket> activateAccountBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> refreshTokenBuckets    = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        if (!properties.isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        String path   = request.getRequestURI();
        String method = request.getMethod();

        // Solo aplicar a POST en los endpoints protegidos
        if (!"POST".equalsIgnoreCase(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        RateLimitProperties.EndpointLimit limit;
        Map<String, Bucket> bucketMap = null;

        if (LOGIN_PATH.equals(path)) {
            limit     = properties.getLogin();
            bucketMap = loginBuckets;
        } else if (ACTIVATE_ACCOUNT_PATH.equals(path)) {
            limit     = properties.getActivateAccount();
            bucketMap = activateAccountBuckets;
        } else if (REFRESH_TOKEN_PATH.equals(path)) {
            limit     = properties.getRefreshToken();
            bucketMap = refreshTokenBuckets;
        } else {
            limit = null;
        }

        if (limit == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String ip     = resolveClientIp(request);
        Bucket bucket = bucketMap.computeIfAbsent(ip, k -> buildBucket(limit));

        if (bucket.tryConsume(1)) {
            // Agregar header informativo con tokens restantes
            long remaining = bucket.getAvailableTokens();
            response.setHeader("X-RateLimit-Remaining", String.valueOf(remaining));
            filterChain.doFilter(request, response);
        } else {
            log.warn("Rate limit exceeded — IP: {}, path: {}", ip, path);
            rejectRequest(response, limit.getRefillSeconds());
        }
    }

    // ── helpers ───────────────────────────────────────────────────────────

    private Bucket buildBucket(RateLimitProperties.EndpointLimit limit) {
        Bandwidth bandwidth = Bandwidth.classic(
                limit.getCapacity(),
                Refill.intervally(
                        limit.getRefillTokens(),
                        Duration.ofSeconds(limit.getRefillSeconds())
                )
        );
        return Bucket.builder().addLimit(bandwidth).build();
    }

    /**
     * Resuelve la IP real del cliente respetando proxies/load balancers.
     * Usa {@code X-Forwarded-For} si está presente, caso contrario {@code remoteAddr}.
     */
    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            // X-Forwarded-For puede contener múltiples IPs — tomar la primera
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void rejectRequest(HttpServletResponse response, int retryAfterSeconds)
            throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));
        response.getWriter().write("""
                {
                  "success": false,
                  "message": "Demasiados intentos. Por favor esperá %d segundos antes de reintentar.",
                  "errorCode": "TOO_MANY_REQUESTS"
                }
                """.formatted(retryAfterSeconds));
    }
}