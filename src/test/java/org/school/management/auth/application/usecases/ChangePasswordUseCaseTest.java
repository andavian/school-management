package org.school.management.auth.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.auth.application.dto.requests.ChangePasswordRequest;
import org.school.management.auth.application.dto.responses.ChangePasswordResponse;
import org.school.management.auth.application.mappers.AuthApplicationMapper;
import org.school.management.auth.domain.exception.InvalidPasswordException;
import org.school.management.auth.domain.exception.UserNotFoundException;
import org.school.management.auth.domain.model.Role;
import org.school.management.auth.domain.model.User;
import org.school.management.auth.domain.repository.UserRepository;
import org.school.management.auth.domain.valueobject.HashedPassword;
import org.school.management.auth.domain.valueobject.PlainPassword;
import org.school.management.auth.domain.valueobject.RoleId;
import org.school.management.auth.domain.valueobject.RoleName;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.shared.person.domain.valueobject.Dni;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("ChangePasswordUseCase")
class ChangePasswordUseCaseTest {

    @Mock private UserRepository userRepository;
    @Mock private AuthApplicationMapper mapper;
    @Mock private HashedPassword.PasswordEncoder passwordEncoder;
    @Mock private BlacklistTokenUseCase blacklistTokenUseCase;

    @InjectMocks private ChangePasswordUseCase useCase;

    // ── constantes ────────────────────────────────────────────────────────

    private static final String USER_ID_STR = "a1b2c3d4-e5f6-7890-abcd-ef1234567890";
    private static final String DNI = "20345676";
    private static final String CURRENT_PASSWORD = "OldPass123!";
    private static final String NEW_PASSWORD = "NewPass456!";
    private static final String OLD_HASH = "$2a$10$oldHashedPassword";
    private static final String NEW_HASH = "$2a$10$newHashedPassword";

    // ── helpers ───────────────────────────────────────────────────────────

    private User buildUser() {
        Role role = Role.reconstruct(RoleId.generate(), RoleName.of("TEACHER"), LocalDateTime.now().minusDays(30));
        return User.reconstruct(
                UserId.from(USER_ID_STR),
                Dni.of(DNI),
                HashedPassword.of(OLD_HASH),
                Set.of(role),
                true,
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1)
        );
    }

    private ChangePasswordRequest buildRequest() {
        return new ChangePasswordRequest(USER_ID_STR, CURRENT_PASSWORD, NEW_PASSWORD);
    }

    // ── tests — flujo feliz ───────────────────────────────────────────────

    @Test
    @DisplayName("execute — flujo feliz — cambia password correctamente")
    void execute_happyPath_changesPasswordSuccessfully() {
        // given
        ChangePasswordRequest request = buildRequest();
        User user = buildUser();

        when(mapper.toUserId(USER_ID_STR)).thenReturn(UserId.from(USER_ID_STR));
        when(userRepository.findById(any(UserId.class))).thenReturn(Optional.of(user));
        when(mapper.toPlainPassword(CURRENT_PASSWORD)).thenReturn(PlainPassword.of(CURRENT_PASSWORD));
        when(mapper.toPlainPassword(NEW_PASSWORD)).thenReturn(PlainPassword.of(NEW_PASSWORD));
        when(passwordEncoder.matches(eq(CURRENT_PASSWORD), eq(OLD_HASH))).thenReturn(true);
        when(passwordEncoder.encode(eq(NEW_PASSWORD))).thenReturn(NEW_HASH);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // when
        ChangePasswordResponse result = useCase.execute(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.success()).isTrue();
        assertThat(result.message()).contains("exitosamente");

        verify(userRepository).findById(any(UserId.class));
        verify(userRepository).save(user);
    }

    // ── tests — usuario no encontrado ─────────────────────────────────────

    @Test
    @DisplayName("execute — usuario no encontrado — lanza UserNotFoundException")
    void execute_userNotFound_throwsUserNotFoundException() {
        // given
        ChangePasswordRequest request = buildRequest();

        when(mapper.toUserId(USER_ID_STR)).thenReturn(UserId.from(USER_ID_STR));
        when(userRepository.findById(any(UserId.class))).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository, never()).save(any());
        verifyNoInteractions(blacklistTokenUseCase);
    }

    // ── tests — password actual incorrecto ────────────────────────────────

    @Test
    @DisplayName("execute — password actual incorrecto — lanza InvalidPasswordException")
    void execute_wrongCurrentPassword_throwsInvalidPasswordException() {
        // given
        ChangePasswordRequest request = buildRequest();
        User user = buildUser();

        when(mapper.toUserId(USER_ID_STR)).thenReturn(UserId.from(USER_ID_STR));
        when(userRepository.findById(any(UserId.class))).thenReturn(Optional.of(user));
        when(mapper.toPlainPassword(CURRENT_PASSWORD)).thenReturn(PlainPassword.of(CURRENT_PASSWORD));
        when(mapper.toPlainPassword(NEW_PASSWORD)).thenReturn(PlainPassword.of(NEW_PASSWORD));
        when(passwordEncoder.matches(eq(CURRENT_PASSWORD), eq(OLD_HASH))).thenReturn(false);

        // when / then
        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessageContaining("incorrect");

        verify(userRepository, never()).save(any());
    }
}
