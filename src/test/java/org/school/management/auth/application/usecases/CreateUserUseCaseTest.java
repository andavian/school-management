package org.school.management.auth.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.auth.application.dto.requests.CreateUserRequest;
import org.school.management.auth.application.dto.responses.CreateUserResponse;
import org.school.management.auth.domain.exception.DniAlreadyExistsException;
import org.school.management.auth.domain.model.Role;
import org.school.management.auth.domain.model.User;
import org.school.management.auth.domain.repository.RoleRepository;
import org.school.management.auth.domain.repository.UserRepository;
import org.school.management.auth.domain.valueobject.HashedPassword;
import org.school.management.auth.domain.valueobject.RoleId;
import org.school.management.auth.domain.valueobject.RoleName;
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
@DisplayName("CreateUserUseCase")
class CreateUserUseCaseTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private HashedPassword.PasswordEncoder passwordEncoder;

    @InjectMocks private CreateUserUseCase useCase;

    // ── constantes ────────────────────────────────────────────────────────

    private static final String DNI = "20345676";
    private static final String RAW_PASSWORD = "TempPass123!";
    private static final String HASHED_PASSWORD = "$2a$10$hashedPasswordValue";

    // ── helpers ───────────────────────────────────────────────────────────

    private Role buildRole(String roleName) {
        return Role.reconstruct(RoleId.generate(), RoleName.of(roleName), LocalDateTime.now().minusDays(30));
    }

    // ── tests — flujo feliz ───────────────────────────────────────────────

    @Test
    @DisplayName("execute — flujo feliz activo — crea usuario con active=true")
    void execute_activeUser_createsUserWithActiveTrue() {
        // given
        CreateUserRequest request = CreateUserRequest.active(DNI, RAW_PASSWORD, "TEACHER");
        Role role = buildRole("TEACHER");

        when(userRepository.existsByDni(any(Dni.class))).thenReturn(false);
        when(roleRepository.findByName(any(RoleName.class))).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(eq(RAW_PASSWORD))).thenReturn(HASHED_PASSWORD);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        CreateUserResponse result = useCase.execute(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.userId()).isNotNull();

        verify(userRepository).existsByDni(any(Dni.class));
        verify(roleRepository).findByName(any(RoleName.class));
        verify(passwordEncoder).encode(RAW_PASSWORD);
        verify(userRepository).save(argThat(user -> user.getActive()));
    }

    @Test
    @DisplayName("execute — flujo feliz inactivo — crea usuario con active=false")
    void execute_inactiveUser_createsUserWithActiveFalse() {
        // given
        CreateUserRequest request = CreateUserRequest.inactive(DNI, RAW_PASSWORD, "TEACHER");
        Role role = buildRole("TEACHER");

        when(userRepository.existsByDni(any(Dni.class))).thenReturn(false);
        when(roleRepository.findByName(any(RoleName.class))).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(eq(RAW_PASSWORD))).thenReturn(HASHED_PASSWORD);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        CreateUserResponse result = useCase.execute(request);

        // then
        assertThat(result).isNotNull();

        verify(userRepository).save(argThat(user -> !user.getActive()));
    }

    @Test
    @DisplayName("execute — flujo feliz — asigna el rol correcto al usuario")
    void execute_happyPath_assignsCorrectRole() {
        // given
        CreateUserRequest request = CreateUserRequest.active(DNI, RAW_PASSWORD, "STUDENT");
        Role role = buildRole("STUDENT");

        when(userRepository.existsByDni(any(Dni.class))).thenReturn(false);
        when(roleRepository.findByName(any(RoleName.class))).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(eq(RAW_PASSWORD))).thenReturn(HASHED_PASSWORD);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        CreateUserResponse result = useCase.execute(request);

        // then
        assertThat(result).isNotNull();

        verify(userRepository).save(argThat(user ->
                user.getRoles().stream().anyMatch(r -> r.getName().name().equals("STUDENT"))
        ));
    }

    // ── tests — DNI duplicado ─────────────────────────────────────────────

    @Test
    @DisplayName("execute — DNI ya existe — lanza DniAlreadyExistsException")
    void execute_dniAlreadyExists_throwsDniAlreadyExistsException() {
        // given
        CreateUserRequest request = CreateUserRequest.active(DNI, RAW_PASSWORD, "TEACHER");

        when(userRepository.existsByDni(any(Dni.class))).thenReturn(true);

        // when / then
        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(DniAlreadyExistsException.class)
                .hasMessageContaining(DNI);

        verifyNoInteractions(roleRepository, passwordEncoder);
        verify(userRepository, never()).save(any());
    }

    // ── tests — rol no encontrado ─────────────────────────────────────────

    @Test
    @DisplayName("execute — rol no encontrado — lanza IllegalStateException")
    void execute_roleNotFound_throwsIllegalStateException() {
        // given
        CreateUserRequest request = CreateUserRequest.active(DNI, RAW_PASSWORD, "ADMIN");

        when(userRepository.existsByDni(any(Dni.class))).thenReturn(false);
        when(roleRepository.findByName(any(RoleName.class))).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Rol no encontrado");

        verifyNoInteractions(passwordEncoder);
        verify(userRepository, never()).save(any());
    }

    // ── tests — factory methods ───────────────────────────────────────────

    @Test
    @DisplayName("CreateUserRequest.active → startActive=true")
    void factory_active_setsStartActiveTrue() {
        CreateUserRequest request = CreateUserRequest.active(DNI, RAW_PASSWORD, "TEACHER");
        assertThat(request.startActive()).isTrue();
    }

    @Test
    @DisplayName("CreateUserRequest.inactive → startActive=false")
    void factory_inactive_setsStartActiveFalse() {
        CreateUserRequest request = CreateUserRequest.inactive(DNI, RAW_PASSWORD, "TEACHER");
        assertThat(request.startActive()).isFalse();
    }
}
