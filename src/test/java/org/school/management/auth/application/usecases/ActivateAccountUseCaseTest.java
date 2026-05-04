package org.school.management.auth.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.auth.application.dto.requests.ActivateAccountRequest;
import org.school.management.auth.application.dto.responses.ActivateAccountResponse;
import org.school.management.auth.domain.exception.InvalidTokenException;
import org.school.management.auth.domain.exception.UserNotFoundException;
import org.school.management.auth.domain.model.ConfirmationToken;
import org.school.management.auth.domain.model.Role;
import org.school.management.auth.domain.model.User;
import org.school.management.auth.domain.repository.ConfirmationTokenRepository;
import org.school.management.auth.domain.repository.UserRepository;
import org.school.management.auth.domain.valueobject.ConfirmationTokenId;
import org.school.management.auth.domain.valueobject.HashedPassword;
import org.school.management.auth.domain.valueobject.RoleName;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.auth.infra.security.token.TokenHasher;
import org.school.management.shared.domain.event.AccountActivatedEvent;
import org.school.management.shared.domain.event.DomainEventPublisher;
import org.school.management.shared.person.domain.valueobject.Dni;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para {@link ActivateAccountUseCase}.
 *
 * <p>El flujo real usa tokens opacos persistidos en {@code confirmation_codes} —
 * NO JWT. El token raw llega por email, el backend lo hashea y busca en BD.</p>
 *
 * <p>Flujo completo:</p>
 * <ol>
 *   <li>Frontend envía {@code rawToken} + {@code newPassword}</li>
 *   <li>{@link TokenHasher#hash(String)} genera el hash del token raw</li>
 *   <li>{@link ConfirmationTokenRepository#findByTokenHash(String)} busca en BD</li>
 *   <li>Valida que no esté usado ni expirado</li>
 *   <li>Activa el {@link User} y cambia la contraseña</li>
 *   <li>Marca el token como usado</li>
 *   <li>Publica {@link AccountActivatedEvent}</li>
 * </ol>
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("ActivateAccountUseCase")
class ActivateAccountUseCaseTest {

    @Mock private UserRepository             userRepository;
    @Mock private ConfirmationTokenRepository tokenRepository;
    @Mock private TokenHasher                tokenHasher;
    @Mock private HashedPassword.PasswordEncoder passwordEncoder;
    @Mock private DomainEventPublisher       eventPublisher;

    @InjectMocks private ActivateAccountUseCase useCase;

    // ── constantes ────────────────────────────────────────────────────────

    private static final String RAW_TOKEN    = "raw-opaque-token-from-email";
    private static final String TOKEN_HASH   = "sha256-hash-of-raw-token";
    private static final String DNI          = "20345676";
    private static final String NEW_PASSWORD = "NuevaPass123!";

    // ── helpers ───────────────────────────────────────────────────────────

    /**
     * Construye un ConfirmationToken válido (no usado, no expirado).
     */
    private ConfirmationToken buildValidToken() {
        return ConfirmationToken.builder()
                .id(ConfirmationTokenId.generate())
                .userDni(Dni.of(DNI))
                .tokenHash(TOKEN_HASH)
                .createdAt(LocalDateTime.now().minusHours(1))
                .expiresAt(LocalDateTime.now().plusHours(47))
                .build();
    }

    /**
     * Construye un ConfirmationToken ya utilizado.
     */
    private ConfirmationToken buildUsedToken() {
        ConfirmationToken token = buildValidToken();
        token.markAsUsed();
        return token;
    }

    /**
     * Construye un ConfirmationToken expirado.
     */
    private ConfirmationToken buildExpiredToken() {
        return ConfirmationToken.builder()
                .id(ConfirmationTokenId.generate())
                .userDni(Dni.of(DNI))
                .tokenHash(TOKEN_HASH)
                .createdAt(LocalDateTime.now().minusHours(50))
                .expiresAt(LocalDateTime.now().minusHours(2))  // ya expiró
                .build();
    }

    /**
     * Construye un User inactivo (estado inicial de un teacher).
     */
    private User buildInactiveUser(String roleName) {
        Role role = mock(Role.class);
        RoleName rn = mock(RoleName.class);
        when(role.getName()).thenReturn(rn);
        when(rn.name()).thenReturn(roleName);

        return User.reconstruct(
                UserId.generate(),
                Dni.of(DNI),
                HashedPassword.of("$2a$10$hashedTemporaryPassword"),
                Set.of(role),
                false,          // inactivo — pendiente de activación
                LocalDateTime.now().minusDays(1),
                null,
                LocalDateTime.now().minusDays(1)
        );
    }

    // ── tests — flujo feliz ───────────────────────────────────────────────

    @Test
    @DisplayName("happy path — activa usuario, marca token usado y publica evento")
    void execute_happyPath_activatesUserMarksTokenAndPublishesEvent() {
        // given
        ConfirmationToken token = buildValidToken();
        User user               = buildInactiveUser("TEACHER");

        when(tokenHasher.hash(RAW_TOKEN)).thenReturn(TOKEN_HASH);
        when(tokenRepository.findByTokenHash(TOKEN_HASH)).thenReturn(Optional.of(token));
        when(userRepository.findByDni(Dni.of(DNI))).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(NEW_PASSWORD)).thenReturn("$2a$10$newHashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // when
        ActivateAccountResponse response = useCase.execute(
                new ActivateAccountRequest(RAW_TOKEN, NEW_PASSWORD));

        // then
        assertThat(response.success()).isTrue();
        assertThat(user.getActive()).isTrue();
        assertThat(token.isUsed()).isTrue();

        verify(userRepository).save(user);
        verify(eventPublisher).publish(any(AccountActivatedEvent.class));
    }

    @Test
    @DisplayName("happy path — el hash del token raw se calcula antes de buscar en BD")
    void execute_hashIsComputedBeforeLookup() {
        // given
        ConfirmationToken token = buildValidToken();
        User user               = buildInactiveUser("TEACHER");

        when(tokenHasher.hash(RAW_TOKEN)).thenReturn(TOKEN_HASH);
        when(tokenRepository.findByTokenHash(TOKEN_HASH)).thenReturn(Optional.of(token));
        when(userRepository.findByDni(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(any())).thenReturn("$2a$10$newHash");
        when(userRepository.save(any())).thenReturn(user);

        // when
        useCase.execute(new ActivateAccountRequest(RAW_TOKEN, NEW_PASSWORD));

        // then — orden de llamadas: primero hash, luego lookup
        var inOrder = inOrder(tokenHasher, tokenRepository);
        inOrder.verify(tokenHasher).hash(RAW_TOKEN);
        inOrder.verify(tokenRepository).findByTokenHash(TOKEN_HASH);
    }

    // ── tests — token inválido ────────────────────────────────────────────

    @Test
    @DisplayName("token no encontrado en BD → lanza InvalidTokenException")
    void execute_tokenNotFound_throwsInvalidTokenException() {
        // given
        when(tokenHasher.hash(RAW_TOKEN)).thenReturn(TOKEN_HASH);
        when(tokenRepository.findByTokenHash(TOKEN_HASH)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() ->
                useCase.execute(new ActivateAccountRequest(RAW_TOKEN, NEW_PASSWORD))
        ).isInstanceOf(InvalidTokenException.class);

        verifyNoInteractions(userRepository, eventPublisher);
    }

    @Test
    @DisplayName("token ya utilizado → lanza InvalidTokenException")
    void execute_tokenAlreadyUsed_throwsInvalidTokenException() {
        // given
        ConfirmationToken usedToken = buildUsedToken();

        when(tokenHasher.hash(RAW_TOKEN)).thenReturn(TOKEN_HASH);
        when(tokenRepository.findByTokenHash(TOKEN_HASH)).thenReturn(Optional.of(usedToken));

        // when / then
        assertThatThrownBy(() ->
                useCase.execute(new ActivateAccountRequest(RAW_TOKEN, NEW_PASSWORD))
        ).isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("utilizado");

        verifyNoInteractions(userRepository, eventPublisher);
    }

    @Test
    @DisplayName("token expirado → lanza InvalidTokenException")
    void execute_tokenExpired_throwsInvalidTokenException() {
        // given
        ConfirmationToken expiredToken = buildExpiredToken();

        when(tokenHasher.hash(RAW_TOKEN)).thenReturn(TOKEN_HASH);
        when(tokenRepository.findByTokenHash(TOKEN_HASH)).thenReturn(Optional.of(expiredToken));

        // when / then
        assertThatThrownBy(() ->
                useCase.execute(new ActivateAccountRequest(RAW_TOKEN, NEW_PASSWORD))
        ).isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("expirado");

        verifyNoInteractions(userRepository, eventPublisher);
    }

    // ── tests — usuario no encontrado ─────────────────────────────────────

    @Test
    @DisplayName("token válido pero usuario no existe → lanza UserNotFoundException")
    void execute_validTokenUserNotFound_throwsUserNotFoundException() {
        // given
        ConfirmationToken token = buildValidToken();

        when(tokenHasher.hash(RAW_TOKEN)).thenReturn(TOKEN_HASH);
        when(tokenRepository.findByTokenHash(TOKEN_HASH)).thenReturn(Optional.of(token));
        when(userRepository.findByDni(Dni.of(DNI))).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() ->
                useCase.execute(new ActivateAccountRequest(RAW_TOKEN, NEW_PASSWORD))
        ).isInstanceOf(UserNotFoundException.class);

        verifyNoInteractions(eventPublisher);
    }

    // ── tests — evento publicado ──────────────────────────────────────────

    @Test
    @DisplayName("evento publicado contiene userId, dni y roleName correctos")
    void execute_publishedEventContainsCorrectData() {
        // given
        ConfirmationToken token = buildValidToken();
        User user               = buildInactiveUser("TEACHER");

        when(tokenHasher.hash(RAW_TOKEN)).thenReturn(TOKEN_HASH);
        when(tokenRepository.findByTokenHash(TOKEN_HASH)).thenReturn(Optional.of(token));
        when(userRepository.findByDni(Dni.of(DNI))).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(any())).thenReturn("$2a$10$newHash");
        when(userRepository.save(any())).thenReturn(user);

        // when
        useCase.execute(new ActivateAccountRequest(RAW_TOKEN, NEW_PASSWORD));

        // then
        ArgumentCaptor<AccountActivatedEvent> captor =
                ArgumentCaptor.forClass(AccountActivatedEvent.class);
        verify(eventPublisher).publish(captor.capture());

        AccountActivatedEvent event = captor.getValue();
        assertThat(event.userId()).isEqualTo(user.getUserId().value());
        assertThat(event.dni()).isEqualTo(DNI);
        assertThat(event.roleName()).isEqualTo("TEACHER");
        assertThat(event.eventId()).isNotNull();
        assertThat(event.occurredOn()).isNotNull();
    }

    @Test
    @DisplayName("rol PARENT también publica evento correctamente")
    void execute_parentRole_publishesEventWithCorrectRole() {
        // given
        ConfirmationToken token = buildValidToken();
        User user               = buildInactiveUser("PARENT");

        when(tokenHasher.hash(RAW_TOKEN)).thenReturn(TOKEN_HASH);
        when(tokenRepository.findByTokenHash(TOKEN_HASH)).thenReturn(Optional.of(token));
        when(userRepository.findByDni(Dni.of(DNI))).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(any())).thenReturn("$2a$10$newHash");
        when(userRepository.save(any())).thenReturn(user);

        // when
        useCase.execute(new ActivateAccountRequest(RAW_TOKEN, NEW_PASSWORD));

        // then
        ArgumentCaptor<AccountActivatedEvent> captor =
                ArgumentCaptor.forClass(AccountActivatedEvent.class);
        verify(eventPublisher).publish(captor.capture());
        assertThat(captor.getValue().roleName()).isEqualTo("PARENT");
    }

    // ── tests — invariantes arquitectónicas ──────────────────────────────

    @Test
    @DisplayName("use case NO depende de JwtTokenProvider")
    void useCase_doesNotDependOnJwtTokenProvider() {
        var fields = ActivateAccountUseCase.class.getDeclaredFields();
        boolean hasJwtProvider = java.util.Arrays.stream(fields)
                .anyMatch(f -> f.getType().getSimpleName().contains("JwtTokenProvider"));

        assertThat(hasJwtProvider)
                .as("ActivateAccountUseCase no debe depender de JwtTokenProvider — " +
                        "usa tokens opacos persistidos en BD")
                .isFalse();
    }

    @Test
    @DisplayName("use case NO depende de repositorios de otros BCs (teachers, students)")
    void useCase_doesNotDependOnOtherBcRepositories() {
        var fields = ActivateAccountUseCase.class.getDeclaredFields();
        boolean hasForeignRepo = java.util.Arrays.stream(fields)
                .anyMatch(f -> f.getType().getSimpleName().contains("Teacher")
                        || f.getType().getSimpleName().contains("Student")
                        || f.getType().getSimpleName().contains("Parent"));

        assertThat(hasForeignRepo)
                .as("ActivateAccountUseCase no debe depender de repositorios de otros BCs — " +
                        "usa eventos de dominio para notificar")
                .isFalse();
    }
}