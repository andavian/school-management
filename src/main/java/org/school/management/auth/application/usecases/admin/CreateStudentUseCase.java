package org.school.management.auth.application.usecases.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.application.dto.*;
import org.school.management.auth.application.dto.requests.CreateStudentRequest;
import org.school.management.auth.application.dto.responses.CreateStudentResponse;
import org.school.management.auth.application.mappers.AuthApplicationMapper;
import org.school.management.auth.domain.model.User;
import org.school.management.auth.domain.repository.UserRepository;
import org.school.management.auth.domain.valueobject.HashedPassword;
import org.school.management.auth.domain.valueobject.PlainPassword;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateStudentUseCase {

    private final UserRepository userRepository;
    private final AuthApplicationMapper mapper;
    private final HashedPassword.PasswordEncoder passwordEncoder;

    @Transactional
    public CreateStudentResponse execute(CreateStudentRequest request) { // Record como parámetro
        // Acceso a campos del record con métodos accessor
        var email = mapper.toEmail(request.email());

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("Ya existe un usuario con este email: " + request.email());
        }

        // Generar password inicial
        var initialPassword = PlainPassword.of(generateStudentPassword(request.dni()));

        // Crear usuario usando factory method
        User student = mapper.createStudentFromRequest(request, initialPassword, passwordEncoder);
        User savedStudent = userRepository.save(student);

        log.info("Estudiante creado: {} - {}", request.email(), request.firstName());

        // Retornar record directamente
        return new CreateStudentResponse(
                savedStudent.getUserId().asString(),
                savedStudent.getEmail().getValue(),
                initialPassword.getValue()
        );
    }

    private String generateStudentPassword(String dni) {
        return dni + "Ipet132!";
    }

    public static class EmailAlreadyExistsException extends RuntimeException {
        public EmailAlreadyExistsException(String message) {
            super(message);
        }
    }
}