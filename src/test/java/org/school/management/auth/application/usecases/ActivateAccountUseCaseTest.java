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
import org.school.management.auth.domain.model.Role;
import org.school.management.auth.domain.model.User;
import org.school.management.auth.domain.repository.UserRepository;
import org.school.management.auth.domain.valueobject.HashedPassword;
import org.school.management.auth.domain.valueobject.RoleName;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.auth.infra.security.JwtTokenProvider;
import org.school.management.shared.domain.event.AccountActivatedEvent;
import org.school.management.shared.domain.event.DomainEventPublisher;
import org.school.management.shared.person.domain.valueobject.Dni;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("ActivateAccountUseCase")
class ActivateAccountUseCaseTest {

    @Mock private UserRepository            userRepository;
    @Mock private JwtTokenProvider          jwtTokenProvider;
    @Mock private HashedPassword.PasswordEncoder passwordEncoder;
    @Mock private DomainEventPublisher      eventPublisher;

    @InjectMocks private ActivateAccountUseCase useCase;

    // ─── helpers ────────────────────────────────────────────────────────────

    private static final String VALID_TOKEN   = "valid.jwt.token";
    private static final String DNI           = "20345676"; // DNI válido — dígito verificador correcto
    private static final String NEW_PASSWORD  = "NuevaPass123!";

    private User buildUser(String roleName) {
        Role role = mock(Role.class);
        RoleName rn = mock(RoleName.class);
        when(role.getName()).thenReturn(rn);
        when(rn.name()).thenReturn(roleName);

        return User.reconstruct(
                UserId.generate(),
                Dni.of(DNI),
                HashedPassword.of("$hashed$"),
                Set.of(role),
                false,
                LocalDateTime.now(),
                null,
                LocalDateTime.now()
        );
    }

    // ─── tests ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("token inválido → lanza InvalidTokenException sin tocar repositorio")
    void invalidToken_throwsInvalidTokenException() {
        when(jwtTokenProvider.isConfirmationTokenValid(VALID_TOKEN)).thenReturn(false);

        assertThatThrownBy(() ->
                useCase.execute(new ActivateAccountRequest(VALID_TOKEN, NEW_PASSWORD))
        ).isInstanceOf(InvalidTokenException.class);

        verifyNoInteractions(userRepository, eventPublisher);
    }

    @Test
    @DisplayName("token válido pero usuario no encontrado → lanza UserNotFoundException")
    void validToken_userNotFound_throwsUserNotFoundException() {
        when(jwtTokenProvider.isConfirmationTokenValid(VALID_TOKEN)).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromToken(VALID_TOKEN)).thenReturn(DNI);
        when(userRepository.findByDni(Dni.of(DNI))).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                useCase.execute(new ActivateAccountRequest(VALID_TOKEN, NEW_PASSWORD))
        ).isInstanceOf(UserNotFoundException.class);

        verifyNoInteractions(eventPublisher);
    }

    @Test
    @DisplayName("happy path → activa User, guarda, publica evento y retorna success")
    void happyPath_activatesUserSavesAndPublishesEvent() {
        User user = buildUser("ROLE_TEACHER");

        when(jwtTokenProvider.isConfirmationTokenValid(VALID_TOKEN)).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromToken(VALID_TOKEN)).thenReturn(DNI);
        when(userRepository.findByDni(any(Dni.class))).thenReturn(Optional.of(user));

        when(passwordEncoder.encode(NEW_PASSWORD)).thenReturn("any-hashed-string");
        when(userRepository.save(any(User.class))).thenReturn(user);

        ActivateAccountResponse response =
                useCase.execute(new ActivateAccountRequest(VALID_TOKEN, NEW_PASSWORD));

        assertThat(response.success()).isTrue();
        verify(userRepository).save(user);
        verify(eventPublisher).publish(any(AccountActivatedEvent.class));
    }

    @Test
    @DisplayName("evento publicado contiene userId, dni y roleName correctos")
    void publishedEvent_containsCorrectData() {
        User user = buildUser("ROLE_TEACHER");

        when(jwtTokenProvider.isConfirmationTokenValid(VALID_TOKEN)).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromToken(VALID_TOKEN)).thenReturn(DNI);
        when(userRepository.findByDni(Dni.of(DNI))).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(NEW_PASSWORD)).thenReturn("any-hashed-string");
        when(userRepository.save(any(User.class))).thenReturn(user);

        useCase.execute(new ActivateAccountRequest(VALID_TOKEN, NEW_PASSWORD));

        ArgumentCaptor<AccountActivatedEvent> captor =
                ArgumentCaptor.forClass(AccountActivatedEvent.class);
        verify(eventPublisher).publish(captor.capture());

        AccountActivatedEvent event = captor.getValue();
        assertThat(event.userId()).isEqualTo(user.getUserId().value());
        assertThat(event.dni()).isEqualTo(DNI);
        assertThat(event.roleName()).isEqualTo("ROLE_TEACHER");
        assertThat(event.eventId()).isNotNull();
        assertThat(event.occurredOn()).isNotNull();
    }

    @Test
    @DisplayName("el use case NO inyecta ni accede a TeacherRepository directamente")
    void useCase_doesNotDependOnTeacherRepository() {
        // Verifica la invariante arquitectónica: auth/ no conoce teachers/
        // Si el use case tuviese TeacherRepository como campo, este test fallaría
        // en compilación porque el @InjectMocks no lo encontraría aquí.
        // Lo validamos inspeccionando los campos declarados del use case.
        var fields = ActivateAccountUseCase.class.getDeclaredFields();
        boolean hasTeacherRepo = java.util.Arrays.stream(fields)
                .anyMatch(f -> f.getType().getSimpleName().contains("Teacher"));

        assertThat(hasTeacherRepo)
                .as("ActivateAccountUseCase no debe depender de ningún repositorio de teachers")
                .isFalse();
    }
}