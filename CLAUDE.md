# CLAUDE.md — Sistema de Gestión Escolar IPET 132

> Guía de contexto, arquitectura y comportamiento para agentes de IA trabajando en este proyecto.
> **Leer completo antes de modificar cualquier archivo.**

---

## 🎯 Propósito del Proyecto

Sistema de gestión escolar para el **IPET 132** (Argentina).
**Stack:** Java 17 + Spring Boot 3.3.4 + Spring Security 6 + MySQL 8
**Package raíz:** `org.school.management`
**Estado actual:** Auth ✅ + Geography ✅ + Academic ✅ + Students ✅ + Teachers ✅ + Email Service ✅ + Grades ✅ + Course ✅ + Attendance ✅

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
teachers/     → Profesores ✅ COMPLETO
grades/       → Calificaciones ✅ COMPLETO
course/       → Asignación profesor-materia-curso ✅ COMPLETO
attendance/   → Asistencia diaria y por materia ✅ COMPLETO
```

**Regla:** Un bounded context **no importa clases completas de otro**.
Solo se comparten IDs (ej: `GradeLevelId`, `PlaceId`) o tipos del Shared Kernel.

**Excepción documentada 1:** Los controllers importan `User` de `auth/` únicamente para el cast de `@AuthenticationPrincipal` en `extractUserId()`. Es un cruce de infraestructura aceptado — no lógica de negocio.

**Excepción documentada 2:** `ActivateTeacherAccountUseCase` (en `auth/`) importa `TeacherRepository` de `teachers/` para activar ambos agregados en la misma transacción. Es el único use case de `auth/` con esta dependencia cruzada — aceptado por la necesidad de atomicidad.

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
│   │                        getFullName() → "firstName lastName"
│   │                        getLastNameFirst() → "lastName, firstName"
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
├── domain/service/
│   └── EmailService.java       # Puerto del dominio para envío de emails — sin dependencias Spring
└── infrastructure/
    ├── persistence/converter/
    │   └── UuidBinaryConverter.java  # AttributeConverter UUID ↔ BINARY(16) — usar en TODOS los módulos
    ├── email/
    │   └── JavaMailEmailService.java # Implementación SMTP — OCI Email Delivery (dev: Mailhog)
    └── config/
        └── AsyncConfig.java          # Habilita @Async para envío de emails no bloqueante
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
- **Columna en BD:** `students/` usa `residence_place_id`; `teachers/` y `parents/` usan `place_id`

**`FullName.java`**
- `firstName` y `lastName` como componentes del record
- `getFullName()` → `"firstName lastName"` ← usar siempre este método, NO `fullName()`
- `getLastNameFirst()` → `"lastName, firstName"`
- Capitalización automática en `of(firstName, lastName)`

**`Gender.java`**
- Enum puro sin dependencias Spring/JPA → se puede usar directamente en entidades JPA con `@Enumerated(EnumType.STRING)`
- **No crear GenderEntity duplicado** — usar `Gender` del Shared Kernel

**`EmailService.java`** (puerto del dominio)
- `sendEmail(to, subject, body)` — genérico texto plano
- `sendTeacherInvitation(to, firstName, lastName, dni, temporaryPassword, activationLink)` — invitación docente con link
- `sendParentCredentials(to, firstName, lastName, dni, temporaryPassword)` — credenciales padre
- Implementado por `JavaMailEmailService` con `@Async` — nunca bloquea el flujo transaccional
- Fallos de email son silenciosos (log + catch) — nunca rompen la transacción principal

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
public class Teacher {
    @EqualsAndHashCode.Include
    private final TeacherId teacherId;
    private PhoneNumber phone; // mutable via updateContactInfo()

    public static Teacher create(...) {
        // validaciones de dominio aquí — nunca en .builder().build() directo
    }
}
```

### Excepciones de dominio (constructor genérico + factory methods)

```java
public class TeacherNotFoundException extends DomainException {
    public TeacherNotFoundException(String message) { super(message); }
    public static TeacherNotFoundException byId(UUID id) {
        return new TeacherNotFoundException("Teacher not found with id: " + id);
    }
    public static TeacherNotFoundException byDni(String dni) {
        return new TeacherNotFoundException("Teacher not found with DNI: " + dni);
    }
}
```

### Use Case

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class GetTeacherByIdUseCase {
    private final TeacherRepository teacherRepository; // puerto del dominio
    private final TeacherApplicationMapper mapper;
    private final GetPlaceByIdUseCase getPlaceByIdUseCase; // use case de otro BC — permitido

    public TeacherResponse execute(UUID teacherId) {
        Teacher teacher = teacherRepository
                .findByTeacherId(TeacherId.from(teacherId))
                .orElseThrow(() -> TeacherNotFoundException.byId(teacherId));
        return buildResponse(teacher);
    }

    // package-private — reutilizable por otros use cases del mismo módulo
    TeacherResponse buildResponse(Teacher teacher) { ... }
}
```

### Repository (Puerto + Adaptador)

```java
// domain/repository/ ← PUERTO
public interface TeacherRepository {
    Optional<Teacher> findByTeacherId(TeacherId id); // VO, no UUID
    boolean existsByDni(Dni dni);
    boolean existsByCuil(String cuil);
    Teacher save(Teacher teacher);
}

// infrastructure/persistence/adapter/ ← ADAPTADOR
@Component
@RequiredArgsConstructor
public class TeacherRepositoryAdapter implements TeacherRepository {
    private final TeacherJpaRepository jpaRepository;
    private final TeacherPersistenceMapper mapper;
}
```

### Entidad JPA — patrón estándar (con BINARY(16))

```java
@Getter @Setter @NoArgsConstructor
@Entity @Table(name = "teachers")
public class TeacherEntity {

    @Id
    @Convert(converter = UuidBinaryConverter.class)
    @Column(name = "teacher_id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID teacherId;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_status", nullable = false, length = 20)
    private EmploymentStatus employmentStatus;

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

### PersistenceMapper — patrón con default methods (para VOs compuestos)

```java
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TeacherPersistenceMapper {

    default TeacherEntity toEntity(Teacher domain) {
        TeacherEntity entity = new TeacherEntity();
        entity.setTeacherId(domain.getTeacherId().value());
        // Aplanar Address
        if (domain.getAddress() != null) {
            entity.setPlaceId(domain.getAddress().placeId().value()); // place_id en teachers/parents
        }
        return entity;
    }

    default Teacher toDomain(TeacherEntity entity) {
        Address address = null;
        if (entity.getAddressStreet() != null && entity.getPlaceId() != null) {
            address = new Address(..., PlaceId.of(entity.getPlaceId()), ...);
        }
        return Teacher.builder()...build();
    }
}
```

**Reglas del PersistenceMapper:**
- Nombre: `*PersistenceMapper` — distinguir del application mapper y web mapper
- Sin campo estático `INSTANCE` — usar `componentModel = "spring"` (bean Spring)
- VOs compuestos (FullName, Address) → usar `default methods` (patrón teachers/parents)
- VOs de un solo campo → `expression = "java(Vo.of(entity.getCampo()))"`
- **Columna Address:** `students/` usa `residence_place_id`; `teachers/` y `parents/` usan `place_id`

### Controller — patrón estándar

```java
@RestController
@RequestMapping("/api/admin/teachers")
@RequiredArgsConstructor @Slf4j
@Tag(name = "Teachers") @SecurityRequirement(name = "bearerAuth")
public class TeacherController {

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
public final class TeacherWebDto {
    private TeacherWebDto() {}
    public record CreateTeacherWebRequest(...) {}
    public record UpdateTeacherWebRequest(...) {}
    public record TeacherWebResponse(...) {}
    public record TeacherSummaryWebResponse(...) {}
    public record TeacherSearchWebResponse(List<TeacherSummaryWebResponse> teachers, int total) {}
}
```

### ExceptionHandler — patrón con ProblemDetail (RFC 9457)

```java
@RestControllerAdvice @Slf4j
public class TeacherExceptionHandler {
    @ExceptionHandler(TeacherNotFoundException.class)
    public ProblemDetail handleTeacherNotFound(TeacherNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Teacher Not Found");
        problem.setType(URI.create("/errors/teacher-not-found"));
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

### Colisión de nombres entre BCs — patrón Java

Cuando dos clases del mismo simple name deben coexistir en el mismo archivo (ej: `CreateTeacherUseCase` de `teachers/` y de `auth/`), usar **nombre completamente calificado en la declaración del campo**:

```java
// En teachers/application/usecases/CreateTeacherUseCase.java
private final org.school.management.auth.application.usecases.admin.CreateTeacherUseCase authCreateTeacherUseCase;
// Java NO soporta alias en imports — usar FQN en la declaración del campo
```

---

## 📁 Estructura por Bounded Context

### `auth/` ✅ completado

```
auth/
├── domain/model/           User, RefreshToken, BlacklistedToken, Role
│                           — User implementa UserDetails directamente
├── domain/valueobject/     UserId, HashedPassword, PlainPassword, RoleName, RoleId
├── domain/repository/      UserRepository, RefreshTokenRepository, BlacklistedTokenRepository
└── infrastructure/
    ├── web/controller/     AuthController, AdminController, UsersController
    ├── persistence/        entity/, repository/, adapter/, mappers/
    └── security/           SecurityConfig, JwtTokenProvider, JwtAuthenticationFilter
```

**Notas `auth/`:**
- `User` implementa `UserDetails` directamente
- `RoleName.student()`, `.admin()`, `.teacher()`, `.parent()`, `.staff()` — factory methods por rol
- `CreateTeacherUseCase` en `auth/` crea el User con rol TEACHER, genera password temporal y envía email real
- `CreateTeacherResponse` tiene: `userId`, `dni`, `temporaryPassword`, `invitationSent`, `confirmationToken`
- `ActivateTeacherAccountUseCase` activa **tanto** el `User` como el `Teacher` en la misma transacción
- `JwtTokenProvider.generateConfirmationToken()` genera JWT de tipo CONFIRMATION (duración configurable, default 48h)
- Duración configurable via `app.security.jwt.confirmation-token-expiration` (segundos)
- `POST /api/auth/activate-account` — endpoint público, recibe `{ token, newPassword }`

**Flujo de activación de cuenta teacher:**
```
POST /api/admin/teachers
  └─ teachers/CreateTeacherUseCase
       ├─ auth/CreateTeacherUseCase
       │    ├─ crea User (active=false)
       │    ├─ genera confirmationToken JWT (48h)
       │    ├─ EmailService.sendTeacherInvitation(link con token) ← ASYNC
       │    └─ retorna CreateTeacherResponse con confirmationToken
       ├─ Teacher.create(...)
       ├─ teacher.assignActivationToken(confirmationToken)
       └─ teacherRepository.save(teacher)

POST /api/auth/activate-account { token, newPassword }
  └─ ActivateTeacherAccountUseCase
       ├─ valida token JWT (purpose=account_confirmation, type=CONFIRMATION)
       ├─ extrae DNI del token → busca User
       ├─ user.resetPassword(newPassword) + user.activate()
       ├─ userRepository.save(user)
       ├─ teacherRepository.findByDni() → teacher.activate(now)
       └─ teacherRepository.save(teacher)  ← Teacher.active=true, activationToken=null
```

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
- `FolioAssignmentService.assignNextFolio()` busca el registro activo internamente
- `RegistryNumberGenerator.generate(AcademicYearId, int year)` → genera `REG-YYYY-NNNNNN` — exclusivo para `QualificationRegistry`, **nunca** para `StudentRecord`
- `EvaluationPeriod` — hasta 4 períodos por año (`period_number BETWEEN 1 AND 4`). IPET 132 usa **2 períodos cuatrimestrales**

### `students/` ✅ COMPLETO — 5 agregados

```
students/
├── personal/    ✅ COMPLETO — 5 use cases, CreateStudentUseCase 15 pasos
├── health/      ✅ COMPLETO — PATCH semántico, emergency_contact_name concatenado
├── enrollment/  ✅ COMPLETO — cierre de ciclo, baja lógica, estados terminales
├── records/     ✅ COMPLETO — legajo por DNI, workflow aprobación documentos
└── parents/     ✅ COMPLETO — entidad global, vínculo estudiante-padre
```

**Decisiones clave `parents/`:**
- `cuil` es obligatorio — campo `final` en `Parent`, validado en `create()`
- Columna de domicilio: `place_id` (no `residence_place_id` como en students)
- `existsByCuil(String cuil)` en puerto, JPA repository y adapter
- `ParentAlreadyExistsException.withCuil(String)` — factory method disponible

### `teachers/` ✅ COMPLETO

```
teachers/
├── domain/
│   ├── model/       Teacher
│   │                — activate(LocalDateTime), deactivate(), retire()
│   │                — assignActivationToken(String) ← asigna token post-create
│   │                — updateContactInfo(), updatePersonalInfo(), updateProfessionalInfo()
│   │                — isPendingActivation(), isRetired()
│   ├── valueobject/ TeacherId (of/from/generate)
│   │                EmploymentStatus (ACTIVE, INACTIVE, RETIRED — isTerminal())
│   │                EmploymentType (FULL_TIME, PART_TIME, CONTRACT)
│   │                TeacherSpecialization (nullable, max 200 chars)
│   ├── repository/  TeacherRepository (findByTeacherId, findByDni, existsByCuil,
│   │                                   findByLastName, findAll, save)
│   └── exception/   TeacherNotFoundException (byId, byDni)
│                    TeacherAlreadyExistsException (withDni, withCuil)
│                    InvalidTeacherDataException (withReason)
├── application/
│   ├── dto/request/ CreateTeacherRequest (con AddressRequest anidado)
│   │                UpdateTeacherRequest (PATCH semántico — null conserva valor)
│   ├── dto/response/TeacherResponse, TeacherSummaryResponse
│   ├── mapper/      TeacherApplicationMapper (recibe PlaceResponse como parámetros)
│   └── usecases/    GetTeacherByIdUseCase (buildResponse() package-private reutilizable)
│                    CreateTeacherUseCase (delega User en auth/ → asigna token → persiste)
│                    UpdateTeacherUseCase (PATCH semántico por sección)
│                    SearchTeachersUseCase (dni exacto | lastName parcial | todos)
└── infrastructure/
    ├── persistence/ TeacherEntity, TeacherJpaRepository, TeacherPersistenceMapper
    │                (default methods — igual que parents), TeacherRepositoryAdapter
    └── web/         TeacherWebDto (clase contenedora), TeacherWebMapper,
                     TeacherController (4 endpoints), TeacherExceptionHandler
```

**Endpoints teachers:**
| Método | Path | Rol | Descripción |
|--------|------|-----|-------------|
| POST | `/api/admin/teachers` | ADMIN | Crear profesor + enviar email con link activación |
| GET | `/api/admin/teachers/{teacherId}` | ADMIN, STAFF | Obtener por ID |
| GET | `/api/admin/teachers` | ADMIN, STAFF | Buscar (dni / lastName / todos) |
| PATCH | `/api/admin/teachers/{teacherId}` | ADMIN, STAFF | Actualizar datos |

**Notas `teachers/`:**
- Password inicial: aleatorio seguro generado en `auth/CreateTeacherUseCase`
- Cuenta inicia con `active = false` — se activa via `POST /api/auth/activate-account`
- `Teacher.assignActivationToken(token)` — se llama después de `create()`, antes de `save()`
- El email de invitación incluye el link real con el JWT de activación (48h)
- `TeacherPersistenceMapper` debe mapear: `active`, `activationToken`, `activationSentAt`, `activatedAt`

### `grades/` ✅ COMPLETO

```
grades/
├── domain/
│   ├── model/
│   │   ├── Evaluation.java       — gradeEvaluation(), validate(), cancel(), isPassed()
│   │   ├── PeriodGrade.java      — calculateAverage(), adjustGrade(), validate()
│   │   └── FinalGrade.java       — create(), recordExam(), validate(), recordInRegistry()
│   ├── valueobject/
│   │   ├── EvaluationId.java     — record UUID (movido desde academic/)
│   │   ├── EvaluationTypeId.java — record UUID (movido desde academic/)
│   │   ├── EvaluationStatus.java — enum (movido desde academic/)
│   │   ├── FinalGradeId.java     — record UUID
│   │   ├── PeriodGradeId.java    — record UUID
│   │   └── FinalGradeStatus.java — PASSED, FAILED, PENDING_EXAM, FREE, ABSENT
│   ├── repository/
│   │   ├── EvaluationRepository.java
│   │   ├── PeriodGradeRepository.java
│   │   └── FinalGradeRepository.java
│   └── exception/
│       ├── GradeNotFoundException (evaluation, periodGrade, finalGrade, etc.)
│       ├── GradeAlreadyValidatedException (evaluation, periodGrade, finalGrade)
│       ├── InvalidGradeException (withReason, gradeOutOfRange, notInPendingExamStatus)
│       └── GradeAlreadyRecordedInRegistryException (forFinalGrade)
├── application/
│   ├── dto/request/
│   │   ├── CreateEvaluationRequest
│   │   ├── GradeEvaluationRequest
│   │   └── RecordExamGradeRequest
│   ├── dto/response/
│   │   ├── EvaluationResponse
│   │   ├── PeriodGradeResponse
│   │   └── FinalGradeResponse
│   ├── mapper/   GradesApplicationMapper
│   └── usecases/
│       ├── CreateEvaluationUseCase        — TEACHER
│       ├── GradeEvaluationUseCase         — TEACHER
│       ├── ValidateEvaluationUseCase      — STAFF
│       ├── CalculatePeriodGradeUseCase    — STAFF
│       ├── RecordExamGradeUseCase         — STAFF
│       ├── CalculateFinalGradeUseCase     — ADMIN/STAFF
│       └── RecordFinalGradeInRegistryUseCase — ADMIN
└── infrastructure/
    ├── persistence/
    │   ├── entity/    EvaluationEntity, PeriodGradeEntity,
    │   │              FinalGradeEntity, EvaluationTypeEntity
    │   ├── repository/ EvaluationJpaRepository, PeriodGradeJpaRepository,
    │   │               FinalGradeJpaRepository, EvaluationTypeJpaRepository
    │   ├── adapter/   EvaluationRepositoryAdapter, PeriodGradeRepositoryAdapter,
    │   │              FinalGradeRepositoryAdapter
    │   └── mapper/    EvaluationPersistenceMapper, PeriodGradePersistenceMapper,
    │                  FinalGradePersistenceMapper
    ├── web/
    │   ├── dto/       GradesWebDto (clase contenedora)
    │   ├── mapper/    GradesWebMapper
    │   ├── controller/ GradesController (7 endpoints)
    │   └── exception/ GradesExceptionHandler
    └── seeder/        GradesDataSeeder (@Profile("dev"), @Order(10))
```

**Endpoints grades:**
| Método | Path | Rol | Descripción |
|--------|------|-----|-------------|
| POST | `/api/grades/evaluations` | TEACHER | Crear evaluación |
| PATCH | `/api/grades/evaluations/{id}/grade` | TEACHER | Cargar nota |
| PATCH | `/api/grades/evaluations/{id}/validate` | ADMIN, STAFF | Validar evaluación |
| POST | `/api/grades/period-grades/calculate` | ADMIN, STAFF | Calcular nota de período |
| POST | `/api/grades/final-grades/exam` | ADMIN, STAFF | Asentar nota de examen/coloquio |
| POST | `/api/grades/final-grades/calculate` | ADMIN, STAFF | Calcular nota final |
| PATCH | `/api/grades/final-grades/{id}/registry` | ADMIN | Registrar en libro matriz |

**Notas críticas `grades/`:**
- `MIN_PASSING_GRADE = 7` — constante en cada modelo de dominio, nunca hardcodear
- `EvaluationId`, `EvaluationTypeId`, `EvaluationStatus` — movidos de `academic/` a `grades/domain/valueobject/`
- `RecordFinalGradeInRegistryUseCase` obtiene `registryId` y `folioNumber` del `StudentRecord` via `GetRecordByStudentIdUseCase`
- `GradesDataSeeder` siembra 5 tipos de evaluación con UUIDs fijos: `PARCIAL`, `TRABAJO_PRACTICO`, `COLOQUIO`, `EXAMEN_PREVIO`, `EVALUACION_CONTINUA`

### `course/` ✅ COMPLETO

```
course/
├── domain/
│   ├── model/        CourseSubject, StudentCourseSubject
│   ├── valueobject/  CourseSubjectId, StudentCourseSubjectId (record Java 17)
│   │                 CourseStatus (ACTIVE, INACTIVE, COMPLETED — isTerminal())
│   │                 SubjectEnrollmentStatus (ENROLLED, ATTENDING, PASSED, FAILED,
│   │                                          PENDING_EXAM, FREE, WITHDRAWN — isTerminal(), isActive())
│   ├── repository/   CourseSubjectRepository, StudentCourseSubjectRepository
│   └── exception/    CourseSubjectNotFoundException, CourseSubjectAlreadyExistsException
│                     StudentCourseSubjectNotFoundException, StudentAlreadyEnrolledException
├── application/
│   ├── dto/request/  CreateCourseSubjectRequest, AssignTeacherRequest, EnrollStudentRequest
│   ├── dto/response/ CourseSubjectResponse, StudentCourseSubjectResponse
│   ├── mapper/       CourseApplicationMapper
│   └── usecases/     CreateCourseSubjectUseCase, AssignTeacherToCourseUseCase,
│                     EnrollStudentInCourseUseCase, GetCourseSubjectsByGradeLevelUseCase,
│                     GetStudentCoursesUseCase
└── infrastructure/
    ├── persistence/  CourseSubjectEntity, StudentCourseSubjectEntity
    │                 + JpaRepositories + Adapters + PersistenceMappers
    ├── web/          CourseWebDto, CourseWebMapper, CourseController (5 endpoints),
    │                 CourseExceptionHandler
    └── seeder/       CourseDataSeeder (@Profile("dev"), @Order(6))
```

**Endpoints course:**
| Método | Path | Rol | Descripción |
|--------|------|-----|-------------|
| POST | `/api/courses/course-subjects` | ADMIN, STAFF | Crear asignación materia-curso |
| GET | `/api/courses/course-subjects` | ADMIN, STAFF, TEACHER | Listar por curso y año |
| PATCH | `/api/courses/course-subjects/{id}/teacher` | ADMIN, STAFF | Asignar docente |
| POST | `/api/courses/enrollments` | ADMIN, STAFF | Inscribir alumno a materia |
| GET | `/api/courses/enrollments/{enrollmentId}/courses` | ADMIN, STAFF, TEACHER | Materias del alumno |

**Decisiones clave `course/`:**
- `CourseStatus` y `SubjectEnrollmentStatus` viven en `course/domain/valueobject/`
- `StudentCourseSubject` no tiene `attendedClasses` — no está en BD; solo `total_classes`
- `CourseSubject.assignTeacher()` y `updateSchedule()` son métodos mutables — diseño intencional
- Tabla `courses` existe en BD pero no se implementó como entidad — se usa `course_subjects`
- Tests: `CreateCourseSubjectUseCaseTest` (4 tests) + `EnrollStudentInCourseUseCaseTest` (5 tests)

### `attendance/` ✅ COMPLETO

```
attendance/
├── domain/
│   ├── model/
│   │   ├── DailyAttendance.java    — create(), justify(), correct()
│   │   │                             Lista del día tomada por preceptor/STAFF
│   │   ├── CourseAttendance.java   — create(), correct()
│   │   │                             Asistencia por clase, tomada por TEACHER
│   │   └── AttendanceSummary.java  — create(), recalculate(List<CourseAttendance>)
│   │                                 MIN_ATTENDANCE_PERCENTAGE = 85.0
│   │                                 Libre si weightedAbsences/totalClasses > 0.15
│   ├── valueobject/
│   │   ├── DailyAttendanceId.java    — record UUID (of/from/generate)
│   │   ├── CourseAttendanceId.java   — record UUID (of/from/generate)
│   │   ├── AttendanceSummaryId.java  — record UUID (of/from/generate)
│   │   └── AttendanceStatus.java     — enum con absenceWeight
│   │                                   PRESENT=0.0, ABSENT=1.0, JUSTIFIED=1.0,
│   │                                   LATE=0.2, WITHDRAWN=0.2
│   │                                   canBeJustified() → solo ABSENT
│   ├── repository/
│   │   ├── DailyAttendanceRepository
│   │   ├── CourseAttendanceRepository
│   │   └── AttendanceSummaryRepository
│   └── exception/
│       ├── AttendanceAlreadyRecordedException (forDailyAttendance, forCourseAttendance)
│       └── AttendanceNotFoundException (dailyById, courseById, summaryByStudentAndPeriod)
├── application/
│   ├── dto/request/
│   │   ├── RecordDailyAttendanceRequest
│   │   ├── RecordCourseAttendanceRequest
│   │   ├── JustifyAbsenceRequest
│   │   └── CorrectAttendanceRequest
│   ├── dto/response/
│   │   ├── DailyAttendanceResponse
│   │   ├── CourseAttendanceResponse
│   │   └── AttendanceSummaryResponse
│   ├── mapper/   AttendanceApplicationMapper
│   └── usecases/
│       ├── RecordDailyAttendanceUseCase    — STAFF
│       ├── RecordCourseAttendanceUseCase   — TEACHER (recalcula summary en cada carga)
│       ├── JustifyAbsenceUseCase           — STAFF (ABSENT → JUSTIFIED)
│       ├── CorrectAttendanceUseCase        — correctDaily() + correctCourse()
│       ├── GetAttendanceSummaryUseCase     — ADMIN, STAFF, TEACHER
│       └── GetAtRiskStudentsUseCase        — ADMIN, STAFF
└── infrastructure/
    ├── persistence/
    │   ├── entity/   DailyAttendanceEntity, CourseAttendanceEntity,
    │   │             AttendanceSummaryEntity (con @PrePersist/@PreUpdate)
    │   ├── repository/ DailyAttendanceJpaRepository, CourseAttendanceJpaRepository,
    │   │               AttendanceSummaryJpaRepository
    │   ├── adapter/  DailyAttendanceRepositoryAdapter, CourseAttendanceRepositoryAdapter,
    │   │             AttendanceSummaryRepositoryAdapter
    │   └── mapper/   DailyAttendancePersistenceMapper, CourseAttendancePersistenceMapper,
    │                 AttendanceSummaryPersistenceMapper (default methods)
    └── web/
        ├── dto/       AttendanceWebDto (clase contenedora con 8 records)
        ├── mapper/    AttendanceWebMapper
        ├── controller/ AttendanceController (8 endpoints, /api/attendance)
        └── exception/ AttendanceExceptionHandler
```

**Endpoints attendance:**
| Método | Path | Rol | Descripción |
|--------|------|-----|-------------|
| POST | `/api/attendance/daily` | ADMIN, STAFF | Registrar asistencia diaria del curso |
| PATCH | `/api/attendance/daily/{id}/justify` | ADMIN, STAFF | Justificar ausencia (ABSENT→JUSTIFIED) |
| PATCH | `/api/attendance/daily/{id}` | ADMIN, STAFF | Corregir registro diario |
| POST | `/api/attendance/course` | ADMIN, STAFF, TEACHER | Registrar asistencia por materia |
| PATCH | `/api/attendance/course/{id}` | ADMIN, STAFF, TEACHER | Corregir registro por materia |
| GET | `/api/attendance/course/summary` | ADMIN, STAFF, TEACHER | Resumen por alumno/materia/período |
| GET | `/api/attendance/course/at-risk` | ADMIN, STAFF | Alumnos en riesgo de quedar libres |

**Reglas de negocio `attendance/`:**
- `MIN_ATTENDANCE_PERCENTAGE = 85` — constante en `AttendanceSummary`, nunca hardcodear
- `ABSENT` y `JUSTIFIED` tienen el mismo peso (1.0) — la justificación solo registra el motivo
- `recalculate()` se invoca en cada `RecordCourseAttendanceUseCase` y `CorrectAttendanceUseCase`
- `justify()` en `DailyAttendance` lanza `IllegalStateException` si el estado no es `ABSENT`
- El summary se crea automáticamente si no existe al registrar la primera asistencia del período

**Dependencias `attendance/` (solo IDs):**
```java
StudentPersonalDataId  → students/
CourseSubjectId        → course/
StudentCourseSubjectId → course/
GradeLevelId           → academic/
PeriodId               → academic/
AcademicYearId         → academic/
```

**Tests `attendance/`:** 30 tests unitarios
- `AttendanceSummaryTest` (9) — lógica de `recalculate()`, pesos, límite 15%
- `RecordDailyAttendanceUseCaseTest` (4)
- `RecordCourseAttendanceUseCaseTest` (5) — verifica recalculate crea/actualiza summary
- `JustifyAbsenceUseCaseTest` (5) — verifica que solo ABSENT puede justificarse
- `CorrectAttendanceUseCaseTest` (4)
- `GetAtRiskStudentsUseCaseTest` (3)

### `shared/email/` ✅ COMPLETO

**Puerto:** `shared/domain/service/EmailService.java`
**Adaptador:** `shared/infrastructure/email/JavaMailEmailService.java`
**Config:** `shared/infrastructure/config/AsyncConfig.java`

- SMTP configurado para **OCI Email Delivery** (prod) y **Mailhog** (local)
- `@Async` — envío nunca bloquea el hilo transaccional
- Fallos silenciosos — log + catch, nunca propagan excepción
- `sendTeacherInvitation()` incluye `activationLink` — si está vacío, el email indica contactar a administración
- Perfil `local` en `application.yml` con Mailhog: `docker run -p 1025:1025 -p 8025:8025 mailhog/mailhog`

---

## ⚙️ Stack y Versiones

| Tecnología | Versión | Uso |
|------------|---------|-----|
| Java | 17 | Records, Value Objects inmutables, pattern matching |
| Spring Boot | 3.3.4 | Framework principal |
| Spring Security | 6.x | Auth/Authz + JWT |
| Spring Data JPA | (Boot managed) | Persistencia |
| MySQL | 8 | Producción |
| H2 | (test scope) | Tests |
| jjwt | 0.12.6 | JWT access + refresh tokens |
| MapStruct | 1.6.2 | Mapeo type-safe entre capas |
| Lombok | (Boot managed) | Solo en modelos de dominio complejos (@Builder, @Getter) |
| Flyway | (Boot managed) | Migraciones de esquema |
| SpringDoc OpenAPI | 2.5.0 | Swagger UI — ProblemDetail compatible con RFC 9457 |
| JUnit 5 + Mockito | (Boot managed) | Testing |
| spring-boot-starter-mail | (Boot managed) | SMTP via JavaMailSender |

---

## 🔐 Decisiones de Dominio — NO Cambiar Sin Discutir

| Decisión | Razón |
|----------|-------|
| **DNI como username** | Identificador universal en Argentina |
| **DNI siempre 8 dígitos** | Consistente con `Dni.java` del Shared Kernel |
| **Email opcional para estudiantes** | Estudiantes menores no tienen email |
| **Email obligatorio para padres y profesores** | Necesario para notificaciones y credenciales |
| **CUIL obligatorio en teachers y parents** | Identificador fiscal — igual que students |
| **UUID como PK** | Preparado para microservicios |
| **BINARY(16) para UUIDs en BD** | Consistente en todo el proyecto — usar `UuidBinaryConverter` |
| **@Id como UUID + @Convert** | `JpaRepository<Entity, UUID>` transparente — no usar `byte[]` |
| **Records para todos los VOs** | Java 17 nativo — sin Lombok `@Value` |
| **of() como factory method principal** | Estándar del proyecto — `from()` como alias |
| **Lombok solo en modelos complejos** | `@Builder + @Getter` para clases con +10 campos |
| **MapStruct en 3 capas** | Persistence / Application / Web — nunca saltear |
| **PersistenceMapper con default methods** | Para Teachers, Parents y Attendance — VOs compuestos complejos |
| **PersistenceMapper con @AfterMapping** | Para Students — patrón alternativo igualmente válido |
| **Sin INSTANCE estático en mappers Spring** | `componentModel = "spring"` → bean inyectado |
| **Flyway obligatorio** | Nunca `ddl-auto: create` |
| **Shared Kernel** | `Dni`, `Email`, `PhoneNumber`, `Cuil`, `Address`, `Gender` — nunca duplicar |
| **Gender directo en entidades JPA** | Enum puro del Shared Kernel |
| **UuidBinaryConverter en shared/** | Un converter para todos los BCs |
| **@PrePersist / @PreUpdate obligatorios** | Garantizan timestamps nunca nulos |
| **Students en 5 agregados** | personal, health, enrollment, records, parents |
| **CreateStudentRequest unificado** | Un request para el flujo atómico de 15 pasos |
| **RecordNumber = DNI del estudiante** | Identificador global compatible con ministerio |
| **Un legajo por estudiante (no por año)** | El legajo es permanente — el DNI no cambia |
| **RegistryNumberGenerator solo para QualificationRegistry** | Genera REG-YYYY-NNNNNN — nunca para StudentRecord |
| **FolioAssignmentService busca el registro internamente** | `assignNextFolio()` sin parámetros |
| **Sin @OneToMany en StudentRecordEntity** | Evita problemas con BINARY(16) — documentos cargados manualmente |
| **Parent es entidad global** | Un padre puede tener hijos en distintas escuelas |
| **DNI inmutable en Parent y Teacher** | Identificador global — no se puede cambiar |
| **isPrimaryContact exclusivo por estudiante** | Un solo contacto principal — validado en use case |
| **Password padre aleatorio seguro** | Generado con SecureRandom, enviado por email |
| **Password inicial estudiante** | `{DNI}Ipet132!` — simple para el admin |
| **Folio automático** | `FolioAssignmentService` transaccional garantiza unicidad |
| **Baja de estudiante es lógica** | No hay delete físico — via `StudentEnrollment.withdraw()` |
| **Sin delete físico en puertos** | Ningún repositorio de students/teachers expone delete |
| **Address.placeId en teachers/parents** | Columna `place_id` — distinto a `residence_place_id` en students |
| **EmailService en shared/domain/service** | Puerto transversal — usado por teachers, parents y futuro students |
| **@Async en JavaMailEmailService** | Email no bloquea la transacción principal |
| **Email falla silenciosamente** | Log + catch — nunca propaga ni revierte la transacción |
| **Teacher.active = false al crear** | Requiere activación via link en email |
| **confirmationToken JWT (48h) para activación** | Configurable via `app.security.jwt.confirmation-token-expiration` |
| **ActivateTeacherAccountUseCase activa User Y Teacher** | Atomicidad — ambos deben quedar activos en la misma tx |
| **Teacher.assignActivationToken() post-create** | El token lo genera auth/, se propaga y persiste en teachers/ |
| **activate() limpia activationToken** | Token consumido = null — `isPendingActivation()` retorna false |
| **ActivateTeacherAccountUseCase en auth/ importa TeacherRepository** | Excepción documentada — necesario para atomicidad |
| **grades/ como BC separado** | Razón de cambio diferente a academic/ — actores distintos (TEACHER vs ADMIN) |
| **CourseStatus y SubjectEnrollmentStatus en course/** | Igual que EvaluationStatus → grades/; pertenecen al BC que los usa |
| **StudentCourseSubject sin attendedClasses** | Campo no existe en BD — solo total_classes está en course_subjects |
| **CourseSubject con métodos mutables** | assignTeacher() y updateSchedule() mutan estado — diseño intencional, no inmutable |
| **attendance/ como BC separado** | Actores distintos (preceptor/STAFF para lista diaria, TEACHER para por materia); volumen alto de escrituras; razón de cambio diferente a course/ |
| **AttendanceStatus con peso de falta** | Encapsula la regla de negocio en el enum — ABSENT=1.0, JUSTIFIED=1.0, LATE=0.2, WITHDRAWN=0.2 |
| **MIN_ATTENDANCE_PERCENTAGE = 85** | Regla del IPET 132 — constante en AttendanceSummary, nunca hardcodear |
| **JUSTIFIED descuenta igual que ABSENT** | Regla del IPET 132 — la justificación registra el motivo pero no exime la falta |
| **recalculate() en AttendanceSummary** | El summary se recalcula en cada carga/corrección — consistencia garantizada en la transacción |
| **atRisk = weightedAbsences/totalClasses > 0.15** | Condición estricta (>) — exactamente 15% NO es libre |
| **Seeders resuelven place_id en runtime** | Geography usa UUIDs dinámicos (UNHEX UUID()) — searchByName() + filter exact match |
| **UserEntity usa dni como username** | Campo `dni` en UserEntity — findByDni() no findByUsername() |
| **auth.infra (no auth.infrastructure)** | El paquete de auth usa `infra` como abreviación — excepción documentada al estándar del proyecto |
| **ProblemDetail para errores HTTP** | RFC 9457, nativo en Spring 6 |
| **User implementa UserDetails directo** | Cast via pattern matching Java 17 en `extractUserId()` |
| **FQN para clases con mismo nombre** | Java no soporta alias en imports — usar nombre completamente calificado |

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
- **Usar factory methods de excepción** — `TeacherNotFoundException.byDni(dni)` no `new TeacherNotFoundException(...)`.
- **Usar `of()` en VOs** — todos tienen `of()` como factory method principal.
- **DTOs en subcarpetas** — `dto/request/` y `dto/response/`, nunca en `dto/` directamente.
- **Cruzar BCs via use case público** — `GetPlaceByIdUseCase`, nunca `PlaceRepository` desde Teachers.
- **Usar `UuidBinaryConverter`** en todos los campos UUID de entidades JPA.
- **Usar `@PrePersist` / `@PreUpdate`** en entidades con timestamps.
- **Nombrar mappers de persistencia** como `*PersistenceMapper`.
- **Usar `default methods`** para VOs compuestos en persistence mapper (patrón teachers/parents/attendance).
- **Usar `ProblemDetail`** en todos los `@RestControllerAdvice`.
- **`RecordNumber.fromDni(dni)`** al crear un legajo — nunca usar `RegistryNumberGenerator` para esto.
- **Inyectar `EmailService`** en use cases que crean usuarios — teachers y parents.
- **Tests en `src/test/java/`** — mismo paquete que producción pero bajo `test/`, no `main/`.
- **`getFullName()`** para obtener el nombre completo de `FullName` — nunca `fullName()`.
- **Llamar `teacher.assignActivationToken(token)`** después de `Teacher.create()` y antes de `teacherRepository.save()`.
- **Verificar que `TeacherPersistenceMapper`** mapea `activationToken`, `activationSentAt`, `activatedAt`, `active`.

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
- **Nunca hacer delete físico** de estudiantes/padres/profesores — la baja es lógica.
- **Nunca usar `RegistryNumberGenerator`** para generar números de legajo.
- **Nunca crear `GenderEntity`** u otros enums duplicados del Shared Kernel.
- **Nunca poner `INSTANCE = Mappers.getMapper(...)`** en mappers con `componentModel = "spring"`.
- **Nunca tipar el `@Id` como `byte[]`** — usar UUID con `@Convert(UuidBinaryConverter.class)`.
- **Nunca usar `infra`** como nombre de paquete — usar `infrastructure` completo.
- **Nunca exponer delete** en puertos de repositorios de students/teachers/parents.
- **Nunca usar alias en imports Java** — Java no lo soporta; usar FQN en declaración del campo.
- **Nunca llamar `fullName()`** en `FullName` — el método correcto es `getFullName()`.
- **Nunca dejar que un fallo de email** rompa o revierta una transacción de negocio.
- **Nunca hardcodear `900`** como duración del confirmation token — usar `confirmationTokenExpirationSeconds`.
- **Nunca activar solo el `User`** en `ActivateTeacherAccountUseCase` — siempre activar también el `Teacher`.

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
13. `infrastructure/persistence/mapper/` — `XPersistenceMapper` con default methods
14. `infrastructure/web/dto/` — clase contenedora `XWebDto` con todos los records web
15. `infrastructure/web/controller/` — REST con `@PreAuthorize`, `extractUserId` via pattern matching
16. `infrastructure/web/mapper/` — `XWebMapper` application ↔ web DTO
17. `infrastructure/web/exception/` — `@RestControllerAdvice` con `ProblemDetail`
18. `infrastructure/seeder/` — datos iniciales para perfil `dev`
19. `db/migration/V{n}__create_{context}_tables.sql`

### 🧪 Al generar tests

```java
// Ruta SIEMPRE en src/test/java/ — mismo paquete que producción
// src/test/java/org/school/management/teachers/application/usecases/GetTeacherByIdUseCaseTest.java

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class GetTeacherByIdUseCaseTest {
    @Mock private TeacherRepository teacherRepository;
    @Mock private TeacherApplicationMapper mapper;
    @Mock private GetPlaceByIdUseCase getPlaceByIdUseCase;
    @InjectMocks private GetTeacherByIdUseCase useCase;

    @Test
    void execute_whenTeacherExists_thenReturnResponse() { ... }
    @Test
    void execute_whenTeacherNotFound_thenThrowTeacherNotFoundException() { ... }
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
| V11 | `document_types`, `student_records`, `record_documents` |
| V12 | `parents`, `student_parents` (incluye `cuil` en `parents`) |
| V13 | `teachers` |
| V14 | `withdrawal_reasons`, `student_enrollments` |
| V15 | `courses`, `course_subjects`, `student_course_subjects` |
| V17 | `evaluation_types`, `evaluations`, `period_grades`, `final_grades` |
| V19 | `countries`, `provinces` — datos Argentina (seed SQL) |
| V20 | `places` — localidades Argentina (seed SQL) |
| V21 | `attendance_daily_records`, `attendance_course_records`, `attendance_period_summaries` ✅ |

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
mvn spring-boot:run -Dspring-boot.run.profiles=local        # con Mailhog local
mvn clean package -DskipTests                               # generar JAR

# Mailhog para desarrollo local de emails
docker run -p 1025:1025 -p 8025:8025 mailhog/mailhog
# UI disponible en http://localhost:8025
```

---

## 🔑 Credenciales de Prueba (perfil `dev`)

| Rol | DNI | Password |
|-----|-----|----------|
| ADMIN | `00000001` | `Admin123!` |
| TEACHER | `12345678` | `Teacher123!` (Juan García — Matemática) |
| TEACHER | `23456789` | `Teacher123!` (María López — Física) |
| TEACHER | `34567890` | `Teacher123!` (Carlos Fernández — Electrotecnia) |
| STUDENT (con email) | `11223344` | `11223344Ipet132!` (Lucas Romero — 1°A) |
| STUDENT (sin email) | `87654321` | `87654321Ipet132!` (Sofía Torres — 1°A) |
| STUDENT | `44556677` | `44556677Ipet132!` (Martín Díaz — 4°A Electricista) |
| STUDENT | `55667788` | `55667788Ipet132!` (Ana Gómez — 4°C Electromecánico) |
| PARENT | `98765432` | `Parent123!` (Roberto Romero) |

---

## ⏳ Estado del Proyecto

### ✅ Completado

- `auth/` — JWT, refresh, blacklist, sesiones, creación de usuarios, activación de cuenta teacher
- `geography/` — Geografía argentina con búsqueda y jerarquía
- `academic/` — Años, orientaciones, cursos, materias, registro de calificaciones, 22 use cases
- `shared/email/` — EmailService (puerto + JavaMailEmailService con OCI SMTP + AsyncConfig)
- **`students/` — COMPLETO** — 5 agregados de punta a punta
- **`teachers/` — COMPLETO** — domain + application + infrastructure + 4 endpoints + flujo activación por email
- **`parents/` — COMPLETO** — cuil agregado en todas las capas, placeId consistente
- **`grades/` — COMPLETO** — domain + application + infrastructure + 7 endpoints + seeder + 19 tests
- **`course/` — COMPLETO** — domain + application + infrastructure + 5 endpoints + seeder + 9 tests
- **`attendance/` — COMPLETO** — domain + application + infrastructure + 7 endpoints + V21 + 30 tests
- **Flujo activación teacher** — confirmationToken (48h), email con link, ActivateTeacherAccountUseCase activa User+Teacher
- **`AcademicDataSeeder`** — 2 años lectivos, 2 orientaciones, 64 materias, 22 cursos, 2 registros
- **`CourseDataSeeder`** — ~152 CourseSubjects para 2025, @Order(6)
- **`TeacherDataSeeder`** — 3 docentes activados, @Order(7)
- **`StudentAndParentDataSeeder`** — 4 alumnos + 4 padres, @Order(8)
- **Tests unitarios** — 80 tests: teachers (11), parents (11), grades (19), course (9), attendance (30)
- Flyway V1–V7, V10–V15, V17, V19, V20, V21

### ⏳ Pendiente

- [ ] Tests de activación teacher — `ActivateTeacherAccountUseCaseTest` (5 casos), `auth/CreateTeacherUseCaseTest` actualizado
- [ ] Tests unitarios para students (`CreateStudentUseCase` — 15 pasos)
- [ ] Rate limiting, auditoría, métricas

### 🎯 Próximos pasos sugeridos

**A) Tests de activación teacher** *(cierra el trabajo de este chat)*
```
ActivateTeacherAccountUseCaseTest
  ├─ token inválido → InvalidTokenException
  ├─ token válido pero user no encontrado → UserNotFoundException
  ├─ token válido pero no es TEACHER → InvalidOperationException
  ├─ teacher no encontrado → TeacherNotFoundException
  └─ happy path → User.active=true + Teacher.active=true + token=null
```

**B) Tests para `CreateStudentUseCase`** *(el flujo más complejo — 15 pasos atómicos)*

**C) Rate limiting / auditoría** *(infraestructura transversal)*