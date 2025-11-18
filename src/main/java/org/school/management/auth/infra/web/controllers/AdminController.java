package org.school.management.auth.infra.web.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.application.usecases.admin.*;
import org.school.management.auth.domain.exception.DniAlreadyExistsException;
import org.school.management.auth.infra.web.dto.requests.*;
import org.school.management.auth.infra.web.dto.response.*;
import org.school.management.auth.infra.web.mappers.AuthWebMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')") // Todos los endpoints requieren rol ADMIN
public class AdminController {

    private final CreateStudentUseCase createStudentUseCase;
    private final CreateTeacherUseCase createTeacherUseCase;
    private final AuthWebMapper webMapper;

    // ============================================
    // CREATE STUDENT - Solo ADMIN
    // ============================================
    @PostMapping("/students")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CreateStudentApiResponse> createStudent(
            @Valid @RequestBody CreateStudentApiRequest request) {

        log.info("POST /api/admin/students - DNI: {} {} {}",
                request.dni(), request.firstName(), request.lastName());

        try {
            var applicationRequest = webMapper.toApplicationDto(request);
            var response = createStudentUseCase.execute(applicationRequest);
            var apiResponse = webMapper.toApiResponse(response);

            log.info("Estudiante creado exitosamente. DNI: {}", request.dni());
            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);

        } catch (DniAlreadyExistsException e) {
            log.warn("DNI ya existe: {}", request.dni());
            throw e;
        }
    }

    // ============================================
    // CREATE TEACHER - Solo ADMIN
    // ============================================
    @PostMapping("/teachers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CreateTeacherApiResponse> createTeacher(
            @Valid @RequestBody CreateTeacherApiRequest request) {

        log.info("POST /api/admin/teachers - DNI: {} {} {}",
                request.dni(), request.firstName(), request.lastName());

        try {
            var applicationRequest = webMapper.toApplicationDto(request);
            var response = createTeacherUseCase.execute(applicationRequest);
            var apiResponse = webMapper.toApiResponse(response);

            log.info("Profesor creado exitosamente. DNI: {} - Email: {}",
                    request.dni(), request.email());
            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);

        } catch (DniAlreadyExistsException e) {
            log.warn("DNI ya existe: {}", request.dni());
            throw e;
        }
    }

    // ============================================
    // GET ALL STUDENTS - Solo ADMIN o TEACHER
    // ============================================
    @GetMapping("/students")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<PagedUsersApiResponse> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        log.info("GET /api/admin/students - page: {}, size: {}", page, size);

        // TODO: Implementar GetAllStudentsUseCase con paginación
        throw new UnsupportedOperationException("Endpoint no implementado aún");
    }

    // ============================================
    // GET ALL TEACHERS - Solo ADMIN
    // ============================================
    @GetMapping("/teachers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedUsersApiResponse> getAllTeachers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("GET /api/admin/teachers - page: {}, size: {}", page, size);

        // TODO: Implementar GetAllTeachersUseCase con paginación
        throw new UnsupportedOperationException("Endpoint no implementado aún");
    }

    // ============================================
    // ACTIVATE/DEACTIVATE USER - Solo ADMIN
    // ============================================
    @PutMapping("/users/{userId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessApiResponse> activateUser(@PathVariable String userId) {
        log.info("PUT /api/admin/users/{}/activate", userId);

        // TODO: Implementar ActivateUserUseCase
        throw new UnsupportedOperationException("Endpoint no implementado aún");
    }

    @PutMapping("/users/{userId}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessApiResponse> deactivateUser(@PathVariable String userId) {
        log.info("PUT /api/admin/users/{}/deactivate", userId);

        // TODO: Implementar DeactivateUserUseCase
        throw new UnsupportedOperationException("Endpoint no implementado aún");
    }

    // ============================================
    // GET USER BY DNI - Solo ADMIN
    // ============================================
    @GetMapping("/users/dni/{dni}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserApiResponse> getUserByDni(@PathVariable String dni) {
        log.info("GET /api/admin/users/dni/{}", dni);

        // TODO: Implementar GetUserByDniUseCase
        throw new UnsupportedOperationException("Endpoint no implementado aún");
    }
}
