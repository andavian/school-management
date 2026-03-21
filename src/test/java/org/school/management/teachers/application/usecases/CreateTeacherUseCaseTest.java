package org.school.management.teachers.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.auth.application.dto.responses.CreateTeacherResponse;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.shared.domain.service.EmailService;
import org.school.management.shared.person.domain.valueobject.*;
import org.school.management.teachers.application.dto.request.CreateTeacherRequest;
import org.school.management.teachers.application.dto.response.TeacherResponse;
import org.school.management.teachers.domain.exception.TeacherAlreadyExistsException;
import org.school.management.teachers.domain.model.Teacher;
import org.school.management.teachers.domain.repository.TeacherRepository;
import org.school.management.teachers.domain.valueobject.EmploymentStatus;
import org.school.management.teachers.domain.valueobject.EmploymentType;
import org.school.management.teachers.domain.valueobject.TeacherId;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("CreateTeacherUseCase")
class CreateTeacherUseCaseTest {

    @Mock private TeacherRepository teacherRepository;
    @Mock private org.school.management.auth.application.usecases.CreateTeacherUseCase authCreateTeacherUseCase;
    @Mock private GetTeacherByIdUseCase getTeacherByIdUseCase;
    @Mock private EmailService emailService;

    @InjectMocks private CreateTeacherUseCase useCase;

    // ── Fixtures ──────────────────────────────────────────────────────────

    private static final UUID CREATED_BY = UUID.randomUUID();
    private static final UUID USER_UUID  = UUID.randomUUID();
    private static final UUID TEACHER_UUID = UUID.randomUUID();

    private CreateTeacherRequest buildRequest() {
        return new CreateTeacherRequest(
                "María", "González",
                "87654321",
                "27876543210",
                "maria.gonzalez@ipet132.edu.ar",
                LocalDate.of(1985, 6, 15),
                null, "FEMALE", "Argentina",
                "3514987654",
                null,
                "Matemática", null,
                LocalDate.of(2023, 3, 1),
                EmploymentType.FULL_TIME
        );
    }

    private Teacher buildSavedTeacher(CreateTeacherRequest req) {
        return Teacher.builder()
                .teacherId(TeacherId.of(TEACHER_UUID))
                .userId(UserId.of(USER_UUID))
                .fullName(FullName.of(req.firstName(), req.lastName()))
                .dni(Dni.of(req.dni()))
                .cuil(Cuil.of(req.cuil()))
                .email(Email.of(req.email()))
                .phone(PhoneNumber.of(req.phone()))
                .hireDate(req.hireDate())
                .employmentStatus(EmploymentStatus.ACTIVE)
                .employmentType(req.employmentType())
                .active(false)
                .createdBy(UserId.of(CREATED_BY))
                .build();
    }

    private TeacherResponse buildTeacherResponse() {
        return new TeacherResponse(
                TEACHER_UUID, USER_UUID,
                "María", "González", "María González",
                "87654321", "27-87654321-0",
                "maria.gonzalez@ipet132.edu.ar",
                LocalDate.of(1985, 6, 15), "FEMALE", "Argentina",
                "3514987654", null, null, null,
                "Matemática", null,
                LocalDate.of(2023, 3, 1),
                EmploymentStatus.ACTIVE, EmploymentType.FULL_TIME,
                false, false, null, null, null
        );
    }

    // ── Tests ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("execute — flujo feliz — crea teacher y envía email")
    void execute_happyPath_createsTeacherAndSendsEmail() {
        CreateTeacherRequest request = buildRequest();
        Teacher saved = buildSavedTeacher(request);
        TeacherResponse response = buildTeacherResponse();

        when(teacherRepository.existsByDni(Dni.of(request.dni()))).thenReturn(false);
        when(teacherRepository.existsByCuil(request.cuil())).thenReturn(false);
        when(authCreateTeacherUseCase.execute(any()))
                .thenReturn(new CreateTeacherResponse(
                        USER_UUID.toString(), request.dni(), "TempPass1!", true
                ));
        when(teacherRepository.save(any(Teacher.class))).thenReturn(saved);
        when(getTeacherByIdUseCase.buildResponse(saved)).thenReturn(response);

        TeacherResponse result = useCase.execute(request, CREATED_BY);

        assertThat(result).isNotNull();
        assertThat(result.dni()).isEqualTo("87654321");
        assertThat(result.employmentStatus()).isEqualTo(EmploymentStatus.ACTIVE);

        verify(teacherRepository).save(any(Teacher.class));
        verify(emailService).sendTeacherInvitation(
                eq(request.email()),
                eq(request.firstName()),
                eq(request.lastName()),
                eq(request.dni()),
                eq("TempPass1!"),
                any()
        );
    }

    @Test
    @DisplayName("execute — DNI duplicado — lanza TeacherAlreadyExistsException")
    void execute_whenDniExists_thenThrowTeacherAlreadyExistsException() {
        CreateTeacherRequest request = buildRequest();

        when(teacherRepository.existsByDni(Dni.of(request.dni()))).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(request, CREATED_BY))
                .isInstanceOf(TeacherAlreadyExistsException.class)
                .hasMessageContaining(request.dni());

        verifyNoInteractions(authCreateTeacherUseCase, emailService);
        verify(teacherRepository, never()).save(any());
    }

    @Test
    @DisplayName("execute — CUIL duplicado — lanza TeacherAlreadyExistsException")
    void execute_whenCuilExists_thenThrowTeacherAlreadyExistsException() {
        CreateTeacherRequest request = buildRequest();

        when(teacherRepository.existsByDni(Dni.of(request.dni()))).thenReturn(false);
        when(teacherRepository.existsByCuil(request.cuil())).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(request, CREATED_BY))
                .isInstanceOf(TeacherAlreadyExistsException.class)
                .hasMessageContaining(request.cuil());

        verifyNoInteractions(authCreateTeacherUseCase, emailService);
        verify(teacherRepository, never()).save(any());
    }

    @Test
    @DisplayName("execute — fallo de email — no interrumpe la creación del teacher")
    void execute_whenEmailFails_thenTeacherIsStillCreated() {
        CreateTeacherRequest request = buildRequest();
        Teacher saved = buildSavedTeacher(request);
        TeacherResponse response = buildTeacherResponse();

        when(teacherRepository.existsByDni(Dni.of(request.dni()))).thenReturn(false);
        when(teacherRepository.existsByCuil(request.cuil())).thenReturn(false);
        when(authCreateTeacherUseCase.execute(any()))
                .thenReturn(new CreateTeacherResponse(
                        USER_UUID.toString(), request.dni(), "TempPass1!", false
                ));
        when(teacherRepository.save(any(Teacher.class))).thenReturn(saved);
        when(getTeacherByIdUseCase.buildResponse(saved)).thenReturn(response);

        // Email falla silenciosamente — @Async + catch en JavaMailEmailService
        doNothing().when(emailService).sendTeacherInvitation(any(), any(), any(), any(), any(), any());

        TeacherResponse result = useCase.execute(request, CREATED_BY);

        // El teacher igual se creó
        assertThat(result).isNotNull();
        verify(teacherRepository).save(any(Teacher.class));
    }
}
