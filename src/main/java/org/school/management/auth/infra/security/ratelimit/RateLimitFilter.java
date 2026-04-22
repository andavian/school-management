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
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

    private static final String LOGIN_PATH            = "/api/auth/login";
    private static final String ACTIVATE_ACCOUNT_PATH = "/api/auth/activate-account";
    private static final String REFRESH_TOKEN_PATH    = "/api/auth/refresh-token";

    private static final long BUCKET_TTL_MS = 30 * 60 * 1000; // 30 minutos

    private final RateLimitProperties properties;

    private final Map<String, BucketWrapper> loginBuckets           = new ConcurrentHashMap<>();
    private final Map<String, BucketWrapper> activateAccountBuckets = new ConcurrentHashMap<>();
    private final Map<String, BucketWrapper> refreshTokenBuckets    = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        if (!properties.isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String path = request.getRequestURI();
        String ip   = resolveClientIp(request);

        RateLimitProperties.EndpointLimit limit;
        Map<String, BucketWrapper> bucketMap;
        String key;

        if (LOGIN_PATH.equals(path)) {
            limit     = properties.getLogin();
            bucketMap = loginBuckets;

            String dni = request.getParameter("dni"); // si viene en JSON no aplica (ver nota abajo)
            key = ip + ":" + (dni != null ? dni : "unknown");

        } else if (ACTIVATE_ACCOUNT_PATH.equals(path)) {
            limit     = properties.getActivateAccount();
            bucketMap = activateAccountBuckets;
            key = ip;

        } else if (REFRESH_TOKEN_PATH.equals(path)) {
            limit     = properties.getRefreshToken();
            bucketMap = refreshTokenBuckets;

            String token = request.getParameter("refreshToken");
            key = ip + ":" + (token != null ? token.hashCode() : "unknown");

        } else {
            filterChain.doFilter(request, response);
            return;
        }

        Bucket bucket = resolveBucket(bucketMap, key, limit);

        if (bucket.tryConsume(1)) {
            response.setHeader("X-RateLimit-Remaining",
                    String.valueOf(bucket.getAvailableTokens()));

            filterChain.doFilter(request, response);
        } else {
            log.warn("Rate limit exceeded — key={} path={}", key, path);
            rejectRequest(response, request, limit.getRefillSeconds());
        }
    }

    // ─────────────────────────────────────────────

    private Bucket resolveBucket(Map<String, BucketWrapper> map,
                                 String key,
                                 RateLimitProperties.EndpointLimit limit) {

        long now = System.currentTimeMillis();

        BucketWrapper wrapper = map.compute(key, (k, existing) -> {

            if (existing == null || (now - existing.lastAccess) > BUCKET_TTL_MS) {
                return new BucketWrapper(buildBucket(limit), now);
            }

            existing.lastAccess = now;
            return existing;
        });

        return wrapper.bucket;
    }

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

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void rejectRequest(HttpServletResponse response,
                               HttpServletRequest request,
                               int retryAfterSeconds) throws IOException {

        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));

        String body = """
        {
          "success": false,
          "message": "Demasiados intentos. Esperá %d segundos antes de reintentar.",
          "errorCode": "TOO_MANY_REQUESTS",
          "timestamp": "%s",
          "path": "%s",
          "errors": []
        }
        """.formatted(
                retryAfterSeconds,
                LocalDateTime.now(),
                request.getRequestURI()
        );

        response.getWriter().write(body);
    }

    // ─────────────────────────────────────────────

    private static class BucketWrapper {
        Bucket bucket;
        long lastAccess;

        BucketWrapper(Bucket bucket, long lastAccess) {
            this.bucket = bucket;
            this.lastAccess = lastAccess;
        }
    }
}