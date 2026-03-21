package org.school.management.auth.application.dto.responses;

import java.util.UUID;

/**
 * Response de {@code CreateUserUseCase}.
 *
 * <p>Devuelve solo el {@code userId} — el orquestador de cada BC ya tiene
 * el DNI y la password porque los generó él mismo antes de llamar al use case.</p>
 *
 * @param userId UUID del {@code User} creado
 */
public record CreateUserResponse(UUID userId) {}