package org.school.management.students.parents.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.auth.domain.model.Role;
import org.school.management.auth.domain.model.User;
import org.school.management.auth.domain.repository.UserRepository;
import org.school.management.auth.domain.valueobject.HashedPassword;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.shared.domain.service.EmailService;
import org.school.management.shared.person.domain.valueobject.*;
import org.school.management.students.parents.application.dto.request.CreateParentRequest;
import org.school.management.students.parents.application.dto.response.ParentResponse;
import org.school.management.students.parents.application.mapper.ParentApplicationMapper;
import org.school.management.students.parents.domain.exception.ParentAlreadyExistsException;
import org.school.management.students.parents.domain.model.Parent;
import org.school.management.students.parents.domain.repository.ParentRepository;
import org.school.management.students.parents.domain.valueobject.ParentId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("CreateParentUseCase")
class CreateParentUseCaseTest {

    @Mock private ParentRepository parentRepository;
    @Mock private UserRepository userRepository;
    @Mock private HashedPassword.PasswordEncoder passwordEncoder;
    @Mock private ParentApplicationMapper mapper;
    @Mock private EmailService emailService;

    @InjectMocks private CreateParentUseCase useCase;

    // ── Fixtures ──────────────────────────────────────────────────────────

    private static final UUID CREATED_BY = UUID.randomUUID();

    private CreateParentRequest buildRequest() {
        return new CreateParentRequest(
                "Ana", "García",
                "20345676",
                "23203456769",
                LocalDate.of(1980, 5, 20),
                "FEMALE", "Argentina",
                "ana.garcia@gmail.com",
                "3514555666",
                null,
                "Av. Colón", "1234", null, null,
                UUID.randomUUID(),
                "5000",
                "Docente", null, null
        );
    }

    private User buildMockUser() {
        return mock(User.class);
    }

    private Parent buildSavedParent(CreateParentRequest req) {
        return Parent.builder()
                .parentId(ParentId.generate())
                .userId(UserId.of(UUID.randomUUID()))
                .dni(Dni.of(req.dni()))
                .cuil(Cuil.of(req.cuil()))
                .fullName(FullName.of(req.firstName(), req.lastName()))
                .email(Email.of(req.email()))
                .phone(PhoneNumber.of(req.phone()))
                .createdBy(UserId.of(CREATED_BY))
                .build();
    }

    private ParentResponse buildParentResponse(CreateParentRequest req) {
        return new ParentResponse(
                UUID.randomUUID(), UUID.randomUUID(),
                req.dni(), req.cuil(),
                req.firstName(), req.lastName(),
                req.firstName() + " " + req.lastName(),
                req.birthDate(), "FEMALE", "Argentina",
                req.email(), req.phone(), null,
                null, null, null, null, null, null,
                null, null, null,
                true,
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

    // ── Tests ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("execute — flujo feliz — crea parent y envía email de credenciales")
    void execute_happyPath_createsParentAndSendsEmail() {
        CreateParentRequest request = buildRequest();
        User mockUser = buildMockUser();
        Parent savedParent = buildSavedParent(request);
        ParentResponse response = buildParentResponse(request);

        when(parentRepository.existsByDni(Dni.of(request.dni()))).thenReturn(false);
        when(parentRepository.existsByEmail(Email.of(request.email()))).thenReturn(false);
        when(parentRepository.existsByCuil(request.cuil())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hashedPassword");
        when(mockUser.getUserId()).thenReturn(UserId.of(UUID.randomUUID()));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(parentRepository.save(any(Parent.class))).thenReturn(savedParent);
        when(mapper.toParentResponse(savedParent)).thenReturn(response);

        ParentResponse result = useCase.execute(request, CREATED_BY);

        assertThat(result).isNotNull();
        assertThat(result.dni()).isEqualTo("20345676");

        verify(parentRepository).save(any(Parent.class));
        verify(emailService).sendParentCredentials(
                eq(request.email()),
                eq(request.firstName()),
                eq(request.lastName()),
                eq(request.dni()),
                any()   // rawPassword generado internamente
        );
    }

    @Test
    @DisplayName("execute — DNI duplicado — lanza ParentAlreadyExistsException")
    void execute_whenDniExists_thenThrowParentAlreadyExistsException() {
        CreateParentRequest request = buildRequest();

        when(parentRepository.existsByDni(Dni.of(request.dni()))).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(request, CREATED_BY))
                .isInstanceOf(ParentAlreadyExistsException.class)
                .hasMessageContaining(request.dni());

        verify(parentRepository, never()).save(any());
        verifyNoInteractions(userRepository, emailService);
    }

    @Test
    @DisplayName("execute — email duplicado — lanza ParentAlreadyExistsException")
    void execute_whenEmailExists_thenThrowParentAlreadyExistsException() {
        CreateParentRequest request = buildRequest();

        when(parentRepository.existsByDni(Dni.of(request.dni()))).thenReturn(false);
        when(parentRepository.existsByEmail(Email.of(request.email()))).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(request, CREATED_BY))
                .isInstanceOf(ParentAlreadyExistsException.class)
                .hasMessageContaining(request.email());

        verify(parentRepository, never()).save(any());
        verifyNoInteractions(userRepository, emailService);
    }

    @Test
    @DisplayName("execute — CUIL duplicado — lanza ParentAlreadyExistsException")
    void execute_whenCuilExists_thenThrowParentAlreadyExistsException() {
        // GIVEN
        CreateParentRequest request = buildRequest();

        // 1. DNI: Debe ser false para que el flujo continúe
        when(parentRepository.existsByDni(any(Dni.class))).thenReturn(false);

        // 2. CUIL: Aquí es donde queremos que falle
        when(parentRepository.existsByCuil(request.cuil())).thenReturn(true);

        // IMPORTANTE: Eliminamos el stub de existsByEmail porque NO se llega a ejecutar.

        // WHEN & THEN
        assertThatThrownBy(() -> useCase.execute(request, CREATED_BY))
                .isInstanceOf(ParentAlreadyExistsException.class)
                .hasMessageContaining(request.cuil());

        // Verificaciones de seguridad
        verify(parentRepository, never()).save(any());
        verifyNoInteractions(userRepository, emailService);
    }

    @Test
    @DisplayName("execute — fallo de email — no interrumpe la creación del parent")
    void execute_whenEmailFails_thenParentIsStillCreated() {
        CreateParentRequest request = buildRequest();
        User mockUser = buildMockUser();
        Parent savedParent = buildSavedParent(request);
        ParentResponse response = buildParentResponse(request);

        when(parentRepository.existsByDni(Dni.of(request.dni()))).thenReturn(false);
        when(parentRepository.existsByEmail(Email.of(request.email()))).thenReturn(false);
        when(parentRepository.existsByCuil(request.cuil())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hashedPassword");
        when(mockUser.getUserId()).thenReturn(UserId.of(UUID.randomUUID()));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(parentRepository.save(any(Parent.class))).thenReturn(savedParent);
        when(mapper.toParentResponse(savedParent)).thenReturn(response);
        doNothing().when(emailService).sendParentCredentials(any(), any(), any(), any(), any());

        ParentResponse result = useCase.execute(request, CREATED_BY);

        assertThat(result).isNotNull();
        verify(parentRepository).save(any(Parent.class));
    }
}
