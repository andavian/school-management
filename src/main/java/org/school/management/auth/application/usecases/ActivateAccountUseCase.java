package org.school.management.auth.application.usecases;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.application.dto.requests.ActivateAccountRequest;
import org.school.management.auth.application.dto.responses.ActivateAccountResponse;
import org.school.management.auth.domain.exception.InvalidTokenException;
import org.school.management.auth.domain.exception.UserNotFoundException;
import org.school.management.auth.domain.model.ConfirmationToken;
import org.school.management.auth.domain.model.User;
import org.school.management.auth.domain.repository.ConfirmationTokenRepository;
import org.school.management.auth.domain.repository.UserRepository;
import org.school.management.auth.domain.valueobject.HashedPassword;
import org.school.management.auth.domain.valueobject.PlainPassword;
import org.school.management.auth.infra.security.jwt.JwtTokenProvider;
import org.school.management.auth.infra.security.token.TokenHasher;
import org.school.management.shared.domain.event.AccountActivatedEvent;
import org.school.management.shared.domain.event.DomainEventPublisher;
import org.school.management.shared.person.domain.valueobject.Dni;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class ActivateAccountUseCase {

    private final UserRepository userRepository;
    private final ConfirmationTokenRepository tokenRepository;
    private final TokenHasher tokenHasher;
    private final HashedPassword.PasswordEncoder passwordEncoder;
    private final DomainEventPublisher eventPublisher;

    @Transactional
    public ActivateAccountResponse execute(ActivateAccountRequest request) {

        // 1. Hashear token recibido
        String tokenHash = tokenHasher.hash(request.token());

        // 2. Buscar token en BD
        ConfirmationToken token = tokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new InvalidTokenException("Token inválido"));

        // 3. Validaciones de seguridad
        if (token.isUsed()) {
            throw new InvalidTokenException("Token ya utilizado");
        }

        if (token.isExpired()) {
            throw new InvalidTokenException("Token expirado");
        }

        // 4. Buscar usuario
        String dni = token.getUserDni().value();

        User user = userRepository.findByDni(Dni.of(dni))
                .orElseThrow(() -> new UserNotFoundException(
                        "Usuario no encontrado para el token recibido"));

        // 5. Cambiar password y activar
        user.resetPassword(PlainPassword.of(request.newPassword()), passwordEncoder);
        user.activate();

        // 🔥 CRÍTICO: marcar token como usado
        token.markAsUsed();

        userRepository.save(user);

        // 6. Evento (igual que antes)
        String roleName = user.getRoles().stream()
                .findFirst()
                .map(role -> role.getName().name())
                .orElse("UNKNOWN");

        eventPublisher.publish(
                AccountActivatedEvent.of(user.getUserId().value(), dni, roleName)
        );

        log.info("Account activated successfully — DNI: {}, role: {}", dni, roleName);

        return new ActivateAccountResponse(true, "Cuenta activada exitosamente");
    }
}