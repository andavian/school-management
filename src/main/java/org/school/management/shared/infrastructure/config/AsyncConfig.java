package org.school.management.shared.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AsyncConfig {
    // @Async habilitado — JavaMailEmailService.sendEmail() no bloquea
    // el hilo transaccional principal
}