package org.school.management.teachers.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.geography.application.usecases.GetPlaceByIdUseCase;
import org.school.management.shared.person.domain.valueobject.*;
import org.school.management.teachers.application.dto.response.TeacherResponse;
import org.school.management.teachers.application.mapper.TeacherApplicationMapper;
import org.school.management.teachers.domain.exception.TeacherNotFoundException;
import org.school.management.teachers.domain.model.Teacher;
import org.school.management.teachers.domain.repository.TeacherRepository;
import org.school.management.teachers.domain.valueobject.EmploymentStatus;
import org.school.management.teachers.domain.valueobject.EmploymentType;
import org.school.management.teachers.domain.valueobject.TeacherId;
import org.school.management.auth.domain.valueobject.UserId;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("GetTeacherByIdUseCase")
class GetTeacherByIdUseCaseTest {

    @Mock private TeacherRepository teacherRepository;
    @Mock private TeacherApplicationMapper mapper;
    @Mock private GetPlaceByIdUseCase getPlaceByIdUseCase;

    @InjectMocks private GetTeacherByIdUseCase useCase;

    // ── Fixtures ──────────────────────────────────────────────────────────

    private static final UUID TEACHER_UUID = UUID.randomUUID();
    private static final UUID USER_UUID    = UUID.randomUUID();

    private Teacher buildTeacher() {
        return Teacher.builder()
                .teacherId(TeacherId.of(TEACHER_UUID))
                .userId(UserId.of(USER_UUID))
                .fullName(FullName.of("Juan", "Pérez"))
                .dni(Dni.of("12345678"))
                .cuil(Cuil.of("20123456789"))
                .email(Email.of("juan.perez@ipet132.edu.ar"))
                .phone(PhoneNumber.of("3514123456"))
                .hireDate(LocalDate.of(2020, 3, 1))
                .employmentStatus(EmploymentStatus.ACTIVE)
                .employmentType(EmploymentType.FULL_TIME)
                .active(false)
                .createdBy(UserId.of(UUID.randomUUID()))
                .build();
    }

    private TeacherResponse buildTeacherResponse() {
        return new TeacherResponse(
                TEACHER_UUID, USER_UUID,
                "Juan", "Pérez", "Juan Pérez",
                "12345678", "20-12345678-9",
                "juan.perez@ipet132.edu.ar",
                null, null, "Argentina",
                "3514123456", null, null, null,
                null, null,
                LocalDate.of(2020, 3, 1),
                EmploymentStatus.ACTIVE, EmploymentType.FULL_TIME,
                false, false, null, null, null
        );
    }

    // ── Tests ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("execute — cuando el profesor existe — retorna TeacherResponse")
    void execute_whenTeacherExists_thenReturnResponse() {
        Teacher teacher = buildTeacher();
        TeacherResponse expected = buildTeacherResponse();

        when(teacherRepository.findByTeacherId(TeacherId.from(TEACHER_UUID)))
                .thenReturn(Optional.of(teacher));
        when(mapper.toTeacherResponse(eq(teacher), any(), any()))
                .thenReturn(expected);

        TeacherResponse result = useCase.execute(TEACHER_UUID);

        assertThat(result).isNotNull();
        assertThat(result.teacherId()).isEqualTo(TEACHER_UUID);
        assertThat(result.fullName()).isEqualTo("Juan Pérez");
        verify(teacherRepository).findByTeacherId(TeacherId.from(TEACHER_UUID));
    }

    @Test
    @DisplayName("execute — cuando el profesor no existe — lanza TeacherNotFoundException")
    void execute_whenTeacherNotFound_thenThrowTeacherNotFoundException() {
        when(teacherRepository.findByTeacherId(TeacherId.from(TEACHER_UUID)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(TEACHER_UUID))
                .isInstanceOf(TeacherNotFoundException.class)
                .hasMessageContaining(TEACHER_UUID.toString());

        verify(teacherRepository).findByTeacherId(TeacherId.from(TEACHER_UUID));
        verifyNoInteractions(mapper);
    }

    @Test
    @DisplayName("execute — cuando birthPlaceId es null — no invoca GetPlaceByIdUseCase")
    void execute_whenBirthPlaceIdIsNull_thenDoNotResolveBirthPlace() {
        Teacher teacher = buildTeacher(); // birthPlaceId null por defecto
        when(teacherRepository.findByTeacherId(TeacherId.from(TEACHER_UUID)))
                .thenReturn(Optional.of(teacher));
        when(mapper.toTeacherResponse(eq(teacher), isNull(), isNull()))
                .thenReturn(buildTeacherResponse());

        useCase.execute(TEACHER_UUID);

        verifyNoInteractions(getPlaceByIdUseCase);
    }
}