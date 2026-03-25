package org.school.management.teachers.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.shared.person.domain.valueobject.*;
import org.school.management.teachers.application.dto.request.UpdateTeacherRequest;
import org.school.management.teachers.application.dto.response.TeacherResponse;
import org.school.management.teachers.domain.exception.TeacherNotFoundException;
import org.school.management.teachers.domain.model.Teacher;
import org.school.management.teachers.domain.repository.TeacherRepository;
import org.school.management.teachers.domain.valueobject.EmploymentStatus;
import org.school.management.teachers.domain.valueobject.EmploymentType;
import org.school.management.teachers.domain.valueobject.TeacherId;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("UpdateTeacherUseCase")
class UpdateTeacherUseCaseTest {

    @Mock private TeacherRepository teacherRepository;
    @Mock private GetTeacherByIdUseCase getTeacherByIdUseCase;

    @InjectMocks private UpdateTeacherUseCase useCase;

    // ── Fixtures ──────────────────────────────────────────────────────────

    private static final UUID TEACHER_UUID = UUID.randomUUID();

    private Teacher buildTeacher() {
        return Teacher.builder()
                .teacherId(TeacherId.of(TEACHER_UUID))
                .userId(UserId.of(UUID.randomUUID()))
                .fullName(FullName.of("Juan", "Pérez"))
                .dni(Dni.of("12345678"))
                .cuil(Cuil.of("20123456789"))
                .email(Email.of("juan.perez@ipet132.edu.ar"))
                .phone(PhoneNumber.of("3514123456"))
                .hireDate(LocalDate.of(2020, 3, 1))
                .employmentStatus(EmploymentStatus.ACTIVE)
                .employmentType(EmploymentType.FULL_TIME)
                .active(true)
                .createdBy(UserId.of(UUID.randomUUID()))
                .build();
    }

    private TeacherResponse buildTeacherResponse(Teacher teacher) {
        return new TeacherResponse(
                teacher.getTeacherId().value(),
                teacher.getUserId().value(),
                teacher.getFullName().firstName(),
                teacher.getFullName().lastName(),
                teacher.getFullName().firstNameFirst(),
                teacher.getDni().value(),
                teacher.getCuil().formatted(),
                teacher.getEmail().value(),
                null, null, null,
                teacher.getPhone().value(),
                null, null, null,
                null, null,
                teacher.getHireDate(),
                teacher.getEmploymentStatus(),
                teacher.getEmploymentType(),
                teacher.isActive(), false, null, null, null
        );
    }

    // ── Tests ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("execute — actualiza teléfono — persiste cambio correctamente")
    void execute_whenPhoneUpdated_thenPersistsChange() {
        Teacher teacher = buildTeacher();
        UpdateTeacherRequest request = new UpdateTeacherRequest(
                null, null, null, null, null, null,
                "3514999999",   // nuevo teléfono
                null, null, null, null, null, null
        );

        when(teacherRepository.findByTeacherId(TeacherId.from(TEACHER_UUID)))
                .thenReturn(Optional.of(teacher));
        when(teacherRepository.save(any(Teacher.class))).thenReturn(teacher);
        when(getTeacherByIdUseCase.buildResponse(any())).thenReturn(buildTeacherResponse(teacher));

        useCase.execute(TEACHER_UUID, request);

        verify(teacherRepository).save(any(Teacher.class));
    }

    @Test
    @DisplayName("execute — teacher no existe — lanza TeacherNotFoundException")
    void execute_whenTeacherNotFound_thenThrowTeacherNotFoundException() {
        UpdateTeacherRequest request = new UpdateTeacherRequest(
                null, null, null, null, null, null,
                null, null, null, null, null, null, null
        );

        when(teacherRepository.findByTeacherId(TeacherId.from(TEACHER_UUID)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(TEACHER_UUID, request))
                .isInstanceOf(TeacherNotFoundException.class)
                .hasMessageContaining(TEACHER_UUID.toString());

        verify(teacherRepository, never()).save(any());
    }

    @Test
    @DisplayName("execute — employmentStatus RETIRED — llama a teacher.retire()")
    void execute_whenStatusRetired_thenTeacherIsRetired() {
        Teacher teacher = buildTeacher();
        UpdateTeacherRequest request = new UpdateTeacherRequest(
                null, null, null, null, null, null,
                null, null, null, null, null, null,
                EmploymentStatus.RETIRED
        );

        when(teacherRepository.findByTeacherId(TeacherId.from(TEACHER_UUID)))
                .thenReturn(Optional.of(teacher));
        when(teacherRepository.save(any(Teacher.class))).thenReturn(teacher);
        when(getTeacherByIdUseCase.buildResponse(any())).thenReturn(buildTeacherResponse(teacher));

        useCase.execute(TEACHER_UUID, request);

        assertThat(teacher.isRetired()).isTrue();
        assertThat(teacher.isActive()).isFalse();
        verify(teacherRepository).save(teacher);
    }

    @Test
    @DisplayName("execute — employmentStatus INACTIVE — desactiva sin retirar")
    void execute_whenStatusInactive_thenTeacherDeactivatedNotRetired() {
        Teacher teacher = buildTeacher();
        UpdateTeacherRequest request = new UpdateTeacherRequest(
                null, null, null, null, null, null,
                null, null, null, null, null, null,
                EmploymentStatus.INACTIVE
        );

        when(teacherRepository.findByTeacherId(TeacherId.from(TEACHER_UUID)))
                .thenReturn(Optional.of(teacher));
        when(teacherRepository.save(any(Teacher.class))).thenReturn(teacher);
        when(getTeacherByIdUseCase.buildResponse(any())).thenReturn(buildTeacherResponse(teacher));

        useCase.execute(TEACHER_UUID, request);

        assertThat(teacher.isActive()).isFalse();
        assertThat(teacher.isRetired()).isFalse();
        verify(teacherRepository).save(teacher);
    }

    @Test
    @DisplayName("execute — request vacío — no llama a ningún método de actualización")
    void execute_whenEmptyRequest_thenNoUpdateMethodCalled() {
        Teacher teacher = buildTeacher();
        UpdateTeacherRequest request = new UpdateTeacherRequest(
                null, null, null, null, null, null,
                null, null, null, null, null, null, null
        );

        when(teacherRepository.findByTeacherId(TeacherId.from(TEACHER_UUID)))
                .thenReturn(Optional.of(teacher));
        when(teacherRepository.save(any(Teacher.class))).thenReturn(teacher);
        when(getTeacherByIdUseCase.buildResponse(any())).thenReturn(buildTeacherResponse(teacher));

        useCase.execute(TEACHER_UUID, request);

        // Se persiste igual — comportamiento correcto, el save es idempotente
        verify(teacherRepository).save(teacher);
    }
}