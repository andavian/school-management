// org.school.management.attendance.infrastructure.web.controller.AttendanceController
package org.school.management.attendance.infrastructure.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.attendance.application.usecases.CorrectAttendanceUseCase;
import org.school.management.attendance.application.usecases.GetAtRiskStudentsUseCase;
import org.school.management.attendance.application.usecases.GetAttendanceSummaryUseCase;
import org.school.management.attendance.application.usecases.JustifyAbsenceUseCase;
import org.school.management.attendance.application.usecases.RecordCourseAttendanceUseCase;
import org.school.management.attendance.application.usecases.RecordDailyAttendanceUseCase;
import org.school.management.attendance.infrastructure.web.dto.AttendanceWebDto;
import org.school.management.attendance.infrastructure.web.mapper.AttendanceWebMapper;
import org.school.management.auth.domain.model.User;
import org.school.management.auth.infra.security.UserPrincipal;
import org.school.management.auth.infra.web.SecurityContextHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Attendance", description = "Control de asistencia diaria y por materia")
@SecurityRequirement(name = "bearerAuth")
public class AttendanceController {

    private final RecordDailyAttendanceUseCase recordDailyAttendanceUseCase;
    private final RecordCourseAttendanceUseCase recordCourseAttendanceUseCase;
    private final JustifyAbsenceUseCase justifyAbsenceUseCase;
    private final CorrectAttendanceUseCase correctAttendanceUseCase;
    private final GetAttendanceSummaryUseCase getAttendanceSummaryUseCase;
    private final GetAtRiskStudentsUseCase getAtRiskStudentsUseCase;
    private final AttendanceWebMapper webMapper;

    // ── Daily Attendance (STAFF / preceptor) ──

    @PostMapping("/daily")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Registrar asistencia diaria del curso")
    public ResponseEntity<AttendanceWebDto.DailyAttendanceWebResponse> recordDailyAttendance(
            @Valid @RequestBody AttendanceWebDto.RecordDailyAttendanceWebRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        var response = recordDailyAttendanceUseCase.execute(
                webMapper.toAppRequest(request),
                SecurityContextHelper.extractUserId(userPrincipal));
        return ResponseEntity.status(HttpStatus.CREATED).body(webMapper.toWebResponse(response));
    }

    @PatchMapping("/daily/{dailyAttendanceId}/justify")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Justificar ausencia diaria")
    public ResponseEntity<AttendanceWebDto.DailyAttendanceWebResponse> justifyAbsence(
            @PathVariable UUID dailyAttendanceId,
            @Valid @RequestBody AttendanceWebDto.JustifyAbsenceWebRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        var response = justifyAbsenceUseCase.execute(
                dailyAttendanceId,
                webMapper.toAppRequest(request),
                SecurityContextHelper.extractUserId(userPrincipal));
        return ResponseEntity.ok(webMapper.toWebResponse(response));
    }

    @PatchMapping("/daily/{dailyAttendanceId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Corregir registro de asistencia diaria")
    public ResponseEntity<AttendanceWebDto.DailyAttendanceWebResponse> correctDailyAttendance(
            @PathVariable UUID dailyAttendanceId,
            @Valid @RequestBody AttendanceWebDto.CorrectAttendanceWebRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        var response = correctAttendanceUseCase.correctDaily(
                dailyAttendanceId,
                webMapper.toAppRequest(request),
                SecurityContextHelper.extractUserId(userPrincipal));
        return ResponseEntity.ok(webMapper.toWebResponse(response));
    }

    // ── Course Attendance (TEACHER) ──

    @PostMapping("/course")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'TEACHER')")
    @Operation(summary = "Registrar asistencia por materia")
    public ResponseEntity<AttendanceWebDto.CourseAttendanceWebResponse> recordCourseAttendance(
            @Valid @RequestBody AttendanceWebDto.RecordCourseAttendanceWebRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        var response = recordCourseAttendanceUseCase.execute(
                webMapper.toAppRequest(request),
                SecurityContextHelper.extractUserId(userPrincipal));
        return ResponseEntity.status(HttpStatus.CREATED).body(webMapper.toWebResponse(response));
    }

    @PatchMapping("/course/{courseAttendanceId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'TEACHER')")
    @Operation(summary = "Corregir registro de asistencia por materia")
    public ResponseEntity<AttendanceWebDto.CourseAttendanceWebResponse> correctCourseAttendance(
            @PathVariable UUID courseAttendanceId,
            @Valid @RequestBody AttendanceWebDto.CorrectAttendanceWebRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        var response = correctAttendanceUseCase.correctCourse(
                courseAttendanceId,
                webMapper.toAppRequest(request),
                SecurityContextHelper.extractUserId(userPrincipal));
        return ResponseEntity.ok(webMapper.toWebResponse(response));
    }

    // ── Summaries & Queries ──

    @GetMapping("/course/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'TEACHER')")
    @Operation(summary = "Resumen de asistencia por alumno, materia y período")
    public ResponseEntity<AttendanceWebDto.AttendanceSummaryWebResponse> getSummary(
            @RequestParam UUID studentCourseSubjectId,
            @RequestParam UUID periodId) {
        var response = getAttendanceSummaryUseCase.execute(studentCourseSubjectId, periodId);
        return ResponseEntity.ok(webMapper.toWebResponse(response));
    }

    @GetMapping("/course/at-risk")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Alumnos en riesgo de quedar libres en una materia")
    public ResponseEntity<AttendanceWebDto.AtRiskStudentsWebResponse> getAtRiskStudents(
            @RequestParam UUID courseSubjectId,
            @RequestParam UUID periodId) {
        var atRiskList = getAtRiskStudentsUseCase.execute(courseSubjectId, periodId);
        return ResponseEntity.ok(webMapper.toAtRiskResponse(atRiskList));
    }


}