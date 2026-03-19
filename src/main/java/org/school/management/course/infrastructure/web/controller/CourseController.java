package org.school.management.course.infrastructure.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.domain.model.User;
import org.school.management.course.application.dto.response.CourseSubjectResponse;
import org.school.management.course.application.dto.response.StudentCourseSubjectResponse;
import org.school.management.course.application.usecases.*;
import org.school.management.course.infrastructure.web.dto.CourseWebDto;
import org.school.management.course.infrastructure.web.mapper.CourseWebMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Courses", description = "Gestión de asignaciones materia-curso-profesor")
@SecurityRequirement(name = "bearerAuth")
public class CourseController {

    private final CreateCourseSubjectUseCase createCourseSubjectUseCase;
    private final AssignTeacherToCourseUseCase assignTeacherToCourseUseCase;
    private final EnrollStudentInCourseUseCase enrollStudentInCourseUseCase;
    private final GetCourseSubjectsByGradeLevelUseCase getCourseSubjectsByGradeLevelUseCase;
    private final GetStudentCoursesUseCase getStudentCoursesUseCase;
    private final CourseWebMapper webMapper;

    // ------------------------------------------------------------------
    // CourseSubject endpoints
    // ------------------------------------------------------------------

    @Operation(summary = "Crear asignación materia-curso")
    @PostMapping("/course-subjects")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<CourseSubjectResponse> createCourseSubject(
            @Valid @RequestBody CourseWebDto.CreateCourseSubjectWebRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createCourseSubjectUseCase.execute(webMapper.toRequest(request)));
    }

    @Operation(summary = "Obtener materias de un curso por año lectivo")
    @GetMapping("/course-subjects")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'TEACHER')")
    public ResponseEntity<List<CourseSubjectResponse>> getCourseSubjectsByGradeLevel(
            @RequestParam UUID gradeLevelId,
            @RequestParam UUID academicYearId) {
        return ResponseEntity.ok(
                getCourseSubjectsByGradeLevelUseCase.execute(gradeLevelId, academicYearId));
    }

    @Operation(summary = "Asignar docente a una materia-curso")
    @PatchMapping("/course-subjects/{courseSubjectId}/teacher")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<CourseSubjectResponse> assignTeacher(
            @PathVariable UUID courseSubjectId,
            @Valid @RequestBody CourseWebDto.AssignTeacherWebRequest request) {
        return ResponseEntity.ok(
                assignTeacherToCourseUseCase.execute(courseSubjectId, webMapper.toRequest(request)));
    }

    // ------------------------------------------------------------------
    // StudentCourseSubject endpoints
    // ------------------------------------------------------------------

    @Operation(summary = "Inscribir alumno a una materia-curso")
    @PostMapping("/enrollments")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<StudentCourseSubjectResponse> enrollStudent(
            @Valid @RequestBody CourseWebDto.EnrollStudentWebRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(enrollStudentInCourseUseCase.execute(webMapper.toRequest(request)));
    }

    @Operation(summary = "Obtener materias de un alumno por inscripción")
    @GetMapping("/enrollments/{enrollmentId}/courses")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'TEACHER')")
    public ResponseEntity<List<StudentCourseSubjectResponse>> getStudentCourses(
            @PathVariable UUID enrollmentId) {
        return ResponseEntity.ok(getStudentCoursesUseCase.execute(enrollmentId));
    }

    // ------------------------------------------------------------------

    private UUID extractUserId(UserDetails userDetails) {
        if (userDetails instanceof User user) {
            return user.getUserId().value();
        }
        throw new IllegalStateException("Principal inesperado: " + userDetails.getClass().getName());
    }
}