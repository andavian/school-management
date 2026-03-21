package org.school.management.auth.application.usecases;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.application.dto.requests.ActivateAccountRequest;
import org.school.management.auth.application.dto.responses.ActivateAccountResponse;
import org.school.management.auth.domain.exception.InvalidTokenException;
import org.school.management.auth.domain.exception.UserNotFoundException;
import org.school.management.auth.domain.model.User;
import org.school.management.auth.domain.repository.UserRepository;
import org.school.management.auth.domain.valueobject.HashedPassword;
import org.school.management.auth.domain.valueobject.PlainPassword;
import org.school.management.auth.infra.security.JwtTokenProvider;
import org.school.management.shared.domain.event.AccountActivatedEvent;
import org.school.management.shared.domain.event.DomainEventPublisher;
import org.school.management.shared.person.domain.valueobject.Dni;
import org.springframework.stereotype.Service;

/**
 * Caso de uso: activar la cuenta de un usuario a partir de un token JWT de confirmación.
 *
 * <p>Responsabilidad exclusiva de {@code auth/}: validar el token, cambiar la password
 * y activar el {@link User}. No conoce ni toca entidades de otros bounded contexts.</p>
 *
 * <p>Una vez activado el {@link User}, publica {@link AccountActivatedEvent} para que
 * cada BC interesado reaccione de forma independiente:</p>
 * <ul>
 *   <li>{@code teachers/TeacherAccountActivatedListener} — activa la entidad {@code Teacher}</li>
 *   <li>Futuros listeners para parents u otros roles — sin modificar este use case</li>
 * </ul>
 *
 * <p>Los listeners usan {@code @TransactionalEventListener(phase = BEFORE_COMMIT)}
 * para garantizar atomicidad — si alguno falla, toda la transacción se revierte.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ActivateAccountUseCase {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final HashedPassword.PasswordEncoder passwordEncoder;
    private final DomainEventPublisher eventPublisher;

    @Transactional
    public ActivateAccountResponse execute(ActivateAccountRequest request) {

        // 1. Validar token de confirmación
        if (!jwtTokenProvider.isConfirmationTokenValid(request.token())) {
            throw new InvalidTokenException("Token de activación inválido o expirado");
        }

        // 2. Obtener DNI del token y buscar el User
        String dni = jwtTokenProvider.getUsernameFromToken(request.token());
        User user = userRepository.findByDni(Dni.of(dni))
                .orElseThrow(() -> new UserNotFoundException(
                        "Usuario no encontrado para el token recibido"));

        // 3. Cambiar password y activar el User
        user.resetPassword(PlainPassword.of(request.newPassword()), passwordEncoder);
        user.activate();
        userRepository.save(user);

        // 4. Publicar evento — cada BC reacciona de forma independiente
        String roleName = user.getRoles().stream()
                .findFirst()
                .map(role -> role.getName().value())
                .orElse("UNKNOWN");

        eventPublisher.publish(
                AccountActivatedEvent.of(user.getUserId().value(), dni, roleName)
        );

        log.info("Account activated successfully — DNI: {}, role: {}", dni, roleName);

        return new ActivateAccountResponse(true, "Cuenta activada exitosamente");
    }
}