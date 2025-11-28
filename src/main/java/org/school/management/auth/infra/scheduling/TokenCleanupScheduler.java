/*
package org.school.management.auth.infra.scheduling;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.domain.repository.BlacklistedTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenCleanupScheduler {

    private final BlacklistedTokenRepository blacklistedTokenRepository;

    // Ejecutar cada 6 horas
    @Scheduled(fixedRate = 21600000) // 6 horas en milisegundos
    public void cleanupExpiredTokens() {
        log.info("Iniciando limpieza de tokens expirados");

        try {
            blacklistedTokenRepository.deleteExpiredTokens();
            log.info("Limpieza de tokens expirados completada");
        } catch (Exception e) {
            log.error("Error durante la limpieza de tokens: {}", e.getMessage(), e);
        }
    }
}*/
