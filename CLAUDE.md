# CLAUDE.md — Sistema de Gestión Escolar IPET 132

> Guía de contexto, arquitectura y comportamiento para agentes de IA trabajando en este proyecto.
> **Leer completo antes de modificar cualquier archivo.**

---

## 🎯 Propósito del Proyecto

Sistema de gestión escolar para el **IPET 132** (Argentina).
**Stack:** Java 17 + Spring Boot 3.2.x + Spring Security 6 + MySQL 8
**Package raíz:** `org.school.management`
**Estado actual:** Auth ✅ + Geography ✅ + Academic ✅ + Students ✅ COMPLETO (personal + health + enrollment + records + parents)

---

## 🏗️ Arquitectura — CRÍTICO LEER ANTES DE MODIFICAR

Este proyecto implementa **tres principios arquitectónicos simultáneamente**:

### 1. Arquitectura Hexagonal (Ports & Adapters)

```
DOMAIN (core)     → Entidades, Value Objects, Domain Services, Repository Interfaces (puertos)
APPLICATION       → Use Cases, Application DTOs (Records), Application Mappers
INFRASTRUCTURE    → Controllers REST, Entidades JPA, Adapters, Persistence Mappers, Security
```

- El **dominio no depende de nada externo** — nunca importar `jakarta.persistence`, `org.springframework` en `domain/`.
- Las **interfaces de repositorio** viven en `domain/repository/` e implementadas en `infrastructure/persistence/adapter/`.
- Los **Use Cases** son la única puerta de entrada a la lógica de negocio.
- Existen **tres capas de mappers MapStruct**: persistence, application y web — nunca saltear capas.

### 2. Vertical Slicing (Bounded Contexts)

```
shared/       → Shared Kernel — NUNCA duplicar estos tipos en otros contextos
auth/         → Autenticación y autorización ✅
geography/    → Países, provincias, localidades ✅
academic/     → Años, cursos, materias, registro de calificaciones ✅
students/     → Estudiantes, salud, matrícula, legajo, padres ✅ COMPLETO
teachers/     → Profesores (futuro)
```

**Regla:** Un bounded context **no importa clases completas de otro**.
Solo se comparten IDs (ej: `GradeLevelId`, `PlaceId`) o tipos del Shared Kernel.

**Excepción documentada:** Los controllers importan `User` de `auth/` únicamente para el cast de `@AuthenticationPrincipal` en `extractUserId()`. Es un cruce de infraestructura aceptado — no lógica de negocio.

### 3. Screaming Architecture

La estructura de paquetes comunica el dominio de negocio, no el framework.
Un paquete dice **qué hace**, no cómo está implementado.

---

## 📦 Shared Kernel — Nunca Duplicar

**Ubicación:** `org.school.management.shared`

```
shared/
├── person/domain/valueobject/
│   ├── Dni.java           # DNI argentino — 8 dígitos, validado
│   ├── FullName.java      # record: firstName + lastName, capitalización automática
│   ├── Gender.java        # Enum: MALE, FEMALE, OTHER — usar directo en entidades JPA (@Enumerated)
│   ├── Nationality.java
│   ├── PhoneNumber.java
│   ├── Email.java
│   ├── Cuil.java          # CUIL argentino — validación dígito verificador ANSES/AFIP
│   ├── CuilType.java      # Enum: MALE_ARGENTINEAN, FEMALE_ARGENTINEAN, LEGAL_ENTITY...
│   └── Address.java       # Domicilio postal — encapsula street, number, floor, apt, PlaceId, CP
├── geography/domain/valueobject/
│   ├── CountryId.java     # record UUID — of() + from(UUID) + from(String) + generate()
│   ├── ProvinceId.java    # record UUID — of() + from(UUID) + from(String) + generate()
│   └── PlaceId.java       # record UUID — of() + from(UUID) + from(String) + generate()
├── domain/exception/
│   └── DomainException.java    # Clase base abstracta — nunca lanzar directamente
└── infrastructure/persistence/converter/
    └── UuidBinaryConverter.java  # AttributeConverter UUID ↔ BINARY(16) — usar en TODOS los módulos
```

**Regla de oro:** Si un concepto aparece en más de un bounded context, va al Shared Kernel.

### Notas sobre Value Objects del Shared Kernel

**`Cuil.java`**
- Valida prefijos ANSES/AFIP: `20`, `27`, `23`, `24`, `30`, `33`, `34`
- Valida dígito verificador con el algoritmo oficial (pesos `5,4,3,2,7,6,5,4,3,2`)
- Normaliza a 11 dígitos sin guiones internamente
- `extractDni()` → devuelve el `Dni` embebido en el CUIL
- `formatted()` → formato `XX-XXXXXXXX-X` para display
- `getType()` → devuelve `CuilType`

**`Address.java`**
- Encapsula domicilio completo: `street`, `number`, `floor` (opt), `apartment` (opt), `PlaceId` (obligatorio), `postalCode` (opt)
- `street` se normaliza automáticamente (capitalización, abreviaturas Av., Dr., Gral., etc.)
- `number` acepta formato `1234` o `567B`
- El persistence mapper es responsable de **aplanar** los campos de `Address` a las columnas de BD
- `toStringFormatted(String localityName)` para documentos/PDFs — requiere nombre de localidad resuelto externamente
- **No override de `equals`/`hashCode`** — el `record` compara todos los campos (semántica correcta)

**`FullName.java`**
- `firstName` y `lastName` como componentes del record
- `getFullName()` → `"firstName lastName"`
- `getLastNameFirst()` → `"lastName, firstName"`
- Capitalización automática en `of(firstName, lastName)`

**`Gender.java`**
- Enum puro sin dependencias Spring/JPA → se puede usar directamente en entidades JPA con `@Enumerated(EnumType.STRING)`
- **No crear GenderEntity duplicado** — usar `Gender` del Shared Kernel

**`UuidBinaryConverter.java`**
- `jakarta.persistence.AttributeConverter<UUID, byte[]>` — convierte UUID ↔ BINARY(16)
- Formato Big-Endian estándar (compatible con MySQL `UUID_TO_BIN` sin flag swap)
- Usar en **todos** los campos UUID de todas las entidades JPA del proyecto:
  ```java
  @Convert(converter = UuidBinaryConverter.class)
  @Column(name = "student_id", columnDefinition = "BINARY(16)")
  private UUID studentId;
  ```
- El `@Id` también lleva `@Convert` → `JpaRepository<Entity, UUID>` funciona transparente

---

## 📐 Convenciones de Código — CRÍTICO

### Value Object (record) — estándar del proyecto

```java
public record YearLevel(int value) {
    public YearLevel {
        if (value < 1 || value > 7)
            throw new IllegalArgumentException("Year level must be between 1 and 7");
    }
    public static YearLevel of(int value) { return new YearLevel(value); }
    public boolean requiresOrientation() { return value >= 4; }
}
```

### ID Value Object (record) — patrón estándar con of() y from()

```java
public record AcademicYearId(UUID value) {
    public AcademicYearId {
        if (value == null) throw new IllegalArgumentException("AcademicYearId cannot be null");
    }
    public static AcademicYearId of(UUID value)  { return new AcademicYearId(value); }
    public static AcademicYearId generate()       { return new AcademicYearId(UUID.randomUUID()); }
    public static AcademicYearId from(UUID uuid)  { return new AcademicYearId(uuid); }
    public static AcademicYearId from(String id) {
        try { return new AcademicYearId(UUID.fromString(id)); }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid AcademicYearId format: " + id);
        }
    }
    public String asString() { return value.toString(); }
}
```

**Regla:** Todos los IDs tienen `of(UUID)`, `from(UUID)`, `from(String)` y `generate()`.
`of()` es el factory method principal. `from()` es alias para compatibilidad.
**Nunca usar Lombok `@Value` en Value Objects** — usar `record` de Java 17.

### Modelo de dominio con Lombok (cuando tiene muchos campos)

```java
@Getter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class StudentPersonalData {
    @EqualsAndHashCode.Include
    private final StudentPersonalDataId studentId;
    private PhoneNumber phone; // mutable via updateContactInfo()

    public static StudentPersonalData create(StudentPersonalDataBuilder builder) {
        // validaciones de dominio aquí — nunca en .builder().build() directo
    }
}
```

### Excepciones de dominio (constructor genérico + factory methods)

```java
public class StudentNotFoundException extends DomainException {
    public StudentNotFoundException(String message) { super(message); }
    public static StudentNotFoundException byId(UUID id) {
        return new StudentNotFoundException("Student not found with id: " + id);
    }
    public static StudentNotFoundException byDni(String dni) {
        return new StudentNotFoundException("Student not found with DNI: " + dni);
    }
}
```

### Use Case

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class GetStudentByIdUseCase {
    private final StudentPersonalDataRepository studentRepository; // puerto del dominio
    private final StudentPersonalDataApplicationMapper mapper;
    private final GetPlaceByIdUseCase getPlaceByIdUseCase; // use case de otro BC — permitido

    public StudentResponse execute(UUID studentId) {
        StudentPersonalData student = studentRepository
                .findByStudentId(StudentPersonalDataId.from(studentId))
                .orElseThrow(() -> StudentNotFoundException.byId(studentId));
        return buildResponse(student);
    }
}
```

### Repository (Puerto + Adaptador)

```java
// domain/repository/ ← PUERTO
public interface StudentPersonalDataRepository {
    Optional<StudentPersonalData> findByStudentId(StudentPersonalDataId id); // VO, no UUID
    boolean existsByDni(Dni dni);
    boolean existsByCuil(String cuil);
    StudentPersonalData save(StudentPersonalData student);
}

// infrastructure/persistence/adapter/ ← ADAPTADOR
@Component
@RequiredArgsConstructor
public class StudentPersonalDataRepositoryAdapter implements StudentPersonalDataRepository {
    private final StudentPersonalDataJpaRepository jpaRepository;
    private final StudentPersonalDataPersistenceMapper mapper;
}
```

### Entidad JPA — patrón estándar (con BINARY(16))

```java
@Getter @Setter @NoArgsConstructor
@Entity @Table(name = "student_personal_data")
public class StudentPersonalDataEntity {

    @Id
    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "student_id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID studentId;

    // Enums del Shared Kernel — directo con @Enumerated, sin duplicar
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 10)
    private Gender gender;

    // Timestamps — siempre con @PrePersist / @PreUpdate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onPrePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
    }

    @PreUpdate
    protected void onPreUpdate() { updatedAt = LocalDateTime.now(); }
}
```

### PersistenceMapper — patrón con @AfterMapping para VOs compuestos

```java
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StudentPersonalDataPersistenceMapper {

    @Mapping(source = "fullName.firstName", target = "firstName")
    @Mapping(source = "address.street",     target = "addressStreet")
    StudentPersonalDataEntity toEntity(StudentPersonalData domain);

    @Mapping(target = "fullName", ignore = true)
    @Mapping(target = "address",  ignore = true)
    @Mapping(target = "studentId", expression = "java(StudentPersonalDataId.of(entity.getStudentId()))")
    StudentPersonalData toDomain(StudentPersonalDataEntity entity);

    @AfterMapping
    default void buildCompositeValueObjects(StudentPersonalDataEntity entity,
            @MappingTarget StudentPersonalData.StudentPersonalDataBuilder builder) {
        builder.fullName(FullName.of(entity.getFirstName(), entity.getLastName()));
        builder.address(new Address(entity.getAddressStreet(), entity.getAddressNumber(),
                entity.getAddressFloor(), entity.getAddressApartment(),
                PlaceId.of(entity.getResidencePlaceId()), entity.getPostalCode()));
    }
}
```

**Reglas del PersistenceMapper:**
- Nombre: `*PersistenceMapper` — distinguir del application mapper y web mapper
- Sin campo estático `INSTANCE` — usar `componentModel = "spring"` (bean Spring)
- VOs de un solo campo → `expression = "java(Vo.of(entity.getCampo()))"`
- VOs compuestos (FullName, Address) → `ignore = true` en `toDomain()` + `@AfterMapping`
- Para mappers con VOs muy complejos (múltiples IDs anidados) → usar métodos `default` en lugar de anotaciones

### Controller — patrón estándar

```java
@RestController
@RequestMapping("/api/admin/students")
@RequiredArgsConstructor @Slf4j
@Tag(name = "Students") @SecurityRequirement(name = "bearerAuth")
public class StudentController {

    // Extrae userId del principal — User de auth/ implementa UserDetails directamente
    private UUID extractUserId(UserDetails userDetails) {
        if (userDetails instanceof User user) {
            return user.getUserId().value();
        }
        throw new IllegalStateException("Principal inesperado: " + userDetails.getClass().getName());
    }
}
```

### Web DTOs — patrón con clase contenedora

```java
// infrastructure/web/dto/StudentWebDto.java
public final class StudentWebDto {
    private StudentWebDto() {}

    public record CreateStudentWebRequest(...) {
        public record HealthDataWebRequest(...) {}
        public record ParentWebRequest(...) {}
    }
    public record StudentWebResponse(...) {}
    public record StudentSummaryWebResponse(...) {}
}
```

### ExceptionHandler — patrón con ProblemDetail (RFC 9457)

```java
@RestControllerAdvice @Slf4j
public class StudentExceptionHandler {
    @ExceptionHandler(StudentNotFoundException.class)
    public ProblemDetail handleStudentNotFound(StudentNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Student Not Found");
        problem.setType(URI.create("/errors/student-not-found"));
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}
```

**Códigos HTTP estándar del proyecto:**
- `404` → `*NotFoundException`
- `409` → `*AlreadyExistsException`, `DuplicatePrimaryContactException`
- `422` → `Invalid*Exception`, `IllegalArgumentException` del dominio
- `500` → `IllegalStateException` (estado inválido del sistema)

---

## 📁 Estructura por Bounded Context

### `auth/` ✅ completado

```
auth/
├── domain/
│   ├── model/           User, RefreshToken, BlacklistedToken, Role
│   │                    — User implementa UserDetails directamente
│   ├── valueobject/     UserId, HashedPassword, PlainPassword, RoleName, RoleId,
│   │                    BlacklistedTokenId
│   ├── repository/      UserRepository, RefreshTokenRepository, BlacklistedTokenRepository
│   └── exception/       InvalidPasswordException, UserNotActiveException
├── application/
│   ├── usecases/        Login, Logout, Refresh, ChangePassword, GetProfile,
│   │                    GetActiveSessions, RevokeSession, admin/CreateStudent, admin/CreateTeacher
│   ├── dto/             Records Java 17
│   └── mappers/         AuthApplicationMapper (MapStruct)
└── infrastructure/
    ├── web/controller/  AuthController, AdminController, UsersController
    ├── persistence/     entity/, repository/, adapter/, mappers/
    └── security/        SecurityConfig, JwtTokenProvider, JwtAuthenticationFilter,
                         CustomUserDetailsService, AuthenticationConfig, PasswordEncoderConfig
```

**Notas `auth/`:**
- `User` implementa `UserDetails` directamente
- `RoleName.student()`, `.admin()`, `.teacher()`, `.parent()`, `.staff()` — factory methods por rol
- `PlainPassword` — valida fortaleza en constructor compacto
- `HashedPassword.PasswordEncoder` — interfaz inyectable, implementada por Spring Security

### `geography/` ✅ completado

```
geography/
├── domain/model/        Country, Province, Place, PlaceWithHierarchy
├── application/
│   ├── usecases/        GetPlaceById (retorna PlaceResponse), SearchPlaces, ...
│   └── dto/response/    PlaceResponse (con jerarquía: provinceName, countryName)
└── infrastructure/
    ├── web/controller/  GeographyController (público), GeographyAdminController (ADMIN)
    └── seeder/          GeographyDataSeeder — Argentina: 1 país, 24 provincias, ~45 lugares
```

**Notas `geography/`:**
- `GetPlaceByIdUseCase.execute(GetPlaceByIdRequest)` → retorna `PlaceResponse`
- `PlaceResponse` tiene: `placeId`, `name`, `provinceName`, `countryName`, `provinceCode`, `countryIsoCode`, `fullAddress`, `fullDescription`

### `academic/` ✅ completado

```
academic/
├── domain/
│   ├── model/           AcademicYear, Orientation, GradeLevel, Subject,
│   │                    StudyPlan, EvaluationPeriod, QualificationRegistry
│   ├── valueobject/ids/ AcademicYearId, GradeLevelId, OrientationId, SubjectId,
│   │                    RegistryId, StudyPlanId, PeriodId, WithdrawalReasonId
│   ├── repository/      AcademicYearRepository (findCurrentYear()),
│   │                    GradeLevelRepository, QualificationRegistryRepository
│   └── service/         FolioAssignmentService ← CRÍTICO: assignNextFolio() @Transactional
│                        RegistryNumberGenerator ← generate(AcademicYearId, int year) → String
│                                                  usado SOLO para QualificationRegistry
└── infrastructure/
    └── seeder/          AcademicDataSeeder — 2 años, 2 orientaciones, 37 cursos, ~60 materias
```

**Notas críticas `academic/`:**
- `FolioAssignmentService.assignNextFolio()` busca el registro activo internamente, incrementa el contador atómicamente y devuelve el folio
- `RegistryNumberGenerator.generate(AcademicYearId, int year)` → genera `REG-YYYY-NNNNNN` — exclusivo para `QualificationRegistry`, **nunca** para `StudentRecord`

### `students/` ✅ COMPLETO — 5 agregados

```
students/
├── personal/    ✅ COMPLETO
├── health/      ✅ COMPLETO
├── enrollment/  ✅ COMPLETO
├── records/     ✅ COMPLETO
└── parents/     ✅ COMPLETO
```

#### `students/personal/` ✅ COMPLETO

```
students/personal/
├── domain/
│   ├── model/       StudentPersonalData
│   ├── valueobject/ StudentPersonalDataId (of(UUID) + from(UUID|String) + generate())
│   ├── repository/  StudentPersonalDataRepository
│   └── exception/   StudentNotFoundException, StudentAlreadyExistsException,
│                    InvalidStudentDataException
├── application/
│   ├── dto/request/   CreateStudentRequest (con HealthDataRequest + ParentRequest nested)
│   │                  UpdateStudentRequest
│   ├── dto/response/  StudentResponse, StudentSummaryResponse
│   ├── mapper/        StudentPersonalDataApplicationMapper
│   └── usecases/      GetStudentByIdUseCase (buildResponse() package-private reutilizable)
│                      GetStudentByDniUseCase, SearchStudentsUseCase,
│                      UpdateStudentUseCase, CreateStudentUseCase (15 pasos ✅ COMPLETO)
└── infrastructure/
    ├── persistence/   entity, JpaRepository, adapter, PersistenceMapper (@AfterMapping)
    └── web/           controller (5 endpoints), StudentWebDto, mapper, exception handler
```

**CreateStudentUseCase — 15 pasos completos:**
```
1-2.  Validar unicidad DNI y CUIL
3.    Obtener AcademicYear activo
4.    Validar GradeLevel activo
5.    Asignar folio → FolioAssignmentService.assignNextFolio()
6.    Generar password estudiante → {DNI}Ipet132!
7.    Crear User (rol STUDENT)
8.    Crear StudentPersonalData
9.    Crear StudentHealthRecord
10.   Obtener QualificationRegistry activo
11.   Crear StudentRecord (recordNumber = DNI del estudiante)
12.   Buscar Parent por DNI → si no existe: crear User (PARENT) + crear Parent
13.   Crear StudentParent (relationship, isPrimaryContact, isAuthorizedPickup)
14.   Crear StudentEnrollment
15.   Commit → retornar StudentResponse
```

#### `students/health/` ✅ COMPLETO

- `StudentHealthRecord` — domain + application + infrastructure completos
- `BloodType.fromString(String)` — busca por displayName (A+, B-, etc.)
- `emergency_contact_name` — columna única concatenada "firstName lastName" (decisión V10)
- Endpoints: `GET /api/admin/students/{studentId}/health`, `PATCH /api/admin/students/{studentId}/health`

#### `students/enrollment/` ✅ COMPLETO

```
students/enrollment/
├── domain/
│   ├── model/       StudentEnrollment (complete(), withdraw(), graduate())
│   ├── valueobject/ EnrollmentId, EnrollmentStatus, EnrollmentType
│   ├── repository/  StudentEnrollmentRepository (VOs en puertos, sin delete físico)
│   └── exception/   EnrollmentNotFoundException, EnrollmentAlreadyCompletedException,
│                    EnrollmentAlreadyWithdrawnException, InvalidEnrollmentCompletionException,
│                    InvalidEnrollmentException
├── application/
│   ├── dto/         UpdateEnrollmentRequest, EnrollmentResponse
│   ├── mapper/      StudentEnrollmentApplicationMapper
│   └── usecases/    GetEnrollmentByStudentIdUseCase, GetActiveEnrollmentUseCase,
│                    UpdateEnrollmentUseCase (cierre de ciclo + baja)
└── infrastructure/
    ├── persistence/ entity, JpaRepository (JPQL para active/completed checks), adapter, mapper
    └── web/         EnrollmentWebDto, mapper, controller (3 endpoints), exception handler
```

**Endpoints enrollment:**
| Método | Path | Rol |
|--------|------|-----|
| GET | `/api/admin/students/{studentId}/enrollments` | ADMIN, STAFF |
| GET | `/api/admin/students/{studentId}/enrollments/{academicYearId}` | ADMIN, STAFF |
| PATCH | `/api/admin/students/{studentId}/enrollments/{enrollmentId}` | ADMIN |

**Notas `enrollment/`:**
- `EnrollmentId` — 4 factory methods: `of(UUID)`, `from(UUID)`, `from(String)`, `generate()`
- Sin delete físico en el puerto — la baja es lógica via `enrollment.withdraw()`
- `UpdateEnrollmentUseCase` soporta dos operaciones mutuamente excluyentes: cierre de ciclo (`finalAverage`) o baja (`withdrawalReasonId`)

#### `students/records/` ✅ COMPLETO

```
students/records/
├── domain/
│   ├── model/       StudentRecord (addDocument, removeDocument, approve, reject, submitForReview)
│   │                RecordDocument (approve, reject, updateMetadata)
│   │                DocumentType
│   ├── valueobject/ RecordId, RecordNumber, DocumentId, DocumentTypeId, DocumentTypeCode,
│   │                DocumentStatus, DocumentCategory, RecordStatus
│   ├── repository/  StudentRecordRepository (findByStudentId — un legajo por estudiante)
│   └── exception/   RecordNotFoundException, DocumentNotFoundException,
│                    RecordAlreadyApprovedException, DocumentAlreadyApprovedException,
│                    IncompleteRecordException, RecordNotReadyForApprovalException
├── application/
│   ├── dto/         AddDocumentRequest, UpdateRecordStatusRequest,
│   │                RecordDocumentResponse, StudentRecordResponse
│   ├── mapper/      StudentRecordApplicationMapper (métodos default)
│   └── usecases/    GetRecordByStudentIdUseCase (buildResponse() reutilizable),
│                    AddDocumentToRecordUseCase, ReviewDocumentUseCase,
│                    UpdateRecordStatusUseCase (SUBMIT/APPROVE/REJECT)
└── infrastructure/
    ├── persistence/ StudentRecordEntity, RecordDocumentEntity,
    │                StudentRecordJpaRepository, RecordDocumentJpaRepository,
    │                StudentRecordPersistenceMapper (métodos default),
    │                StudentRecordRepositoryAdapter (sync manual de documentos)
    └── web/         RecordWebDto, RecordWebMapper, RecordController (4 endpoints),
                     RecordExceptionHandler (7 handlers)
```

**Decisiones clave `records/`:**
- `RecordNumber` = DNI del estudiante (8 dígitos) — único y permanente, no cambia entre años
- Un estudiante tiene **un único legajo** — no uno por año
- `StudentRecordRepository.findByStudentId()` es la búsqueda principal
- Sin `@OneToMany` en entidades — documentos se cargan manualmente via `RecordDocumentJpaRepository`
- `StudentRecordRepositoryAdapter.save()` sincroniza documentos manualmente (elimina los removidos, guarda los actuales)

**Endpoints records:**
| Método | Path | Rol |
|--------|------|-----|
| GET | `/api/admin/students/{studentId}/record` | ADMIN, STAFF |
| POST | `/api/admin/students/{studentId}/record/documents` | ADMIN, STAFF |
| PATCH | `/api/admin/students/{studentId}/record/documents/{documentId}` | ADMIN, STAFF |
| PATCH | `/api/admin/students/{studentId}/record/status` | ADMIN |

#### `students/parents/` ✅ COMPLETO

```
students/parents/
├── domain/
│   ├── model/       Parent (entidad global — compartida entre escuelas)
│   │                StudentParent (vínculo estudiante-padre con flags operativos)
│   ├── valueobject/ ParentId, StudentParentId, ParentRelationship
│   │                (FATHER, MOTHER, GUARDIAN, GRANDPARENT, SIBLING, OTHER)
│   ├── repository/  ParentRepository, StudentParentRepository
│   └── exception/   ParentNotFoundException, ParentAlreadyExistsException,
│                    DuplicatePrimaryContactException, InvalidParentDataException
├── application/
│   ├── dto/         CreateParentRequest, UpdateParentRequest, LinkParentRequest,
│   │                ParentResponse, StudentParentResponse
│   ├── mapper/      ParentApplicationMapper (métodos default)
│   └── usecases/    GetParentsByStudentIdUseCase, CreateParentUseCase,
│                    UpdateParentUseCase (PATCH por sección), LinkParentToStudentUseCase
└── infrastructure/
    ├── persistence/ ParentEntity, StudentParentEntity,
    │                ParentJpaRepository, StudentParentJpaRepository,
    │                ParentPersistenceMapper (Address aplanado/reconstruido),
    │                ParentRepositoryAdapter, StudentParentRepositoryAdapter
    └── web/         ParentWebDto, ParentWebMapper, ParentController (4 endpoints),
                     ParentExceptionHandler (5 handlers)
```

**Decisiones clave `parents/`:**
- `Parent` es entidad global — un padre puede tener hijos en distintas escuelas del sistema
- DNI es identificador global del padre — único e inmutable
- Email **obligatorio** para padres — necesario para notificaciones y credenciales
- Password inicial del padre: aleatorio seguro (pendiente email service)
- `isPrimaryContact` es exclusivo por estudiante — validado en `LinkParentToStudentUseCase`
- `StudentParent` contiene los flags operativos específicos del vínculo (no del padre)

**Endpoints parents:**
| Método | Path | Rol |
|--------|------|-----|
| POST | `/api/admin/parents` | ADMIN, STAFF |
| PATCH | `/api/admin/parents/{parentId}` | ADMIN, STAFF |
| GET | `/api/admin/students/{studentId}/parents` | ADMIN, STAFF |
| POST | `/api/admin/students/{studentId}/parents` | ADMIN, STAFF |

---

## ⚙️ Stack y Versiones

| Tecnología | Versión | Uso |
|------------|---------|-----|
| Java | 17 | Records, Value Objects inmutables, pattern matching |
| Spring Boot | 3.2.x | Framework principal |
| Spring Security | 6.x | Auth/Authz + JWT |
| Spring Data JPA | (Boot managed) | Persistencia |
| MySQL | 8 | Producción |
| H2 | (test scope) | Tests |
| jjwt | 0.12.3 | JWT access + refresh tokens |
| MapStruct | 1.5.5.Final | Mapeo type-safe entre capas |
| Lombok | (Boot managed) | Solo en modelos de dominio complejos (@Builder, @Getter) |
| Flyway | (Boot managed) | Migraciones de esquema |
| SpringDoc OpenAPI | latest | Swagger UI — ProblemDetail compatible con RFC 9457 |
| JUnit 5 + Mockito | (Boot managed) | Testing |

---

## 🔐 Decisiones de Dominio — NO Cambiar Sin Discutir

| Decisión | Razón |
|----------|-------|
| **DNI como username** | Identificador universal en Argentina |
| **DNI siempre 8 dígitos** | Consistente con `Dni.java` del Shared Kernel |
| **Email opcional para estudiantes** | Estudiantes menores no tienen email |
| **Email obligatorio para padres** | Necesario para notificaciones y credenciales |
| **UUID como PK** | Preparado para microservicios |
| **BINARY(16) para UUIDs en BD** | Consistente en todo el proyecto — usar `UuidBinaryConverter` |
| **@Id como UUID + @Convert** | `JpaRepository<Entity, UUID>` transparente — no usar `byte[]` |
| **Records para todos los VOs** | Java 17 nativo — sin Lombok `@Value` |
| **of() como factory method principal** | Estándar del proyecto — `from()` como alias |
| **Lombok solo en modelos complejos** | `@Builder + @Getter` para clases con +10 campos |
| **MapStruct en 3 capas** | Persistence / Application / Web — nunca saltear |
| **PersistenceMapper con @AfterMapping** | Para VOs compuestos (FullName, Address) |
| **PersistenceMapper con default methods** | Para mappers con múltiples IDs anidados complejos |
| **Sin INSTANCE estático en mappers Spring** | `componentModel = "spring"` → bean inyectado |
| **Flyway obligatorio** | Nunca `ddl-auto: create` |
| **Shared Kernel** | `Dni`, `Email`, `PhoneNumber`, `Cuil`, `Address`, `Gender` — nunca duplicar |
| **Gender directo en entidades JPA** | Enum puro del Shared Kernel |
| **UuidBinaryConverter en shared/** | Un converter para todos los BCs |
| **@PrePersist / @PreUpdate obligatorios** | Garantizan timestamps nunca nulos |
| **Students en 5 agregados** | personal, health, enrollment, records, parents |
| **CreateStudentRequest unificado** | Un request para el flujo atómico de 15 pasos |
| **RecordNumber = DNI del estudiante** | Identificador global compatibles con ministerio y otras escuelas |
| **Un legajo por estudiante (no por año)** | El legajo es permanente — el DNI no cambia |
| **StudentRecord.findByStudentId()** | Búsqueda principal del legajo — no por año |
| **RegistryNumberGenerator solo para QualificationRegistry** | Genera REG-YYYY-NNNNNN — nunca para StudentRecord |
| **FolioAssignmentService busca el registro internamente** | `assignNextFolio()` sin parámetros — encapsula la búsqueda |
| **Sin @OneToMany en StudentRecordEntity** | Evita problemas con BINARY(16) converter — documentos cargados manualmente |
| **Parent es entidad global** | Un padre puede tener hijos en distintas escuelas |
| **DNI inmutable en Parent** | Identificador global — no se puede cambiar |
| **isPrimaryContact exclusivo por estudiante** | Un solo contacto principal — validado en use case |
| **Password padre aleatorio seguro** | Pendiente email service — generado con SecureRandom |
| **Password inicial estudiante** | `{DNI}Ipet132!` — simple para el admin |
| **Folio automático** | `FolioAssignmentService` transaccional garantiza unicidad |
| **Baja de estudiante es lógica** | No hay delete físico — via `StudentEnrollment.withdraw()` |
| **Sin delete físico en puertos** | Ningún repositorio de students expone delete |
| **Address encapsula PlaceId** | Domicilio completo como VO en Shared Kernel |
| **Excepciones: constructor + factory methods** | `byId()`, `byDni()`, `withDni()` — flexibilidad |
| **ProblemDetail para errores HTTP** | RFC 9457, nativo en Spring 6 |
| **User implementa UserDetails directo** | No hay wrapper — cast via pattern matching Java 17 |
| **extractUserId via instanceof User** | En todos los controllers — seguro y explícito |
| **WebDto clase contenedora** | Todos los web DTOs del módulo en un archivo |
| **Naming adapter vs impl** | Auth usa `*RepositoryImpl`; módulos nuevos usan `*RepositoryAdapter` |

---

## 🤖 Instrucciones para el Agente

### ✅ Siempre hacer

- **Respetar las tres capas** — dominio → aplicación → infraestructura, sin saltear.
- **Usar los puertos del dominio** en use cases — nunca inyectar `JpaRepository` en `application/`.
- **Un Use Case por operación** — no agrupar lógicas distintas.
- **MapStruct para todos los mapeos** — nunca mapear manualmente en lógica de negocio.
- **Flyway para cambios de esquema** — nueva migración `V{n}__descripcion.sql`, nunca editar existentes.
- **`@Slf4j` para logging** — nunca `System.out.println`.
- **Validar con Jakarta** en `infrastructure/web/dto/` — el dominio lanza sus propias excepciones.
- **Consultar el Shared Kernel** antes de crear un Value Object — puede ya existir.
- **Usar `FolioAssignmentService`** al asignar folios — nunca hacerlo manualmente.
- **Usar factory methods de excepción** — `StudentNotFoundException.byDni(dni)` no `new StudentNotFoundException(...)`.
- **Usar `of()` en VOs** — todos tienen `of()` como factory method principal.
- **DTOs en subcarpetas** — `dto/request/` y `dto/response/`, nunca en `dto/` directamente.
- **Cruzar BCs via use case público** — `GetPlaceByIdUseCase`, nunca `PlaceRepository` desde Students.
- **Usar `UuidBinaryConverter`** en todos los campos UUID de entidades JPA.
- **Usar `@PrePersist` / `@PreUpdate`** en entidades con timestamps.
- **Nombrar mappers de persistencia** como `*PersistenceMapper`.
- **Usar `@AfterMapping` o métodos `default`** para VOs compuestos en persistence mapper.
- **Usar `ProblemDetail`** en todos los `@RestControllerAdvice`.
- **`RecordNumber.fromDni(dni)`** al crear un legajo — nunca usar `RegistryNumberGenerator` para esto.

### ❌ Nunca hacer

- **Nunca importar** `jakarta.persistence.*` o `org.springframework.*` en `domain/`.
- **Nunca exponer** entidades JPA en la API — siempre DTO via mapper.
- **Nunca inyectar** `JpaRepository` directamente en un Use Case.
- **Nunca poner lógica de negocio** en controllers ni en entidades JPA.
- **Nunca modificar** migraciones Flyway ya ejecutadas.
- **Nunca usar** `ddl-auto: create` o `update`.
- **Nunca hardcodear** secretos, contraseñas o URLs.
- **Nunca cruzar** bounded contexts con clases completas — solo IDs o Shared Kernel.
- **Nunca duplicar** `Dni`, `Email`, `PhoneNumber`, `Cuil`, `Address`, `Gender` — están en `shared/`.
- **Nunca usar Lombok `@Value`** en Value Objects — usar `record` de Java 17.
- **Nunca poner validaciones de negocio** en DTOs — van en el dominio.
- **Nunca incluir `studentId`** en el body del UpdateStudentRequest — va como `@PathVariable`.
- **Nunca hacer delete físico** de estudiantes/padres — la baja es lógica.
- **Nunca usar `RegistryNumberGenerator`** para generar números de legajo — solo para `QualificationRegistry`.
- **Nunca crear `GenderEntity`** u otros enums duplicados del Shared Kernel.
- **Nunca poner `INSTANCE = Mappers.getMapper(...)`** en mappers con `componentModel = "spring"`.
- **Nunca tipar el `@Id` como `byte[]`** — usar UUID con `@Convert(UuidBinaryConverter.class)`.
- **Nunca usar `infra`** como nombre de paquete — usar `infrastructure` completo.
- **Nunca exponer delete** en puertos de repositorios de students — sin delete físico.

### 🔍 Al analizar código existente

1. Identificar a qué bounded context y capa pertenece el archivo.
2. Verificar si la modificación cruza alguna frontera arquitectónica.
3. Si se detecta una violación existente, mencionarla pero **no corregirla** salvo pedido explícito.
4. Respetar el naming del módulo: `*RepositoryImpl` (auth), `*RepositoryAdapter` (módulos nuevos).
5. Verificar que los VOs usen `of()` y no `from()` como factory method principal.
6. Verificar que el paquete sea `infrastructure` (no `infra`).

### 🧩 Al crear un nuevo Bounded Context

Orden estricto de implementación:

1. `domain/model/` — entidades del dominio con métodos de negocio
2. `domain/valueobject/` — value objects con validación en constructor compacto (records)
3. `domain/repository/` — interfaces (puertos) — reciben VOs, no tipos primitivos
4. `domain/exception/` — excepciones semánticas por entidad con factory methods
5. `domain/service/` — domain services (lógica que cruza agregados)
6. `application/dto/request/` — Records con validaciones Jakarta
7. `application/dto/response/` — Records puros
8. `application/usecases/` — un archivo por caso de uso
9. `application/mapper/` — MapStruct domain → response DTO
10. `infrastructure/persistence/entity/` — entidades JPA sin lógica, con UuidBinaryConverter y @PrePersist
11. `infrastructure/persistence/repository/` — `XJpaRepository extends JpaRepository<Entity, UUID>`
12. `infrastructure/persistence/adapter/` — `XRepositoryAdapter implements XRepository`
13. `infrastructure/persistence/mapper/` — `XPersistenceMapper` con @AfterMapping o default methods
14. `infrastructure/web/dto/` — clase contenedora `XWebDto` con todos los records web
15. `infrastructure/web/controller/` — REST con `@PreAuthorize`, `extractUserId` via pattern matching
16. `infrastructure/web/mapper/` — `XWebMapper` application ↔ web DTO
17. `infrastructure/web/exception/` — `@RestControllerAdvice` con `ProblemDetail`
18. `infrastructure/seeder/` — datos iniciales para perfil `dev`
19. `db/migration/V{n}__create_{context}_tables.sql`

### 🔗 Dependencias permitidas en `students/`

```java
// ✅ Permitido — solo IDs y Shared Kernel
import org.school.management.shared.person.domain.valueobject.Dni;
import org.school.management.shared.person.domain.valueobject.Cuil;
import org.school.management.shared.person.domain.valueobject.Address;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.academic.domain.valueobject.ids.GradeLevelId;
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.shared.geography.domain.valueobject.PlaceId;
// ✅ Permitido — use cases públicos de otros BCs (no repositorios)
import org.school.management.geography.application.usecases.GetPlaceByIdUseCase;
// ✅ Permitido — SOLO en infrastructure/web/controller/ para extractUserId
import org.school.management.auth.domain.model.User;

// ❌ Prohibido — clases completas de otro bounded context
import org.school.management.academic.domain.model.GradeLevel; // NUNCA
import org.school.management.geography.domain.repository.PlaceRepository; // NUNCA
```

### 🧪 Al generar tests

```java
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class GetStudentByIdUseCaseTest {
    @Mock private StudentPersonalDataRepository studentRepository;
    @Mock private StudentPersonalDataApplicationMapper mapper;
    @Mock private GetPlaceByIdUseCase getPlaceByIdUseCase;
    @InjectMocks private GetStudentByIdUseCase useCase;

    @Test
    void execute_whenStudentExists_thenReturnResponse() { ... }
    @Test
    void execute_whenStudentNotFound_thenThrowStudentNotFoundException() { ... }
}
```

---

## 🗄️ Migraciones Flyway

| Versión | Contenido |
|---------|-----------|
| V1 | Tabla `users` |
| V2 | Tabla `blacklisted_tokens` |
| V3 | Admin por defecto (dev) |
| V4 | Tabla `refresh_tokens` |
| V5 | `countries`, `provinces`, `places` |
| V6 | `academic_years`, `orientations`, `grade_levels`, `subjects`, `qualification_registries` |
| V7 | `study_plans`, `evaluation_periods`, extensiones academic |
| V10 | `student_personal_data`, `student_health_records` |
| V11 | `document_types`, `student_records` (record_number = DNI, único por estudiante), `record_documents` |
| V12 | `parents`, `student_parents` |
| V14 | `withdrawal_reasons`, `student_enrollments` |

**Convenciones de BD:**
- PK: `BINARY(16)` — `@Convert(UuidBinaryConverter.class)` en entidades, `@Id` como `UUID`
- Timestamps: `TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` + `@PrePersist`/`@PreUpdate`
- Enums: `VARCHAR` con `@Enumerated(EnumType.STRING)` — usar enums del Shared Kernel directamente
- Nunca modificar migraciones ya ejecutadas — siempre crear `V{n+1}`

---

## 🛠️ Comandos

```bash
mvn clean verify                                            # compilar + tests
mvn test -Dgroups="unit"                                    # unit tests
mvn test -Dgroups="integration"                             # integration tests
mvn test jacoco:report                                      # coverage
mvn spring-boot:run -Dspring-boot.run.profiles=dev          # ejecutar con seeders
mvn clean package -DskipTests                               # generar JAR
```

---

## 🔑 Credenciales de Prueba (perfil `dev`)

| Rol | DNI | Password |
|-----|-----|----------|
| ADMIN | `00000001` | `Admin123!` |
| TEACHER | `12345678` | `Teacher123!` |
| STUDENT (con email) | `11223344` | `11223344Ipet132!` |
| STUDENT (sin email) | `87654321` | `87654321Ipet132!` |

---

## ⏳ Estado del Proyecto

### ✅ Completado

- `auth/` — JWT, refresh, blacklist, sesiones, creación de usuarios
- `geography/` — Geografía argentina con búsqueda y jerarquía
- `academic/` — Años, orientaciones, cursos, materias, registro de calificaciones, 22 use cases
- **Refactor global de VOs** — todos los `@Value` class de Lombok migrados a `record` de Java 17
- `shared/infrastructure/persistence/converter/UuidBinaryConverter` — converter compartido
- **`students/` — COMPLETO** — los 5 agregados implementados de punta a punta:
    - `personal/` — 5 use cases, CreateStudentUseCase 15 pasos completos
    - `health/` — PATCH semántico, emergency_contact_name concatenado
    - `enrollment/` — cierre de ciclo, baja, estados terminales
    - `records/` — legajo por DNI, workflow de aprobación de documentos
    - `parents/` — entidad global, vínculo estudiante-padre, contacto principal exclusivo
- Flyway V1–V7, V10–V12, V14

### ⏳ Pendiente

- [ ] `teachers/` — asignación a cursos ← **próximo**
- [ ] Calificaciones por período y promedio final
- [ ] Email service — credenciales para padres (password aleatorio ya generado, falta envío)
- [ ] Rate limiting, auditoría, métricas
- [ ] Tests unitarios e integración
- [ ] Seeders para `students/` y `parents/`

### 🎯 Próximo paso — `teachers/`

```
teachers/
├── domain/
│   ├── model/       Teacher
│   ├── valueobject/ TeacherId, TeacherSpecialty
│   ├── repository/  TeacherRepository
│   └── exception/   TeacherNotFoundException, TeacherAlreadyExistsException
├── application/
│   ├── dto/         CreateTeacherRequest, UpdateTeacherRequest, TeacherResponse
│   ├── mapper/      TeacherApplicationMapper
│   └── usecases/    GetTeacherByIdUseCase, CreateTeacherUseCase, UpdateTeacherUseCase,
│                    AssignTeacherToSubjectUseCase
└── infrastructure/
    ├── persistence/ entity, JpaRepository, adapter, mapper
    └── web/         TeacherWebDto, mapper, controller, exception handler
```