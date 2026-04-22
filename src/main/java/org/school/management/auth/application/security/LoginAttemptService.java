package org.school.management.auth.application.security;

import lombok.extern.slf4j.Slf4j;
import org.school.management.shared.person.domain.valueobject.Dni;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;

    private static final Duration BLOCK_TIME = Duration.ofMinutes(5);

    private static final long TTL_MS = 30 * 60 * 1000; // 30 min

    private final Map<String, Attempt> attempts = new ConcurrentHashMap<>();

    // ─────────────────────────────────────────────

    public void checkAttempts(Dni dni, String ip) {

        String key = buildKey(dni, ip);

        Attempt attempt = attempts.get(key);

        if (attempt == null) return;

        long now = System.currentTimeMillis();

        if (attempt.blockedUntil > now) {
            long secondsLeft = (attempt.blockedUntil - now) / 1000;

            log.warn("🚫 Login bloqueado | dni={} ip={} remaining={}s",
                    dni.value(), ip, secondsLeft);

            throw new RuntimeException(
                    "Demasiados intentos fallidos. Intentá nuevamente en " + secondsLeft + " segundos."
            );
        }
    }

    // ─────────────────────────────────────────────

    public void recordFailedAttempt(Dni dni, String ip) {

        String key = buildKey(dni, ip);

        Attempt attempt = attempts.computeIfAbsent(key, k -> new Attempt());

        attempt.failures++;

        if (attempt.failures >= MAX_ATTEMPTS) {
            attempt.blockedUntil = System.currentTimeMillis() + BLOCK_TIME.toMillis();

            log.warn("🚨 Usuario bloqueado temporalmente | dni={} ip={}",
                    dni.value(), ip);
        }

        attempt.lastUpdated = System.currentTimeMillis();
    }

    // ─────────────────────────────────────────────

    public void recordSuccess(Dni dni, String ip) {
        String key = buildKey(dni, ip);
        attempts.remove(key);
    }

    // ─────────────────────────────────────────────

    private String buildKey(Dni dni, String ip) {
        return dni.value() + ":" + ip;
    }

    // ─────────────────────────────────────────────
    // limpieza simple anti memory leak

    public void cleanup() {

        long now = System.currentTimeMillis();

        attempts.entrySet().removeIf(entry -> {
            Attempt a = entry.getValue();

            return (now - a.lastUpdated) > TTL_MS;
        });
    }

    // ─────────────────────────────────────────────

    private static class Attempt {
        int failures;
        long blockedUntil;
        long lastUpdated = System.currentTimeMillis();
    }
}