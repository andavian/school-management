package org.school.management.auth.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.auth.domain.exception.UserNotFoundException;
import org.school.management.auth.domain.model.ConfirmationToken;
import org.school.management.auth.domain.model.Role;
import org.school.management.auth.domain.model.User;
import org.school.management.auth.domain.repository.ConfirmationTokenRepository;
import org.school.management.auth.domain.repository.UserRepository;
import org.school.management.auth.domain.valueobject.HashedPassword;
import org.school.management.auth.domain.valueobject.RoleId;
import org.school.management.auth.domain.valueobject.RoleName;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.auth.infra.security.token.SecureTokenGenerator;
import org.school.management.auth.infra.security.token.TokenHasher;
import org.school.management.shared.person.domain.valueobject.Dni;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("GenerateConfirmationTokenUseCase")
class GenerateConfirmationTokenUseCaseTest {

    @Mock private UserRepository userRepository;
    @Mock private ConfirmationTokenRepository tokenRepository;
    @Mock private SecureTokenGenerator tokenGenerator;
    @Mock private TokenHasher tokenHasher;

    @InjectMocks private GenerateConfirmationTokenUseCase useCase;

    // ── constantes ────────────────────────────────────────────────────────

    private static final String DNI = "20345676";
    private static final String RAW_TOKEN = "raw-confirmation-token";
    private static final String TOKEN_HASH = "sha256-hash-of-confirmation-token";

    // ── helpers ───────────────────────────────────────────────────────────

    private User buildUser() {
        Role role = Role.reconstruct(RoleId.generate(), RoleName.of("TEACHER"), LocalDateTime.now().minusDays(30));
        return User.reconstruct(
                UserId.generate(),
                Dni.of(DNI),
                HashedPassword.of("$2a$10$hashedPassword"),
                Set.of(role),
                false,
                LocalDateTime.now().minusDays(1),
                null,
                LocalDateTime.now().minusDays(1)
        );
    }

    // ── tests — flujo feliz ───────────────────────────────────────────────

    @Test
    @DisplayName("execute — flujo feliz — genera token, lo hashea y lo persiste")
    void execute_happyPath_generatesTokenHashesAndPersists() {
        // given
        User user = buildUser();

        when(userRepository.findByDni(any(Dni.class))).thenReturn(Optional.of(user));
        when(tokenGenerator.generate()).thenReturn(RAW_TOKEN);
        when(tokenHasher.hash(RAW_TOKEN)).thenReturn(TOKEN_HASH);

        // when
        String result = useCase.execute(DNI);

        // then
        assertThat(result).isEqualTo(RAW_TOKEN);

        verify(userRepository).findByDni(any(Dni.class));
        verify(tokenGenerator).generate();
        verify(tokenHasher).hash(RAW_TOKEN);
        verify(tokenRepository).save(any(ConfirmationToken.class));
    }

    @Test
    @DisplayName("execute — flujo feliz — el token guardado tiene TTL de 48 horas")
    void execute_happyPath_tokenHas48HourTtl() {
        // given
        User user = buildUser();

        when(userRepository.findByDni(any(Dni.class))).thenReturn(Optional.of(user));
        when(tokenGenerator.generate()).thenReturn(RAW_TOKEN);
        when(tokenHasher.hash(RAW_TOKEN)).thenReturn(TOKEN_HASH);

        // when
        useCase.execute(DNI);

        // then
        verify(tokenRepository).save(argThat(token ->
                token.getUserDni().equals(Dni.of(DNI))
                        && TOKEN_HASH.equals(token.getTokenHash())
                        && token.getExpiresAt().isAfter(token.getCreatedAt())
        ));
    }

    // ── tests — usuario no encontrado ─────────────────────────────────────

    @Test
    @DisplayName("execute — usuario no encontrado — lanza UserNotFoundException")
    void execute_userNotFound_throwsUserNotFoundException() {
        // given
        when(userRepository.findByDni(any(Dni.class))).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> useCase.execute(DNI))
                .isInstanceOf(UserNotFoundException.class);

        verifyNoInteractions(tokenGenerator, tokenHasher, tokenRepository);
    }
}
