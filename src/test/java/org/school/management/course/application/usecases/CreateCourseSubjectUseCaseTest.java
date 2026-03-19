package org.school.management.course.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.GradeLevelId;
import org.school.management.academic.domain.valueobject.ids.SubjectId;
import org.school.management.course.application.dto.request.CreateCourseSubjectRequest;
import org.school.management.course.application.dto.response.CourseSubjectResponse;
import org.school.management.course.application.mapper.CourseApplicationMapper;
import org.school.management.course.domain.exception.CourseSubjectAlreadyExistsException;
import org.school.management.course.domain.model.CourseSubject;
import org.school.management.course.domain.repository.CourseSubjectRepository;
import org.school.management.course.domain.valueobject.CourseStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("CreateCourseSubjectUseCase")
class CreateCourseSubjectUseCaseTest {

    @Mock private CourseSubjectRepository courseSubjectRepository;
    @Mock private CourseApplicationMapper mapper;

    @InjectMocks private CreateCourseSubjectUseCase useCase;

    // ── Fixtures ──────────────────────────────────────────────────────────

    private static final UUID GRADE_LEVEL_UUID   = UUID.randomUUID();
    private static final UUID SUBJECT_UUID       = UUID.randomUUID();
    private static final UUID ACADEMIC_YEAR_UUID = UUID.randomUUID();
    private static final UUID COURSE_SUBJECT_UUID = UUID.randomUUID();
    private static final UUID TEACHER_UUID       = UUID.randomUUID();

    private CreateCourseSubjectRequest buildRequest() {
        return new CreateCourseSubjectRequest(
                GRADE_LEVEL_UUID,
                SUBJECT_UUID,
                ACADEMIC_YEAR_UUID,
                null,           // sin docente inicialmente
                null,
                null
        );
    }

    private CreateCourseSubjectRequest buildRequestWithTeacher() {
        return new CreateCourseSubjectRequest(
                GRADE_LEVEL_UUID,
                SUBJECT_UUID,
                ACADEMIC_YEAR_UUID,
                TEACHER_UUID,
                "{\"monday\": \"08:00-10:00\"}",
                "Aula 12"
        );
    }

    private CourseSubject buildSavedCourseSubject() {
        return CourseSubject.builder()
                .courseSubjectId(
                        org.school.management.course.domain.valueobject.CourseSubjectId
                                .of(COURSE_SUBJECT_UUID))
                .gradeLevelId(GradeLevelId.from(GRADE_LEVEL_UUID))
                .subjectId(SubjectId.from(SUBJECT_UUID))
                .teacherId(null)
                .academicYearId(AcademicYearId.from(ACADEMIC_YEAR_UUID))
                .minPassingGrade(BigDecimal.valueOf(6.00))
                .status(CourseStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private CourseSubjectResponse buildCourseSubjectResponse() {
        return new CourseSubjectResponse(
                COURSE_SUBJECT_UUID,
                GRADE_LEVEL_UUID,
                SUBJECT_UUID,
                null,
                ACADEMIC_YEAR_UUID,
                null,
                null,
                BigDecimal.valueOf(6.00),
                CourseStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    // ── Tests ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("execute — flujo feliz sin docente — crea curso en estado ACTIVE")
    void execute_happyPath_withoutTeacher_createsCourseSubjectInActiveStatus() {
        CreateCourseSubjectRequest request = buildRequest();
        CourseSubject saved = buildSavedCourseSubject();
        CourseSubjectResponse response = buildCourseSubjectResponse();

        when(courseSubjectRepository.existsByGradeLevelAndSubjectAndYear(
                any(GradeLevelId.class), any(SubjectId.class), any(AcademicYearId.class)))
                .thenReturn(false);
        when(courseSubjectRepository.save(any(CourseSubject.class))).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(response);

        CourseSubjectResponse result = useCase.execute(request);

        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo(CourseStatus.ACTIVE);
        assertThat(result.teacherId()).isNull();
        verify(courseSubjectRepository).save(any(CourseSubject.class));
    }

    @Test
    @DisplayName("execute — flujo feliz con docente — persiste teacherId en el curso")
    void execute_happyPath_withTeacher_persistsTeacherId() {
        CreateCourseSubjectRequest request = buildRequestWithTeacher();
        CourseSubject savedWithTeacher = CourseSubject.builder()
                .courseSubjectId(
                        org.school.management.course.domain.valueobject.CourseSubjectId
                                .of(COURSE_SUBJECT_UUID))
                .gradeLevelId(GradeLevelId.from(GRADE_LEVEL_UUID))
                .subjectId(SubjectId.from(SUBJECT_UUID))
                .teacherId(org.school.management.teachers.domain.valueobject.TeacherId
                        .from(TEACHER_UUID))
                .academicYearId(AcademicYearId.from(ACADEMIC_YEAR_UUID))
                .minPassingGrade(BigDecimal.valueOf(6.00))
                .status(CourseStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        CourseSubjectResponse responseWithTeacher = new CourseSubjectResponse(
                COURSE_SUBJECT_UUID, GRADE_LEVEL_UUID, SUBJECT_UUID, TEACHER_UUID,
                ACADEMIC_YEAR_UUID, "{\"monday\": \"08:00-10:00\"}", "Aula 12",
                BigDecimal.valueOf(6.00), CourseStatus.ACTIVE,
                LocalDateTime.now(), LocalDateTime.now()
        );

        when(courseSubjectRepository.existsByGradeLevelAndSubjectAndYear(
                any(GradeLevelId.class), any(SubjectId.class), any(AcademicYearId.class)))
                .thenReturn(false);
        when(courseSubjectRepository.save(any(CourseSubject.class))).thenReturn(savedWithTeacher);
        when(mapper.toResponse(savedWithTeacher)).thenReturn(responseWithTeacher);

        CourseSubjectResponse result = useCase.execute(request);

        assertThat(result.teacherId()).isEqualTo(TEACHER_UUID);
        verify(courseSubjectRepository).save(any(CourseSubject.class));
    }

    @Test
    @DisplayName("execute — combinación ya existe — lanza CourseSubjectAlreadyExistsException")
    void execute_whenCourseSubjectAlreadyExists_thenThrowCourseSubjectAlreadyExistsException() {
        CreateCourseSubjectRequest request = buildRequest();

        when(courseSubjectRepository.existsByGradeLevelAndSubjectAndYear(
                any(GradeLevelId.class), any(SubjectId.class), any(AcademicYearId.class)))
                .thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(CourseSubjectAlreadyExistsException.class);

        verify(courseSubjectRepository, never()).save(any());
    }

    @Test
    @DisplayName("execute — combinación ya existe — no persiste nada")
    void execute_whenAlreadyExists_thenNothingIsPersisted() {
        CreateCourseSubjectRequest request = buildRequest();

        when(courseSubjectRepository.existsByGradeLevelAndSubjectAndYear(
                any(GradeLevelId.class), any(SubjectId.class), any(AcademicYearId.class)))
                .thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(CourseSubjectAlreadyExistsException.class);

        verify(courseSubjectRepository, never()).save(any());
        verifyNoMoreInteractions(courseSubjectRepository);
    }
}