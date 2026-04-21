package org.school.management.auth.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.domain.exception.UserNotFoundException;
import org.school.management.auth.domain.model.ConfirmationToken;
import org.school.management.auth.domain.repository.ConfirmationTokenRepository;
import org.school.management.auth.domain.repository.UserRepository;
import org.school.management.auth.infra.security.token.SecureTokenGenerator;
import org.school.management.auth.infra.security.token.TokenHasher;
import org.school.management.shared.person.domain.valueobject.Dni;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenerateConfirmationTokenUseCase {

    private final UserRepository userRepository;
    private final ConfirmationTokenRepository tokenRepository;
    private final SecureTokenGenerator tokenGenerator;
    private final TokenHasher tokenHasher;

    private final Duration TTL = Duration.ofHours(48);

    @Transactional
    public String execute(String dni) {

        var user = userRepository.findByDni(Dni.of(dni))
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String rawToken = tokenGenerator.generate();
        String hash = tokenHasher.hash(rawToken);

        ConfirmationToken token = ConfirmationToken.create(
                user.getDni(),
                hash,
                TTL
        );

        tokenRepository.save(token);

        log.debug("Confirmation token generated for DNI: {}", dni);

        return rawToken; // 🔥 ESTE es el que va por email
    }
}