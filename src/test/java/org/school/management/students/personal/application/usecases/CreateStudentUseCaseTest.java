package org.school.management.students.personal.application.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.academic.domain.exception.AcademicYearNotFoundException;
import org.school.management.academic.domain.exception.GradeLevelNotFoundException;
import org.school.management.academic.domain.model.AcademicYear;
import org.school.management.academic.domain.model.GradeLevel;
import org.school.management.academic.domain.model.QualificationRegistry;
import org.school.management.academic.domain.repository.AcademicYearRepository;
import org.school.management.academic.domain.repository.GradeLevelRepository;
import org.school.management.academic.domain.repository.QualificationRegistryRepository;
import org.school.management.academic.domain.service.FolioAssignmentService;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.GradeLevelId;
import org.school.management.academic.domain.valueobject.ids.RegistryId;
import org.school.management.auth.application.dto.requests.CreateUserRequest;
import org.school.management.auth.application.dto.responses.CreateUserResponse;
import org.school.management.auth.application.usecases.CreateUserUseCase;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.shared.domain.service.EmailService;
import org.school.management.shared.person.domain.valueobject.*;
import org.school.management.students.enrollment.domain.model.StudentEnrollment;
import org.school.management.students.enrollment.domain.repository.StudentEnrollmentRepository;
import org.school.management.students.health.domain.model.StudentHealthRecord;
import org.school.management.students.health.domain.repository.StudentHealthRecordRepository;
import org.school.management.students.parents.domain.model.Parent;
import org.school.management.students.parents.domain.model.StudentParent;
import org.school.management.students.parents.domain.repository.ParentRepository;
import org.school.management.students.parents.domain.repository.StudentParentRepository;
import org.school.management.students.parents.domain.valueobject.ParentId;
import org.school.management.students.personal.application.dto.request.CreateStudentRequest;
import org.school.management.students.personal.application.dto.response.StudentResponse;
import org.school.management.students.personal.domain.exception.StudentAlreadyExistsException;
import org.school.management.students.personal.domain.model.StudentPersonalData;
import org.school.management.students.personal.domain.repository.StudentPersonalDataRepository;
import org.school.management.students.records.domain.model.StudentRecord;
import org.school.management.students.records.domain.repository.StudentRecordRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("CreateStudentUseCase")
class CreateStudentUseCaseTest {

    @Mock private StudentPersonalDataRepository   studentRepository;
    @Mock private StudentHealthRecordRepository   healthRecordRepository;
    @Mock private StudentRecordRepository         studentRecordRepository;
    @Mock private StudentEnrollmentRepository     enrollmentRepository;
    @Mock private AcademicYearRepository          academicYearRepository;
    @Mock private GradeLevelRepository            gradeLevelRepository;
    @Mock private QualificationRegistryRepository registryRepository;
    @Mock private ParentRepository                parentRepository;
    @Mock private StudentParentRepository         studentParentRepository;
    @Mock private FolioAssignmentService          folioAssignmentService;
    @Mock private CreateUserUseCase               createUserUseCase;
    @Mock private GetStudentByIdUseCase           getStudentByIdUseCase;
    @Mock private EmailService                    emailService;

    @InjectMocks private CreateStudentUseCase useCase;

    private static final UUID   CREATED_BY       = UUID.randomUUID();
    private static final UUID   GRADE_LEVEL_UUID = UUID.randomUUID();
    private static final UUID   BIRTH_PLACE_UUID = UUID.randomUUID();
    private static final UUID   RESIDENCE_UUID   = UUID.randomUUID();
    private static final String STUDENT_DNI      = "12345678";
    private static final String STUDENT_CUIL     = "20123456786"; // válido para DNI 12345678
    private static final String PARENT_DNI       = "87654321";
    private static final String PARENT_CUIL      = "20876543215"; // válido para DNI 87654321

    private AcademicYear          academicYear;
    private GradeLevel            gradeLevel;
    private QualificationRegistry registry;

    @BeforeEach
    void setUp() {
        academicYear = mock(AcademicYear.class);
        lenient().when(academicYear.getAcademicYearId()).thenReturn(AcademicYearId.generate());

        gradeLevel = mock(GradeLevel.class);
        lenient().when(gradeLevel.getIsActive()).thenReturn(true);

        registry = mock(QualificationRegistry.class);
        lenient().when(registry.getRegistryId()).thenReturn(RegistryId.generate());

        lenient().when(createUserUseCase.execute(any())).thenReturn(new CreateUserResponse(UUID.randomUUID()));
        lenient().when(academicYearRepository.findCurrentYear()).thenReturn(Optional.of(academicYear));
        lenient().when(gradeLevelRepository.findById(GradeLevelId.from(GRADE_LEVEL_UUID)))
                .thenReturn(Optional.of(gradeLevel));
        lenient().when(registryRepository.findActiveRegistryForYear(any()))
                .thenReturn(Optional.of(registry));
        lenient().when(folioAssignmentService.assignNextFolio()).thenReturn(42);

        lenient().when(studentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(healthRecordRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(studentRecordRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(enrollmentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(studentParentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        lenient().when(getStudentByIdUseCase.buildResponse(any())).thenReturn(mock(StudentResponse.class));
    }

    // ── helpers ───────────────────────────────────────────────────────────

    private CreateStudentRequest buildRequest() {
        var health = new CreateStudentRequest.HealthDataRequest(
                "A+", "OSDE", "123456", null, null, null, null,
                "María", "García", "1155556666"
        );
        var parent = new CreateStudentRequest.ParentRequest(
                "Roberto", "García", PARENT_DNI, PARENT_CUIL,  "roberto@example.com",
                "3547654342", "FATHER",
                 true, true
        );
        return new CreateStudentRequest(
                "Lucas", "Romero", STUDENT_DNI, STUDENT_CUIL,
                LocalDate.of(2005, 3, 15),
                BIRTH_PLACE_UUID, "MALE", "Argentina",
                "1155558888", "lucas@example.com",
                "Av. Colón", "1234", null, null, "5000",
                RESIDENCE_UUID,
                health, parent,
                GRADE_LEVEL_UUID, "NEW", false, null
        );
    }

    /** Parent real — no mockeable por campos final con Lombok @Builder */
    private Parent buildRealParent() {
        return Parent.create(
                Parent.builder()
                        .parentId(ParentId.generate())
                        .userId(UserId.generate())
                        .dni(Dni.of(PARENT_DNI))
                        .cuil(Cuil.of(PARENT_CUIL))
                        .fullName(FullName.of("Roberto", "García"))
                        .email(Email.of("roberto@example.com"))
                        .phone(PhoneNumber.of("1155557777"))
                        .createdBy(UserId.from(CREATED_BY))
        );
    }

    // ── tests ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("DNI duplicado → lanza StudentAlreadyExistsException sin crear nada")
    void duplicateDni_throwsException() {
        when(studentRepository.existsByDni(any())).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(buildRequest(), CREATED_BY))
                .isInstanceOf(StudentAlreadyExistsException.class)
                .hasMessageContaining(STUDENT_DNI);

        verifyNoInteractions(createUserUseCase, academicYearRepository, folioAssignmentService);
    }

    @Test
    @DisplayName("CUIL duplicado → lanza StudentAlreadyExistsException sin crear nada")
    void duplicateCuil_throwsException() {
        when(studentRepository.existsByDni(any())).thenReturn(false);
        when(studentRepository.existsByCuil(any())).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(buildRequest(), CREATED_BY))
                .isInstanceOf(StudentAlreadyExistsException.class)
                .hasMessageContaining(STUDENT_CUIL);

        verifyNoInteractions(createUserUseCase, academicYearRepository, folioAssignmentService);
    }

    @Test
    @DisplayName("sin AcademicYear activo → lanza AcademicYearNotFoundException")
    void noActiveAcademicYear_throwsException() {
        when(studentRepository.existsByDni(any())).thenReturn(false);
        when(studentRepository.existsByCuil(any())).thenReturn(false);
        when(academicYearRepository.findCurrentYear()).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(buildRequest(), CREATED_BY))
                .isInstanceOf(AcademicYearNotFoundException.class);

        verifyNoInteractions(createUserUseCase, folioAssignmentService);
    }

    @Test
    @DisplayName("GradeLevel no encontrado → lanza GradeLevelNotFoundException")
    void gradeLevelNotFound_throwsException() {
        when(studentRepository.existsByDni(any())).thenReturn(false);
        when(studentRepository.existsByCuil(any())).thenReturn(false);
        when(gradeLevelRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(buildRequest(), CREATED_BY))
                .isInstanceOf(GradeLevelNotFoundException.class);

        verifyNoInteractions(createUserUseCase, folioAssignmentService);
    }

    @Test
    @DisplayName("GradeLevel inactivo → lanza GradeLevelNotFoundException")
    void gradeLevelInactive_throwsException() {
        when(studentRepository.existsByDni(any())).thenReturn(false);
        when(studentRepository.existsByCuil(any())).thenReturn(false);
        when(gradeLevel.getIsActive()).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(buildRequest(), CREATED_BY))
                .isInstanceOf(GradeLevelNotFoundException.class);

        verifyNoInteractions(createUserUseCase, folioAssignmentService);
    }

    @Test
    @DisplayName("sin registry activo → lanza IllegalStateException")
    void noActiveRegistry_throwsIllegalStateException() {
        when(studentRepository.existsByDni(any())).thenReturn(false);
        when(studentRepository.existsByCuil(any())).thenReturn(false);
        when(registryRepository.findActiveRegistryForYear(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(buildRequest(), CREATED_BY))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No active registry");
    }

    @Test
    @DisplayName("happy path — padre nuevo → crea todos los agregados y 2 Users")
    void happyPath_newParent_createsAllAggregates() {
        when(studentRepository.existsByDni(any())).thenReturn(false);
        when(studentRepository.existsByCuil(any())).thenReturn(false);
        when(parentRepository.findByDni(any())).thenReturn(Optional.empty());
        when(createUserUseCase.execute(any()))
                .thenReturn(new CreateUserResponse(UUID.randomUUID()))
                .thenReturn(new CreateUserResponse(UUID.randomUUID()));
        when(parentRepository.save(any())).thenReturn(buildRealParent());

        StudentResponse result = useCase.execute(buildRequest(), CREATED_BY);

        assertThat(result).isNotNull();
        verify(studentRepository).save(any(StudentPersonalData.class));
        verify(healthRecordRepository).save(any(StudentHealthRecord.class));
        verify(studentRecordRepository).save(any(StudentRecord.class));
        verify(enrollmentRepository).save(any(StudentEnrollment.class));
        verify(studentParentRepository).save(any(StudentParent.class));
        verify(createUserUseCase, times(2)).execute(any(CreateUserRequest.class));
    }

    @Test
    @DisplayName("happy path — padre existente → solo 1 User, no guarda Parent, no envía email")
    void happyPath_existingParent_reusesParent() {
        when(studentRepository.existsByDni(any())).thenReturn(false);
        when(studentRepository.existsByCuil(any())).thenReturn(false);
        when(parentRepository.findByDni(any())).thenReturn(Optional.of(buildRealParent()));

        useCase.execute(buildRequest(), CREATED_BY);

        verify(createUserUseCase, times(1)).execute(any(CreateUserRequest.class));
        verify(parentRepository, never()).save(any());
        verify(emailService, never()).sendParentCredentials(any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("User del estudiante se crea con active=true, ROLE_STUDENT y password {DNI}Ipet132!")
    void studentUser_createdWithCorrectParams() {
        when(studentRepository.existsByDni(any())).thenReturn(false);
        when(studentRepository.existsByCuil(any())).thenReturn(false);
        when(parentRepository.findByDni(any())).thenReturn(Optional.of(buildRealParent()));

        useCase.execute(buildRequest(), CREATED_BY);

        ArgumentCaptor<CreateUserRequest> captor =
                ArgumentCaptor.forClass(CreateUserRequest.class);
        verify(createUserUseCase).execute(captor.capture());

        CreateUserRequest req = captor.getValue();
        assertThat(req.dni()).isEqualTo(STUDENT_DNI);
        assertThat(req.roleName()).isEqualTo("ROLE_STUDENT");
        assertThat(req.startActive()).isTrue();
        assertThat(req.plainPassword()).isEqualTo(STUDENT_DNI + "Ipet132!");
    }

    @Test
    @DisplayName("padre nuevo → se envía email de credenciales al padre")
    void newParent_credentialsEmailSent() {
        when(studentRepository.existsByDni(any())).thenReturn(false);
        when(studentRepository.existsByCuil(any())).thenReturn(false);
        when(parentRepository.findByDni(any())).thenReturn(Optional.empty());
        when(createUserUseCase.execute(any()))
                .thenReturn(new CreateUserResponse(UUID.randomUUID()))
                .thenReturn(new CreateUserResponse(UUID.randomUUID()));
        when(parentRepository.save(any())).thenReturn(buildRealParent());

        useCase.execute(buildRequest(), CREATED_BY);

        verify(emailService).sendParentCredentials(
                eq("roberto@example.com"),
                eq("Roberto"),
                eq("García"),
                eq(PARENT_DNI),
                any(String.class)
        );
    }

    @Test
    @DisplayName("fallo de email del padre no revierte la transacción")
    void parentEmailFailure_doesNotPropagateException() {
        when(studentRepository.existsByDni(any())).thenReturn(false);
        when(studentRepository.existsByCuil(any())).thenReturn(false);
        when(parentRepository.findByDni(any())).thenReturn(Optional.empty());
        when(createUserUseCase.execute(any()))
                .thenReturn(new CreateUserResponse(UUID.randomUUID()))
                .thenReturn(new CreateUserResponse(UUID.randomUUID()));
        when(parentRepository.save(any())).thenReturn(buildRealParent());
        doThrow(new RuntimeException("SMTP unreachable"))
                .when(emailService)
                .sendParentCredentials(any(), any(), any(), any(), any());

        assertThat(useCase.execute(buildRequest(), CREATED_BY)).isNotNull();
        verify(studentRepository).save(any(StudentPersonalData.class));
    }
}