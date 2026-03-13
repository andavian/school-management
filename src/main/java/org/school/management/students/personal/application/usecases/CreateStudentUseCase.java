package org.school.management.students.personal.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.academic.domain.exception.AcademicYearNotFoundException;
import org.school.management.academic.domain.exception.GradeLevelNotFoundException;
import org.school.management.academic.domain.repository.AcademicYearRepository;
import org.school.management.academic.domain.repository.GradeLevelRepository;
import org.school.management.academic.domain.repository.QualificationRegistryRepository;
import org.school.management.academic.domain.service.FolioAssignmentService;
import org.school.management.academic.domain.service.RegistryNumberGenerator;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.GradeLevelId;
import org.school.management.academic.domain.valueobject.ids.RegistryId;
import org.school.management.auth.domain.model.Role;
import org.school.management.auth.domain.model.User;
import org.school.management.auth.domain.repository.UserRepository;
import org.school.management.auth.domain.valueobject.HashedPassword;
import org.school.management.auth.domain.valueobject.PlainPassword;
import org.school.management.auth.domain.valueobject.RoleName;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.shared.geography.domain.valueobject.PlaceId;
import org.school.management.shared.person.domain.valueobject.*;
import org.school.management.students.enrollment.domain.model.StudentEnrollment;
import org.school.management.students.enrollment.domain.repository.StudentEnrollmentRepository;
import org.school.management.students.enrollment.domain.valueobject.EnrollmentId;
import org.school.management.students.enrollment.domain.valueobject.EnrollmentType;
import org.school.management.students.health.domain.model.StudentHealthRecord;
import org.school.management.students.health.domain.repository.StudentHealthRecordRepository;
import org.school.management.students.health.domain.valueobject.BloodType;
import org.school.management.students.health.domain.valueobject.HealthRecordId;
import org.school.management.students.personal.application.dto.request.CreateStudentRequest;
import org.school.management.students.personal.application.dto.response.StudentResponse;
import org.school.management.students.personal.domain.exception.StudentAlreadyExistsException;
import org.school.management.students.personal.domain.model.StudentPersonalData;
import org.school.management.students.personal.domain.repository.StudentPersonalDataRepository;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;
import org.school.management.students.records.domain.model.StudentRecord;
import org.school.management.students.records.domain.repository.StudentRecordRepository;
import org.school.management.students.records.domain.valueobject.RecordId;
import org.school.management.students.records.domain.valueobject.RecordNumber;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CreateStudentUseCase {

    // ── Repositorios ──────────────────────────────────────────────────────
    private final StudentPersonalDataRepository studentRepository;
    private final StudentHealthRecordRepository healthRecordRepository;
    private final StudentRecordRepository studentRecordRepository;
    private final StudentEnrollmentRepository enrollmentRepository;
    private final AcademicYearRepository academicYearRepository;
    private final GradeLevelRepository gradeLevelRepository;
    private final UserRepository userRepository;
    private final QualificationRegistryRepository registryRepository;

    // ── Domain Services ───────────────────────────────────────────────────
    private final FolioAssignmentService folioAssignmentService;
    private final RegistryNumberGenerator registryNumberGenerator;
    private final HashedPassword.PasswordEncoder passwordEncoder;

    // ── Use Cases ─────────────────────────────────────────────────────────
    private final GetStudentByIdUseCase getStudentByIdUseCase;

    public StudentResponse execute(CreateStudentRequest request, UUID createdByUserId) {
        log.info("Creating student with DNI: {}", request.dni());

        // ── Paso 1 & 2: Validar unicidad DNI y CUIL ───────────────────────
        Dni dni = Dni.of(request.dni());
        Cuil cuil = Cuil.of(request.cuil());

        if (studentRepository.existsByDni(dni)) {
            throw StudentAlreadyExistsException.withDni(request.dni());
        }
        if (studentRepository.existsByCuil(cuil.value())) {
            throw StudentAlreadyExistsException.withCuil(request.cuil());
        }

        // ── Paso 3: Obtener AcademicYear activo ───────────────────────────
        var academicYear = academicYearRepository.findCurrentYear()
                .orElseThrow(() -> new AcademicYearNotFoundException(
                        "No active academic year found. Cannot enroll student."
                ));

        // ── Paso 4: Validar GradeLevel existe y está activo ───────────────
        GradeLevelId gradeLevelId = GradeLevelId.from(request.gradeLevelId());

        var gradeLevel = gradeLevelRepository
                .findById(gradeLevelId)
                .orElseThrow(() -> new GradeLevelNotFoundException(gradeLevelId));

        if (!gradeLevel.getIsActive()) {
            throw new GradeLevelNotFoundException(
                    "GradeLevel is not active: " + request.gradeLevelId()
            );
        }



        // ── Paso 5: Asignar folio ─────────────────────────────────────────
        Integer folioNumber = folioAssignmentService.assignNextFolio();

        // ── Paso 6: Generar password inicial {DNI}Ipet132! ────────────────
        String rawPassword = request.dni() + "Ipet132!";
        PlainPassword plainPassword = PlainPassword.of(rawPassword);

        // ── Paso 7: Crear User en auth ────────────────────────────────────
        Role studentRole = Role.create(RoleName.student());
        User user = User.create(dni, plainPassword, Set.of(studentRole), passwordEncoder);
        User savedUser = userRepository.save(user);
        UserId userId = savedUser.getUserId();
        UserId createdBy = UserId.from(createdByUserId);

        log.debug("User created for student — userId: {}, DNI: {}", userId.value(), request.dni());

        // ── Paso 8: Crear StudentPersonalData ─────────────────────────────
        StudentPersonalDataId studentId = StudentPersonalDataId.generate();

        Address address = new Address(
                request.addressStreet(),
                request.addressNumber(),
                request.addressFloor(),
                request.addressApartment(),
                PlaceId.of(request.residencePlaceId()),
                request.postalCode()
        );

        StudentPersonalData student = StudentPersonalData.create(
                StudentPersonalData.builder()
                        .studentId(studentId)
                        .userId(userId)
                        .dni(dni)
                        .cuil(cuil)
                        .fullName(FullName.of(request.firstName(), request.lastName()))
                        .birthDate(request.birthDate())
                        .birthPlaceId(PlaceId.of(request.birthPlaceId()))
                        .residencePlaceId(PlaceId.of(request.residencePlaceId()))
                        .gender(Gender.valueOf(request.gender()))
                        .nationality(Nationality.of(request.nationality()))
                        .phone(request.phone() != null && !request.phone().isBlank()
                                ? PhoneNumber.of(request.phone()) : null)
                        .email(request.email() != null && !request.email().isBlank()
                                ? Email.of(request.email()) : null)
                        .address(address)
                        .createdBy(createdBy)
        );
        studentRepository.save(student);
        log.debug("StudentPersonalData created — id: {}", studentId.value());

        // ── Paso 9: Crear StudentHealthRecord ─────────────────────────────
        var healthData = request.healthData();
        StudentHealthRecord healthRecord = StudentHealthRecord.create(
                StudentHealthRecord.builder()
                        .healthRecordId(HealthRecordId.generate())
                        .studentId(studentId)
                        .bloodType(healthData.bloodType() != null
                                ? BloodType.fromString(healthData.bloodType()) : null)
                        .healthInsurance(healthData.healthInsurance())
                        .healthInsuranceNumber(healthData.healthInsuranceNumber())
                        .allergies(healthData.allergies())
                        .chronicConditions(healthData.chronicConditions())
                        .medications(healthData.medications())
                        .medicalObservations(healthData.medicalObservations())
                        .emergencyContactName(FullName.of(
                                healthData.emergencyContactFirstName(),
                                healthData.emergencyContactLastName()
                        ))
                        .emergencyContactPhone(PhoneNumber.of(healthData.emergencyContactPhone()))
        );
        healthRecordRepository.save(healthRecord);
        log.debug("StudentHealthRecord created — studentId: {}", studentId.value());

// ── Pasos 10 & 11: Crear StudentRecord ────────────────────────────
        var activeRegistry = registryRepository
                .findActiveRegistryForYear(academicYear.getAcademicYearId())
                .orElseThrow(() -> new IllegalStateException(
                        "No active registry found for academic year: "
                                + academicYear.getAcademicYearId().value()
                ));

        // El número de legajo ES el DNI del estudiante — único y permanente
        RecordNumber recordNumber = RecordNumber.fromDni(request.dni());

        StudentRecord studentRecord = StudentRecord.create(
                StudentRecord.builder()
                        .recordId(RecordId.generate())
                        .studentId(studentId)
                        .academicYearId(AcademicYearId.of(academicYear.getAcademicYearId().value()))
                        .recordNumber(recordNumber)
                        .registryId(RegistryId.of(activeRegistry.getRegistryId().value()))
                        .folioNumber(folioNumber)  // ← asignado por FolioAssignmentService en paso 5
                        .documents(new ArrayList<>())
        );

        studentRecordRepository.save(studentRecord);
        log.debug("StudentRecord created — recordNumber (DNI): {}, folio: {}",
                recordNumber.value(), folioNumber);

        // ── Pasos 12 & 13: Parent + StudentParent ─────────────────────────
        // TODO: implementar cuando el agregado parents/ esté desarrollado
        // - Buscar Parent por DNI: request.parent().dni()
        // - Si no existe: crear User (password aleatorio seguro) + crear Parent
        // - Crear StudentParent: relationship, isPrimaryContact, isAuthorizedPickup
        log.warn("Parent/StudentParent creation pending — parents aggregate not yet implemented");

        // ── Paso 14: Crear StudentEnrollment ──────────────────────────────
        StudentEnrollment enrollment = StudentEnrollment.create(
                StudentEnrollment.builder()
                        .enrollmentId(EnrollmentId.generate())
                        .studentId(studentId)
                        .academicYearId(AcademicYearId.of(academicYear.getAcademicYearId().value()))
                        .gradeLevelId(GradeLevelId.of(request.gradeLevelId()))
                        .enrollmentDate(LocalDate.now())
                        .enrollmentType(EnrollmentType.valueOf(request.enrollmentType()))
                        .isRepeating(Boolean.TRUE.equals(request.isRepeating()))
                        .previousSchool(request.previousSchool())
        );
        enrollmentRepository.save(enrollment);
        log.debug("StudentEnrollment created — studentId: {}", studentId.value());

        // ── Paso 15: Commit y retornar response ───────────────────────────
        log.info("Student created successfully — id: {}, DNI: {}",
                studentId.value(), request.dni());

        return getStudentByIdUseCase.buildResponse(student);
    }
}