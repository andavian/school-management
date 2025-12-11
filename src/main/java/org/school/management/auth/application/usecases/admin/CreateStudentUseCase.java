package org.school.management.auth.application.usecases.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.application.dto.requests.CreateStudentRequest;
import org.school.management.auth.application.dto.responses.CreateStudentResponse;
import org.school.management.auth.application.mappers.AuthApplicationMapper;
import org.school.management.auth.domain.exception.DniAlreadyExistsException;
import org.school.management.auth.domain.model.User;
import org.school.management.auth.domain.repository.RoleRepository;
import org.school.management.auth.domain.repository.UserRepository;
import org.school.management.auth.domain.valueobject.HashedPassword;
import org.school.management.auth.domain.valueobject.PlainPassword;
import org.school.management.shared.person.domain.valueobject.DNI;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateStudentUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthApplicationMapper mapper;
    private final HashedPassword.PasswordEncoder passwordEncoder;

    @Transactional
    public CreateStudentResponse execute(CreateStudentRequest request) {
        log.info("Creando estudiante: {} {} - DNI: {}",
                request.firstName(), request.lastName(), request.dni());

        // Validar DNI único
        DNI dni = mapper.toDni(request.dni());

        if (userRepository.existsByDni(dni)) {
            log.warn("Intento de crear estudiante con DNI existente: {}", request.dni());
            throw new DniAlreadyExistsException("Ya existe un usuario con este DNI: " + request.dni());
        }

        // Generar password inicial: DNI + sufijo del colegio
        String initialPasswordStr = generateStudentPassword(request.dni());
        PlainPassword initialPassword = PlainPassword.of(initialPasswordStr);

        // Crear usuario estudiante usando factory method del mapper
        User student = mapper.createStudentFromRequest(request, initialPassword, passwordEncoder, roleRepository);

        // Los estudiantes están activos desde el inicio
        student.activate();

        // Guardar
        User savedStudent = userRepository.save(student);

        log.info("Estudiante creado exitosamente. DNI: {} - ID: {}",
                request.dni(), savedStudent.getUserId().asString());

        // TODO: Enviar credenciales a email del padre si está disponible
        if (request.parentEmail() != null && !request.parentEmail().isEmpty()) {
            log.info("Enviar credenciales a padre: {}", request.parentEmail());
        }

        return new CreateStudentResponse(
                savedStudent.getUserId().asString(),
                savedStudent.getDni().getValue(),
                initialPasswordStr
        );
    }

    private String generateStudentPassword(String dni) {
        return dni + "ipet#xyz";
    }


}