package org.school.management.auth.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.domain.exception.UserNotFoundException;
import org.school.management.auth.domain.repository.UserRepository;
import org.school.management.auth.infra.security.JwtTokenProvider;
import org.school.management.auth.infra.security.UserPrincipal;
import org.school.management.shared.person.domain.valueobject.Dni;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso: generar un token JWT de confirmación para activación de cuenta.
 *
 * <p>El token tiene duración configurable via
 * {@code app.security.jwt.confirmation-token-expiration} (default 48h).</p>
 *
 * <p>Quién llama a este use case:</p>
 * <ul>
 *   <li>{@code teachers/CreateTeacherUseCase} — después de crear el User,
 *       genera el token para incluirlo en el email de invitación.</li>
 * </ul>
 *
 * <p>Vive en {@code auth/application/usecases/} porque {@link JwtTokenProvider}
 * es infraestructura de {@code auth/} — ningún otro BC debería accederlo directamente.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GenerateConfirmationTokenUseCase {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional(readOnly = true)
    public String execute(String dni) {
        var user = userRepository.findByDni(Dni.of(dni))
                .orElseThrow(() -> new UserNotFoundException(
                        "Usuario no encontrado para generar token — DNI: " + dni));

        String token = jwtTokenProvider.generateConfirmationToken(new UserPrincipal(user));

        log.debug("Confirmation token generated for DNI: {}", dni);

        return token;
    }
}