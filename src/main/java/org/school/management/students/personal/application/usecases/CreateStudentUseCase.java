package org.school.management.students.personal.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.students.personal.application.dto.request.CreateStudentRequest;
import org.school.management.students.personal.application.dto.response.StudentResponse;
import org.school.management.students.personal.application.mappers.StudentPersonalDataMapper;
import org.school.management.students.personal.domain.exception.StudentAlreadyExistsException;
import org.school.management.students.personal.domain.model.StudentPersonalData;
import org.school.management.students.personal.domain.repository.StudentPersonalDataRepository;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use Case: Crear un nuevo estudiante
 *
 * Responsabilidad:
 * - Validar que el DNI no exista
 * - Validar coherencia DNI-CUIL
 * - Crear User en Auth context (usando port)
 * - Construir StudentPersonalData con todos los Value Objects
 * - Construir Address usando el VO del Shared Kernel (con normalización automática)
 * - Persistir el agregado
 *
 * Flujo transaccional completo
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CreateStudentUseCase {

    private final StudentPersonalDataRepository studentRepository;
    private final StudentPersonalDataMapper mapper;
    // TODO: Inyectar cuando exista
    // private final UserService userService; // Port hacia Auth context
    // private final PlaceService placeService; // Port hacia Geography context

    /**
     * Ejecuta el caso de uso
     *
     * @param request Datos del estudiante a crear
     * @param createdBy Usuario que ejecuta la acción
     * @return StudentResponse con datos completos del estudiante creado
     * @throws StudentAlreadyExistsException si el DNI ya existe
     * @throws IllegalArgumentException si CUIL no coincide con DNI
     */
    @Transactional
    public StudentResponse execute(CreateStudentRequest request, UserId createdBy) {
        log.info("Creating student with DNI: {}", request.dni());

        // 1. Validar que DNI no exista
        var dni = mapper.mapDni(request.dni());
        if (studentRepository.existsByDni(dni)) {
            throw new StudentAlreadyExistsException(
                    "Student with DNI " + request.dni() + " already exists"
            );
        }



        // 3. Crear User en Auth context (TODO: cuando exista el port)
        // UserId userId = userService.createUserForStudent(dni, generateInitialPassword(dni.getValue()));
        UserId userId = UserId.generate(); // Temporal hasta implementar Auth integration

        // 4. Construir Address usando el VO del Shared Kernel
        // IMPORTANTE: Address hace normalización automática:
        // - "av colon" → "Av. Colón"
        // - "1234" valida formato
        // - floor/apartment opcionales
        var address = mapper.mapAddress(request);

        // 5. Construir StudentPersonalData con builder validado
        StudentPersonalData student = StudentPersonalData.create(
                StudentPersonalData.builder()
                        .studentId(StudentPersonalDataId.generate())
                        .userId(userId)
                        .dni(dni)
                        .cuil(mapper.mapCuil(request.cuil()))
                        .fullName(mapper.mapFullName(request))
                        .birthDate(request.birthDate())
                        .gender(mapper.mapGender(request.gender()))
                        .nationality(mapper.mapNationality(request.nationality()))
                        .birthPlaceId(mapper.mapPlaceId(request.birthPlaceId()))
                        .residencePlaceId(mapper.mapResidencePlaceId(request.residencePlaceId()))
                        .phone(mapper.mapPhoneNumber(request.phone()))
                        .email(mapper.mapEmail(request.email()))
                        .address(address) // Address del Shared Kernel (ya normalizado)
                        .createdBy(createdBy)
        );

        // 6. Persistir
        StudentPersonalData saved = studentRepository.save(student);

        log.info("Student created successfully with ID: {}", saved.getStudentId().asString());

        // 7. Convertir a DTO
        StudentResponse response = mapper.toResponse(saved);

        // 8. Enriquecer con datos de Geography context (TODO: cuando exista el port)
        // response = enrichWithPlaceNames(response, placeService);

        return response;
    }

    /**
     * Genera password inicial para el estudiante
     * Formato: {DNI}Ipet132!
     */
    private String generateInitialPassword(String dni) {
        return dni + "Ipet132!";
    }

    /**
     * Enriquece la respuesta con nombres de lugares desde Geography context
     *
     * TODO: Implementar cuando exista el port PlaceService
     *
     * @param response Response básico sin nombres de lugares
     * @param placeService Servicio de Geography context
     * @return Response enriquecido con nombres completos
     */
    private StudentResponse enrichWithPlaceNames(
            StudentResponse response,
            Object placeService) {

        // Pseudocódigo de implementación futura:

        // 1. Obtener nombre completo del birth place
        // var birthPlace = placeService.getPlaceDetails(response.birthPlace().placeId());
        // var birthPlaceDto = new StudentResponse.PlaceDto(
        //     birthPlace.id(),
        //     birthPlace.name(),       // "Córdoba Capital"
        //     birthPlace.province(),   // "Córdoba"
        //     birthPlace.country()     // "Argentina"
        // );

        // 2. Obtener nombre completo del residence place
        // var residencePlace = placeService.getPlaceDetails(response.address().residencePlaceId());
        // var residencePlaceDto = new StudentResponse.PlaceDto(
        //     residencePlace.id(),
        //     residencePlace.name(),
        //     residencePlace.province(),
        //     residencePlace.country()
        // );

        // 3. Actualizar AddressDto con formato completo usando Address.toStringFormatted()
        // var enrichedAddress = new StudentResponse.AddressDto(
        //     response.address().street(),
        //     response.address().number(),
        //     response.address().floor(),
        //     response.address().apartment(),
        //     response.address().residencePlaceId(),
        //     residencePlace.name(), // "Córdoba"
        //     response.address().postalCode(),
        //     // Usar método del Shared Kernel Address para formato oficial
        //     Address.toStringFormatted(residencePlace.name())
        //     // Resultado: "Av. Colón 1234, Piso 5, Depto A, Córdoba, CP X5000"
        // );

        // 4. Reconstruir response con los datos completos
        // return new StudentResponse(
        //     response.studentId(),
        //     response.userId(),
        //     // ... todos los campos
        //     enrichedAddress,
        //     birthPlaceDto,
        //     residencePlaceDto,
        //     response.createdAt(),
        //     response.updatedAt()
        // );

        return response;
    }
}