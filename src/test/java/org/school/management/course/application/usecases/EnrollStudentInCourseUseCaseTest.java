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
import org.school.management.course.application.dto.request.EnrollStudentRequest;
import org.school.management.course.application.dto.response.StudentCourseSubjectResponse;
import org.school.management.course.application.mapper.CourseApplicationMapper;
import org.school.management.course.domain.exception.CourseSubjectNotFoundException;
import org.school.management.course.domain.exception.StudentAlreadyEnrolledException;
import org.school.management.course.domain.model.CourseSubject;
import org.school.management.course.domain.model.StudentCourseSubject;
import org.school.management.course.domain.repository.CourseSubjectRepository;
import org.school.management.course.domain.repository.StudentCourseSubjectRepository;
import org.school.management.course.domain.valueobject.CourseSubjectId;
import org.school.management.course.domain.valueobject.StudentCourseSubjectId;
import org.school.management.course.domain.valueobject.SubjectEnrollmentStatus;
import org.school.management.course.domain.valueobject.CourseStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("EnrollStudentInCourseUseCase")
class EnrollStudentInCourseUseCaseTest {

    @Mock private CourseSubjectRepository courseSubjectRepository;
    @Mock private StudentCourseSubjectRepository studentCourseSubjectRepository;
    @Mock private CourseApplicationMapper mapper;

    @InjectMocks private EnrollStudentInCourseUseCase useCase;

    // ── Fixtures ──────────────────────────────────────────────────────────

    private static final UUID ENROLLMENT_UUID         = UUID.randomUUID();
    private static final UUID COURSE_SUBJECT_UUID     = UUID.randomUUID();
    private static final UUID STUDENT_CS_UUID         = UUID.randomUUID();

    private EnrollStudentRequest buildRequest() {
        return new EnrollStudentRequest(ENROLLMENT_UUID, COURSE_SUBJECT_UUID);
    }

    private CourseSubject buildActiveCourseSubject() {
        return CourseSubject.builder()
                .courseSubjectId(CourseSubjectId.of(COURSE_SUBJECT_UUID))
                .gradeLevelId(GradeLevelId.from(UUID.randomUUID()))
                .subjectId(SubjectId.from(UUID.randomUUID()))
                .teacherId(null)
                .academicYearId(AcademicYearId.from(UUID.randomUUID()))
                .minPassingGrade(BigDecimal.valueOf(6.00))
                .status(CourseStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private CourseSubject buildInactiveCourseSubject() {
        return CourseSubject.builder()
                .courseSubjectId(CourseSubjectId.of(COURSE_SUBJECT_UUID))
                .gradeLevelId(GradeLevelId.from(UUID.randomUUID()))
                .subjectId(SubjectId.from(UUID.randomUUID()))
                .teacherId(null)
                .academicYearId(AcademicYearId.from(UUID.randomUUID()))
                .minPassingGrade(BigDecimal.valueOf(6.00))
                .status(CourseStatus.COMPLETED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private StudentCourseSubject buildSavedEnrollment() {
        return StudentCourseSubject.builder()
                .studentCourseSubjectId(StudentCourseSubjectId.of(STUDENT_CS_UUID))
                .enrollmentId(ENROLLMENT_UUID)
                .courseSubjectId(COURSE_SUBJECT_UUID)
                .status(SubjectEnrollmentStatus.ENROLLED)
                .totalClasses(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private StudentCourseSubjectResponse buildResponse() {
        return new StudentCourseSubjectResponse(
                STUDENT_CS_UUID,
                ENROLLMENT_UUID,
                COURSE_SUBJECT_UUID,
                SubjectEnrollmentStatus.ENROLLED,
                0,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    // ── Tests ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("execute — flujo feliz — inscribe alumno en estado ENROLLED")
    void execute_happyPath_enrollsStudentInEnrolledStatus() {
        EnrollStudentRequest request = buildRequest();
        CourseSubject activeCourse = buildActiveCourseSubject();
        StudentCourseSubject saved = buildSavedEnrollment();
        StudentCourseSubjectResponse response = buildResponse();

        when(courseSubjectRepository.findById(CourseSubjectId.from(COURSE_SUBJECT_UUID)))
                .thenReturn(Optional.of(activeCourse));
        when(studentCourseSubjectRepository.existsByEnrollmentAndCourseSubject(
                ENROLLMENT_UUID, COURSE_SUBJECT_UUID))
                .thenReturn(false);
        when(studentCourseSubjectRepository.save(any(StudentCourseSubject.class)))
                .thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(response);

        StudentCourseSubjectResponse result = useCase.execute(request);

        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo(SubjectEnrollmentStatus.ENROLLED);
        assertThat(result.totalClasses()).isZero();
        verify(studentCourseSubjectRepository).save(any(StudentCourseSubject.class));
    }

    @Test
    @DisplayName("execute — curso no existe — lanza CourseSubjectNotFoundException")
    void execute_whenCourseSubjectNotFound_thenThrowCourseSubjectNotFoundException() {
        EnrollStudentRequest request = buildRequest();

        when(courseSubjectRepository.findById(CourseSubjectId.from(COURSE_SUBJECT_UUID)))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(CourseSubjectNotFoundException.class);

        verify(studentCourseSubjectRepository, never()).save(any());
    }

    @Test
    @DisplayName("execute — curso COMPLETED — lanza CourseSubjectNotFoundException")
    void execute_whenCourseSubjectIsCompleted_thenThrowCourseSubjectNotFoundException() {
        EnrollStudentRequest request = buildRequest();
        CourseSubject completedCourse = buildInactiveCourseSubject();

        when(courseSubjectRepository.findById(CourseSubjectId.from(COURSE_SUBJECT_UUID)))
                .thenReturn(Optional.of(completedCourse));

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(CourseSubjectNotFoundException.class);

        verify(studentCourseSubjectRepository, never()).save(any());
    }

    @Test
    @DisplayName("execute — alumno ya inscripto — lanza StudentAlreadyEnrolledException")
    void execute_whenStudentAlreadyEnrolled_thenThrowStudentAlreadyEnrolledException() {
        EnrollStudentRequest request = buildRequest();
        CourseSubject activeCourse = buildActiveCourseSubject();

        when(courseSubjectRepository.findById(CourseSubjectId.from(COURSE_SUBJECT_UUID)))
                .thenReturn(Optional.of(activeCourse));
        when(studentCourseSubjectRepository.existsByEnrollmentAndCourseSubject(
                ENROLLMENT_UUID, COURSE_SUBJECT_UUID))
                .thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(StudentAlreadyEnrolledException.class);

        verify(studentCourseSubjectRepository, never()).save(any());
    }

    @Test
    @DisplayName("execute — alumno ya inscripto — no persiste nada")
    void execute_whenAlreadyEnrolled_thenNothingIsPersisted() {
        EnrollStudentRequest request = buildRequest();
        CourseSubject activeCourse = buildActiveCourseSubject();

        when(courseSubjectRepository.findById(CourseSubjectId.from(COURSE_SUBJECT_UUID)))
                .thenReturn(Optional.of(activeCourse));
        when(studentCourseSubjectRepository.existsByEnrollmentAndCourseSubject(
                ENROLLMENT_UUID, COURSE_SUBJECT_UUID))
                .thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(StudentAlreadyEnrolledException.class);

        verify(studentCourseSubjectRepository, never()).save(any());
        verifyNoMoreInteractions(studentCourseSubjectRepository);
    }
}