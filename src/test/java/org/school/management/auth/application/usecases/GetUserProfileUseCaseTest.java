package org.school.management.auth.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.auth.application.dto.responses.UserResponse;
import org.school.management.auth.application.mappers.AuthApplicationMapper;
import org.school.management.auth.domain.exception.UserNotFoundException;
import org.school.management.auth.domain.model.Role;
import org.school.management.auth.domain.model.User;
import org.school.management.auth.domain.repository.UserRepository;
import org.school.management.auth.domain.valueobject.HashedPassword;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("GetUserProfileUseCase")
class GetUserProfileUseCaseTest {

    @Mock private UserRepository userRepository;
    @Mock private AuthApplicationMapper mapper;

    @InjectMocks private GetUserProfileUseCase useCase;

    // ── constantes ────────────────────────────────────────────────────────

    private static final String USER_ID_STR = "a1b2c3d4-e5f6-7890-abcd-ef1234567890";
    private static final String DNI = "20345676";

    // ── helpers ───────────────────────────────────────────────────────────

    private User buildUser() {
        Role role = Role.reconstruct(RoleId.generate(), RoleName.of("TEACHER"), LocalDateTime.now().minusDays(30));
        return User.reconstruct(
                UserId.from(USER_ID_STR),
                Dni.of(DNI),
                HashedPassword.of("$2a$10$hashedPassword"),
                Set.of(role),
                true,
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1)
        );
    }

    private UserResponse buildUserResponse(User user) {
        return new UserResponse(
                user.getUserId().asString(),
                DNI,
                Set.of("TEACHER"),
                true,
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1)
        );
    }

    // ── tests — flujo feliz ───────────────────────────────────────────────

    @Test
    @DisplayName("execute — flujo feliz — devuelve perfil de usuario")
    void execute_happyPath_returnsUserProfile() {
        // given
        User user = buildUser();
        UserResponse userResponse = buildUserResponse(user);

        when(userRepository.findById(any(UserId.class))).thenReturn(Optional.of(user));
        when(mapper.toUserResponse(user)).thenReturn(userResponse);

        // when
        UserResponse result = useCase.execute(USER_ID_STR);

        // then
        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(USER_ID_STR);
        assertThat(result.dni()).isEqualTo(DNI);
        assertThat(result.roles()).contains("TEACHER");
        assertThat(result.active()).isTrue();

        verify(userRepository).findById(any(UserId.class));
        verify(mapper).toUserResponse(user);
    }

    // ── tests — usuario no encontrado ─────────────────────────────────────

    @Test
    @DisplayName("execute — usuario no encontrado — lanza UserNotFoundException")
    void execute_userNotFound_throwsUserNotFoundException() {
        // given
        when(userRepository.findById(any(UserId.class))).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> useCase.execute(USER_ID_STR))
                .isInstanceOf(UserNotFoundException.class);

        verifyNoInteractions(mapper);
    }
}
