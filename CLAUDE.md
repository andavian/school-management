# CLAUDE.md — Sistema de Gestión Escolar IPET 132

> Guía de contexto, arquitectura y comportamiento para agentes de IA trabajando en este proyecto.
> **Leer completo antes de modificar cualquier archivo.**

---

## 🎯 Propósito del Proyecto

Sistema de gestión escolar para el **IPET 132** (Argentina).
**Stack:** Java 17 + Spring Boot 3.3.4 + Spring Security 6 + MySQL 8
**Package raíz:** `org.school.management`
**Estado actual:** Auth ✅ + Geography ✅ + Academic ✅ + Students ✅ + Teachers ✅ + Email Service ✅ + Grades ✅ + Course ✅ + Attendance ✅ + Rate Limiting ✅ + Storage (OCI) ✅ parcial

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
shared/              → Shared Kernel — NUNCA duplicar estos tipos en otros contextos
auth/                → Autenticación y autorización ✅
geography/           → Países, provincias, localidades ✅
academic/            → Años, cursos, materias, registro de calificaciones ✅
students/            → Estudiantes, salud, matrícula, legajo, padres ✅ COMPLETO
teachers/            → Profesores ✅ COMPLETO
grades/              → Calificaciones ✅ COMPLETO
course/              → Asignación profesor-materia-curso ✅ COMPLETO
attendance/          → Asistencia diaria y por materia ✅ COMPLETO
storage/             → Almacenamiento de archivos en OCI Object Storage ✅ COMPLETO
teaching-materials/  → Material didáctico de profesores ⏳ PENDIENTE
```

**Regla:** Un bounded context **no importa clases completas de otro**.
Solo se comparten IDs (ej: `GradeLevelId`, `PlaceId`) o tipos del Shared Kernel.

**Excepción documentada 1:** Los controllers de `students/`, `teachers/` y cualquier BC que necesite el userId del usuario autenticado llaman a `SecurityContextHelper.extractUserId(userDetails)` de `auth/infra/web/`. Es un cruce de infraestructura aceptado y centralizado — no lógica de negocio.

**Excepción documentada 2:** `auth/` usa `infra` como nombre de paquete en lugar de `infrastructure` — excepción histórica documentada, no replicar en nuevos BCs.

**Deuda técnica documentada:** `RecordController` tiene `extractUserId()` duplicado en lugar de usar `SecurityContextHelper` — no corregir sin task específica.

### 3. Screaming Architecture

La estructura de paquetes comunica el dominio de negocio, no el framework.
Un paquete dice **qué hace**, no cómo está implementado.

---

## 📦 Shared Kernel — Nunca Duplicar

**Ubicación:** `org.school.management.shared`

```
shared/
├── person/domain/valueobject/
│   ├── Dni.java           # DNI argentino — 7 u 8 dígitos, SIN dígito verificador
│   │                        (el DNI argentino es correlativo, no tiene verificador)
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
├── domain/event/
│   ├── DomainEvent.java        # Interfaz base — eventId() + occurredOn()
│   ├── AccountActivatedEvent.java  # Publicado por ActivateAccountUseCase — of(userId, dni, roleName)
│   └── DomainEventPublisher.java   # Puerto — publish(DomainEvent) — sin dependencias Spring
├── domain/service/
│   └── EmailService.java       # Puerto del dominio para envío de emails — sin dependencias Spring
└── infrastructure/
    ├── persistence/converter/
    │   └── UuidBinaryConverter.java  # AttributeConverter UUID ↔ BINARY(16) — usar en TODOS los módulos
    ├── event/
    │   └── SpringDomainEventPublisher.java  # Implementa DomainEventPublisher via ApplicationEventPublisher
    ├── email/
    │   └── JavaMailEmailService.java # Implementación SMTP — OCI Email Delivery (dev: Mailhog)
    └── config/
        └── AsyncConfig.java          # Habilita @Async para envío de emails no bloqueante
```

**Regla de oro:** Si un concepto aparece en más de un bounded context, va al Shared Kernel.

### Notas sobre Value Objects del Shared Kernel

**`Dni.java`**
- 7 u 8 dígitos numéricos — SIN validación de dígito verificador
- El DNI argentino es correlativo — el prefijo solo distingue extranjeros (90+)
- Normaliza quitando ceros a la izquierda en DNIs de 8 dígitos que empiecen con 0

**`Cuil.java`**
- Valida prefijos ANSES/AFIP: `20`, `27`, `23`, `24`, `30`, `33`, `34`
- Valida dígito verificador con el algoritmo oficial (pesos `5,4,3,2,7,6,5,4,3,2`)
- Normaliza a 11 dígitos sin guiones internamente
- `extractDni()` → devuelve el `Dni` embebido en el CUIL
- `formatted()` → formato `XX-XXXXXXXX-X` para display
- `getType()` → devuelve `CuilType`
- **CUILs válidos para tests:** `20123456786` (DNI 12345678), `20876543215` (DNI 87654321)

**`Address.java`**
- Encapsula domicilio completo: `street`, `number`, `floor` (opt), `apartment` (opt), `PlaceId` (obligatorio), `postalCode` (opt)
- El persistence mapper es responsable de **aplanar** los campos de `Address` a las columnas de BD
- **Columna en BD:** `students/` usa `residence_place_id`; `teachers/` y `parents/` usan `place_id`

**`FullName.java`**
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

**`DomainEvent.java`** (interfaz base)
- `eventId()` → UUID único para trazabilidad y deduplicación
- `occurredOn()` → LocalDateTime del momento en que ocurrió
- Todos los eventos se implementan como `record` Java 17 con factory method `of(...)`

**`AccountActivatedEvent.java`**
- Publicado por `ActivateAccountUseCase` cuando un usuario activa su cuenta
- Campos: `eventId`, `occurredOn`, `userId`, `dni`, `roleName`
- `roleName` como `String` — no depende del enum `RoleName` de `auth/` para no crear acoplamiento
- Factory method: `AccountActivatedEvent.of(userId, dni, roleName)`

**`DomainEventPublisher.java`** (puerto)
- `publish(DomainEvent event)` — sin imports de Spring
- Implementado por `SpringDomainEventPublisher` en `shared/infrastructure/event/`
- Los listeners usan `@TransactionalEventListener(phase = BEFORE_COMMIT)` para atomicidad

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
        if (domain.getAddress() != null) {
            entity.setPlaceId(domain.getAddress().placeId().value());
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
    // Extraer userId via SecurityContextHelper — nunca duplicar este método
    // SecurityContextHelper.extractUserId(userDetails) centraliza el cast User → UUID
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

---

## 📁 Estructura por Bounded Context

### `auth/` ✅ completado

```
auth/
├── domain/model/           User, RefreshToken, BlacklistedToken, Role
│                           — User implementa UserDetails directamente
├── domain/valueobject/     UserId, HashedPassword, PlainPassword, RoleName, RoleId
│                           UnauthorizedException  ← en domain/exception/
├── domain/repository/      UserRepository, RefreshTokenRepository, BlacklistedTokenRepository
├── application/usecases/   LoginUseCase, ChangePasswordUseCase, GetUserProfileUseCase
│                           CreateUserUseCase            ← factory puro de User, agnóstico del rol
│                           ActivateAccountUseCase       ← activa User + publica AccountActivatedEvent
│                           GenerateConfirmationTokenUseCase ← JWT 48h para activación
└── infrastructure/
    ├── web/
    │   ├── SecurityContextHelper.java  ← extractUserId(UserDetails) — static, centralizado
    │   └── controllers/    AuthController
    ├── persistence/        entity/, repository/, adapter/, mappers/
    └── security/           SecurityConfig, JwtTokenProvider, JwtAuthenticationFilter
                            ratelimit/RateLimitFilter, ratelimit/RateLimitProperties
```

**Notas `auth/`:**
- `User` implementa `UserDetails` directamente
- `RoleName.student()`, `.admin()`, `.teacher()`, `.parent()`, `.staff()` — factory methods por rol
- `auth/` ya **no** crea teachers ni students directamente — cada BC orquesta su propio flujo
- `CreateUserUseCase` recibe `CreateUserRequest` con factory methods `active()` e `inactive()`
- `CreateUserRequest` usa campo `startActive` (no `active`) — factory methods `active()` e `inactive()`
- `ActivateAccountUseCase` activa el `User` y publica `AccountActivatedEvent` — no conoce `teachers/`
- `GenerateConfirmationTokenUseCase` encapsula `JwtTokenProvider` — ningún BC externo lo usa directamente
- `POST /api/auth/activate-account` — endpoint público, recibe `{ token, newPassword }`
- `UnauthorizedException` vive en `auth/domain/exception/` — no como clase interna de `AuthController`

**`SecurityContextHelper`:**
```java
// auth/infra/web/SecurityContextHelper.java
public final class SecurityContextHelper {
    public static UUID extractUserId(UserDetails userDetails) {
        if (userDetails instanceof User user) return user.getUserId().value();
        throw new IllegalStateException("Principal inesperado: " + userDetails.getClass().getName());
    }
}
```
Todos los controllers del proyecto llaman a este método estático — nunca duplicarlo.

**Rate Limiting — `auth/infra/security/ratelimit/`:**
- `RateLimitFilter` — filtro Bucket4j in-memory, por IP, aplicado antes del JWT filter
- `RateLimitProperties` — configuración externalizada via `app.rate-limit.*`
- Endpoints protegidos: `POST /api/auth/login`, `POST /api/auth/activate-account`, `POST /api/auth/refresh-token`
- Límites: login 5/min prod (100/min dev), activate-account 3/min prod, refresh-token 10/min prod
- Respuesta `429 Too Many Requests` con header `Retry-After` y body JSON
- Rate limiting desactivado en perfil `test` via `app.rate-limit.enabled: false`
- Dependencia: `com.bucket4j:bucket4j-core:8.10.1`

**Flujo de creación y activación de teacher:**
```
POST /api/admin/teachers
  └─ teachers/CreateTeacherUseCase (orquestador completo)
       ├─ valida unicidad DNI y CUIL en teachers/
       ├─ genera password temporal segura (SecureRandom, 12 chars)
       ├─ auth/CreateUserUseCase.execute(CreateUserRequest.inactive(dni, password, "ROLE_TEACHER"))
       ├─ auth/GenerateConfirmationTokenUseCase.execute(dni) → confirmationToken JWT 48h
       ├─ Teacher.create(...) + teacher.assignActivationToken(confirmationToken)
       ├─ teacherRepository.save(teacher)
       └─ EmailService.sendTeacherInvitation(link con token) ← ASYNC, falla silenciosamente

POST /api/auth/activate-account { token, newPassword }
  └─ auth/ActivateAccountUseCase
       ├─ valida token JWT
       ├─ user.resetPassword(newPassword) + user.activate()
       ├─ userRepository.save(user)
       └─ eventPublisher.publish(AccountActivatedEvent.of(userId, dni, roleName))
            └─ teachers/TeacherAccountActivatedListener @TransactionalEventListener(BEFORE_COMMIT)
                 ├─ filtra roleName == "ROLE_TEACHER"
                 ├─ teacher.activate(LocalDateTime.now())
                 └─ teacherRepository.save(teacher)
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
- `EvaluationPeriod` — hasta 4 períodos por año. IPET 132 usa **2 períodos cuatrimestrales**

### `students/` ✅ COMPLETO — 5 agregados

```
students/
├── personal/    ✅ COMPLETO — 5 use cases, CreateStudentUseCase 15 pasos
├── health/      ✅ COMPLETO — PATCH semántico
├── enrollment/  ✅ COMPLETO — cierre de ciclo, baja lógica, estados terminales
├── records/     ✅ COMPLETO — legajo por DNI, workflow aprobación documentos
│                             + UploadRecordDocumentUseCase (subida a OCI)
│                             + RecordDocumentRepository (puerto + adapter)
└── parents/     ✅ COMPLETO — entidad global, vínculo estudiante-padre
```

**Decisiones clave `students/records/`:**
- `RecordDocument.filePath` guarda el `objectName` de OCI (para delete y presigned URLs)
- `RecordDocument.fileName` guarda la URL pública de OCI (para acceso directo)
- `RecordDocumentRepository` — puerto en `domain/repository/`, adapter en `infrastructure/persistence/adapter/`
- `StudentRecordPersistenceMapper` — mapea `fileSizeBytes` del dominio a `fileSize` en la entidad JPA
- Endpoint de upload: `POST /api/admin/students/{studentId}/record/{recordId}/upload` (multipart/form-data)
- Tipos permitidos: `application/pdf`, `image/jpeg`, `image/png` — máx 10 MB

**Decisiones clave `parents/`:**
- `cuil` es obligatorio — campo `final` en `Parent`, validado en `create()`
- Columna de domicilio: `place_id` (no `residence_place_id` como en students)

### `teachers/` ✅ COMPLETO

```
teachers/
├── domain/
│   ├── model/       Teacher
│   │                — activate(LocalDateTime), deactivate(), retire()
│   │                — assignActivationToken(String)
│   │                — updateContactInfo(), updatePersonalInfo(), updateProfessionalInfo()
│   │                — isPendingActivation(), isRetired()
│   ├── valueobject/ TeacherId, EmploymentStatus (ACTIVE, INACTIVE, RETIRED),
│   │                EmploymentType (FULL_TIME, PART_TIME, CONTRACT), TeacherSpecialization
│   ├── repository/  TeacherRepository
│   └── exception/   TeacherNotFoundException (byId, byDni)
│                    TeacherAlreadyExistsException (withDni, withCuil)
│                    InvalidTeacherDataException (withReason)
├── application/
│   ├── dto/         CreateTeacherRequest, UpdateTeacherRequest, TeacherResponse, TeacherSummaryResponse
│   ├── mapper/      TeacherApplicationMapper
│   └── usecases/    GetTeacherByIdUseCase, CreateTeacherUseCase, UpdateTeacherUseCase, SearchTeachersUseCase
└── infrastructure/
    ├── persistence/ TeacherEntity, TeacherJpaRepository, TeacherPersistenceMapper, TeacherRepositoryAdapter
    ├── event/       TeacherAccountActivatedListener (@TransactionalEventListener(BEFORE_COMMIT))
    └── web/         TeacherWebDto, TeacherWebMapper, TeacherController (4 endpoints), TeacherExceptionHandler
```

**Endpoints teachers:**
| Método | Path | Rol | Descripción |
|--------|------|-----|-------------|
| POST | `/api/admin/teachers` | ADMIN | Crear profesor + enviar email con link activación |
| GET | `/api/admin/teachers/{teacherId}` | ADMIN, STAFF | Obtener por ID |
| GET | `/api/admin/teachers` | ADMIN, STAFF | Buscar (dni / lastName / todos) |
| PATCH | `/api/admin/teachers/{teacherId}` | ADMIN, STAFF | Actualizar datos |

### `grades/` ✅ COMPLETO

```
grades/
├── domain/
│   ├── model/        Evaluation, PeriodGrade, FinalGrade
│   ├── valueobject/  EvaluationId, EvaluationTypeId, EvaluationStatus (movidos de academic/)
│   │                 FinalGradeId, PeriodGradeId, FinalGradeStatus
│   ├── repository/   EvaluationRepository, PeriodGradeRepository, FinalGradeRepository
│   └── exception/    GradeNotFoundException, GradeAlreadyValidatedException,
│                     InvalidGradeException, GradeAlreadyRecordedInRegistryException
├── application/
│   ├── dto/          CreateEvaluationRequest, GradeEvaluationRequest, RecordExamGradeRequest
│   │                 EvaluationResponse, PeriodGradeResponse, FinalGradeResponse
│   ├── mapper/       GradesApplicationMapper
│   └── usecases/     CreateEvaluationUseCase, GradeEvaluationUseCase, ValidateEvaluationUseCase,
│                     CalculatePeriodGradeUseCase, RecordExamGradeUseCase,
│                     CalculateFinalGradeUseCase, RecordFinalGradeInRegistryUseCase
└── infrastructure/
    ├── persistence/  EvaluationEntity, PeriodGradeEntity, FinalGradeEntity, EvaluationTypeEntity
    │                 + JpaRepositories + Adapters + Mappers
    ├── web/          GradesWebDto, GradesWebMapper, GradesController (7 endpoints), GradesExceptionHandler
    └── seeder/       GradesDataSeeder (@Profile("dev"), @Order(10))
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

### `course/` ✅ COMPLETO

```
course/
├── domain/
│   ├── model/        CourseSubject, StudentCourseSubject
│   ├── valueobject/  CourseSubjectId, StudentCourseSubjectId, CourseStatus, SubjectEnrollmentStatus
│   ├── repository/   CourseSubjectRepository, StudentCourseSubjectRepository
│   └── exception/    CourseSubjectNotFoundException, StudentAlreadyEnrolledException, ...
├── application/
│   ├── dto/          CreateCourseSubjectRequest, AssignTeacherRequest, EnrollStudentRequest
│   ├── mapper/       CourseApplicationMapper
│   └── usecases/     CreateCourseSubjectUseCase, AssignTeacherToCourseUseCase,
│                     EnrollStudentInCourseUseCase, GetCourseSubjectsByGradeLevelUseCase,
│                     GetStudentCoursesUseCase
└── infrastructure/
    ├── persistence/  CourseSubjectEntity, StudentCourseSubjectEntity + JpaRepositories + Adapters + Mappers
    ├── web/          CourseWebDto, CourseWebMapper, CourseController (5 endpoints), CourseExceptionHandler
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

### `attendance/` ✅ COMPLETO

```
attendance/
├── domain/
│   ├── model/        DailyAttendance, CourseAttendance, AttendanceSummary
│   ├── valueobject/  DailyAttendanceId, CourseAttendanceId, AttendanceSummaryId
│   │                 AttendanceStatus (PRESENT=0, ABSENT=1, JUSTIFIED=1, LATE=0.2, WITHDRAWN=0.2)
│   ├── repository/   DailyAttendanceRepository, CourseAttendanceRepository, AttendanceSummaryRepository
│   └── exception/    AttendanceAlreadyRecordedException, AttendanceNotFoundException
├── application/
│   ├── dto/          RecordDailyAttendanceRequest, RecordCourseAttendanceRequest,
│   │                 JustifyAbsenceRequest, CorrectAttendanceRequest + responses
│   ├── mapper/       AttendanceApplicationMapper
│   └── usecases/     RecordDailyAttendanceUseCase, RecordCourseAttendanceUseCase,
│                     JustifyAbsenceUseCase, CorrectAttendanceUseCase,
│                     GetAttendanceSummaryUseCase, GetAtRiskStudentsUseCase
└── infrastructure/
    ├── persistence/  DailyAttendanceEntity, CourseAttendanceEntity, AttendanceSummaryEntity
    │                 + JpaRepositories + Adapters + PersistenceMappers
    └── web/          AttendanceWebDto, AttendanceWebMapper,
                      AttendanceController (7 endpoints), AttendanceExceptionHandler
```

**Reglas de negocio `attendance/`:**
- `MIN_ATTENDANCE_PERCENTAGE = 85` — constante en `AttendanceSummary`
- `JUSTIFIED` descuenta igual que `ABSENT` (peso 1.0) — la justificación registra el motivo
- `atRisk = weightedAbsences/totalClasses > 0.15` — condición estricta

### `storage/` ✅ COMPLETO

```
storage/
├── domain/
│   ├── service/StorageService.java     ← puerto: upload(), delete(), generatePresignedUrl()
│   └── model/UploadedFile.java         ← VO: objectName, publicUrl, fileName, mimeType, sizeBytes
└── infrastructure/
    ├── config/OciStorageProperties.java ← app.storage.oci.*
    └── oci/OciObjectStorageService.java ← adaptador OCI SDK 3.43.0
```

**Decisiones clave `storage/`:**
- Puerto en `storage/domain/service/StorageService` — sin dependencias OCI
- Dependencias: `oci-java-sdk-objectstorage:3.43.0` + `oci-java-sdk-common:3.43.0`
- Cliente OCI inicializado en `@PostConstruct`, cerrado en `@PreDestroy`
- Estructura de carpetas en bucket: `records/{studentId}/{uuid}-{fileName}`, `materials/{teacherId}/{uuid}-{fileName}`
- URL pública: `https://objectstorage.{region}.oraclecloud.com/n/{namespace}/b/{bucket}/o/{objectName}`
- Presigned URLs via `CreatePreauthenticatedRequest` de OCI SDK
- Variables de entorno requeridas: `OCI_TENANCY_OCID`, `OCI_USER_OCID`, `OCI_FINGERPRINT`, `OCI_PRIVATE_KEY_PATH`, `OCI_REGION`, `OCI_NAMESPACE`, `OCI_BUCKET_NAME`
- Límite multipart: `spring.servlet.multipart.max-file-size=10MB`, `max-request-size=12MB`

**Configuración `application.yml` — agregar en `app:` de cada perfil:**
```yaml
  storage:
    oci:
      tenancy-ocid: ${OCI_TENANCY_OCID}
      user-ocid: ${OCI_USER_OCID}
      fingerprint: ${OCI_FINGERPRINT}
      private-key-path: ${OCI_PRIVATE_KEY_PATH}
      region: ${OCI_REGION}
      namespace: ${OCI_NAMESPACE}
      bucket-name: ${OCI_BUCKET_NAME}
      max-file-size-mb: 10
```

---

## 📐 Instrucciones para el Agente

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
- **Usar `default methods`** para VOs compuestos en persistence mapper.
- **Usar `ProblemDetail`** en todos los `@RestControllerAdvice`.
- **`RecordNumber.fromDni(dni)`** al crear un legajo.
- **Inyectar `EmailService`** en use cases que crean usuarios.
- **`getFullName()`** para obtener el nombre completo de `FullName` — nunca `fullName()`.
- **Usar `SecurityContextHelper.extractUserId(userDetails)`** en controllers.
- **Usar `CreateUserRequest.active()` o `inactive()`** al llamar `CreateUserUseCase`.
- **Usar `DomainEventPublisher`** para notificar eventos entre BCs.
- **Agregar `@TransactionalEventListener(phase = BEFORE_COMMIT)`** en listeners atómicos.
- **Usar `StorageService`** para subida de archivos — nunca acceder al SDK de OCI directamente desde use cases.
- **Tipos MIME permitidos para upload:** `application/pdf`, `image/jpeg`, `image/png` — máx 10 MB.
- **En tests usar CUILs válidos:** calcular con pesos `[5,4,3,2,7,6,5,4,3,2]` — ejemplos: DNI `12345678` → CUIL `20123456786`, DNI `87654321` → CUIL `20876543215`.
- **En tests, modelos con campos `final` y `@Builder` de Lombok** (como `Parent`) → construir instancia real con builder, no mockear.

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
- **Nunca hacer delete físico** de estudiantes/padres/profesores.
- **Nunca usar `RegistryNumberGenerator`** para generar números de legajo.
- **Nunca crear `GenderEntity`** u otros enums duplicados del Shared Kernel.
- **Nunca poner `INSTANCE = Mappers.getMapper(...)`** en mappers con `componentModel = "spring"`.
- **Nunca tipar el `@Id` como `byte[]`** — usar UUID con `@Convert(UuidBinaryConverter.class)`.
- **Nunca usar `infra`** como nombre de paquete en BCs nuevos — usar `infrastructure` completo.
- **Nunca exponer delete** en puertos de repositorios de students/teachers/parents.
- **Nunca llamar `fullName()`** — el método correcto es `getFullName()`.
- **Nunca dejar que un fallo de email** rompa una transacción.
- **Nunca duplicar `extractUserId()`** en controllers — usar `SecurityContextHelper.extractUserId()`.
- **Nunca inyectar repositorios de otros BCs en use cases de `auth/`** — usar eventos de dominio.
- **Nunca usar `@Async` en listeners de activación** — deben ser `@TransactionalEventListener(BEFORE_COMMIT)`.
- **Nunca acceder al SDK de OCI directamente** desde use cases o dominio — usar el puerto `StorageService`.
- **Nunca asumir que un DNI tiene dígito verificador** — el DNI argentino es correlativo.
- **Nunca mockear con `mock()` clases con campos `final` y `@Builder`** — construir instancia real.

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
15. `infrastructure/web/controller/` — REST con `@PreAuthorize`, `SecurityContextHelper.extractUserId()`
16. `infrastructure/web/mapper/` — `XWebMapper` application ↔ web DTO
17. `infrastructure/web/exception/` — `@RestControllerAdvice` con `ProblemDetail`
18. `infrastructure/seeder/` — datos iniciales para perfil `dev`
19. `db/migration/V{n}__create_{context}_tables.sql`
20. Si el BC necesita reaccionar a activaciones de cuenta → agregar `XAccountActivatedListener` en `infrastructure/event/`

### 🧪 Al generar tests

```java
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class CreateStudentUseCaseTest {
    // Modelos con campos final + @Builder → instancia real, no mock
    private Parent buildRealParent() {
        return Parent.create(Parent.builder()
                .parentId(ParentId.generate())
                .userId(UserId.generate())
                .dni(Dni.of("87654321"))
                .cuil(Cuil.of("20876543215")) // CUIL válido para DNI 87654321
                ...
                .build());
    }
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
| V21 | `attendance_daily_records`, `attendance_course_records`, `attendance_period_summaries` |
| V22 | `teaching_materials` ⏳ PENDIENTE |

**Próxima migración disponible: V22**

---

## 🛠️ Comandos

```bash
mvn clean verify
mvn test -Dgroups="unit"
mvn test jacoco:report
mvn spring-boot:run -Dspring-boot.run.profiles=dev
mvn spring-boot:run -Dspring-boot.run.profiles=local
mvn clean package -DskipTests

# Mailhog para desarrollo local de emails
docker run -p 1025:1025 -p 8025:8025 mailhog/mailhog
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

### ✅ Completado en esta sesión

- **Tests unitarios** — 98 tests totales:
    - `ActivateAccountUseCaseTest` (5 casos) — token inválido, user no encontrado, happy path, datos del evento, invariante arquitectónica
    - `TeacherAccountActivatedListenerTest` (3 casos) — ROLE_TEACHER, otro rol ignorado, teacher no encontrado
    - `CreateStudentUseCaseTest` (10 casos) — DNI/CUIL duplicado, sin año académico, GradeLevel inactivo/no encontrado, sin registry, happy path padre nuevo/existente, params del User, email silencioso
- **`UnauthorizedException`** — movida a `auth/domain/exception/` (fix de compilación)
- **`Dni.java`** — eliminada validación de dígito verificador (el DNI argentino es correlativo)
- **Rate Limiting** — Bucket4j in-memory, por IP, 3 endpoints protegidos, configurable por perfil
- **`storage/` BC** — puerto `StorageService`, VO `UploadedFile`, adaptador OCI (`OciObjectStorageService`), propiedades `OciStorageProperties`
- **`UploadRecordDocumentUseCase`** — subida de documentos al legajo via OCI, validación MIME/tamaño, atómico con BD
- **`RecordDocumentRepository`** — puerto + adaptador implementados
- **`RecordController`** actualizado — nuevo endpoint `POST /{recordId}/upload` (multipart)

### ⏳ Pendiente

- [ ] Crear bucket OCI (`ipet132-documents`) y configurar variables de entorno OCI en `.env`
- [ ] Probar endpoint de upload con bucket real
- [ ] **`teaching-materials/` BC** — material didáctico de profesores ← **PRÓXIMO**
- [ ] Auditoría (registrar quién hizo qué y cuándo)
- [ ] Métricas / monitoreo

### 🎯 Próximo BC: `teaching-materials/`

**Decisiones de diseño acordadas:**
- Asociación principal a `CourseSubject`, con `subjectId` y `academicYearId` desnormalizados para búsquedas
- Visibilidad: flag simple `isVisibleToStudents: boolean` (no estados)
- Tipos: enum fijo `APUNTE, EJERCICIO, EXAMEN, GUIA, VIDEO, OTRO`
- Almacenamiento en OCI: carpeta `materials/{teacherId}/{courseSubjectId}/{uuid}-{fileName}`
- Tipos permitidos: PDF, JPG, PNG — máx 10 MB
- Roles: TEACHER sube/gestiona su propio material; ADMIN/STAFF ve todo; STUDENT solo ve `isVisibleToStudents=true` de sus cursos

**Estructura planificada:**
```
teaching-materials/
├── domain/
│   ├── model/TeachingMaterial.java
│   ├── valueobject/TeachingMaterialId.java, MaterialType.java (enum)
│   ├── repository/TeachingMaterialRepository.java
│   └── exception/TeachingMaterialNotFoundException.java
├── application/
│   ├── dto/request/UploadMaterialRequest.java, UpdateMaterialRequest.java
│   ├── dto/response/TeachingMaterialResponse.java
│   ├── mapper/TeachingMaterialApplicationMapper.java
│   └── usecases/
│       ├── UploadTeachingMaterialUseCase.java   ← TEACHER
│       ├── GetMaterialsByCourseUseCase.java     ← TEACHER, ADMIN, STAFF
│       ├── GetMaterialsForStudentUseCase.java   ← STUDENT (filtra isVisibleToStudents)
│       ├── UpdateMaterialUseCase.java           ← TEACHER (solo su propio material)
│       └── DeleteMaterialUseCase.java           ← TEACHER, ADMIN
└── infrastructure/
    ├── persistence/  TeachingMaterialEntity + JpaRepository + Adapter + PersistenceMapper
    ├── web/          TeachingMaterialWebDto, WebMapper, TeachingMaterialController (5 endpoints)
    └── (sin seeder — el material lo crean los profesores)
```

**Migración V22:**
```sql
CREATE TABLE teaching_materials (
    material_id         BINARY(16) PRIMARY KEY,
    teacher_id          BINARY(16) NOT NULL,
    course_subject_id   BINARY(16) NOT NULL,
    subject_id          BINARY(16) NOT NULL,        -- desnormalizado para búsquedas
    academic_year_id    BINARY(16) NOT NULL,         -- desnormalizado para búsquedas
    title               VARCHAR(200) NOT NULL,
    description         TEXT,
    material_type       VARCHAR(20) NOT NULL,        -- APUNTE, EJERCICIO, EXAMEN, GUIA, VIDEO, OTRO
    file_path           VARCHAR(500) NOT NULL,       -- objectName en OCI
    file_name           VARCHAR(255) NOT NULL,       -- URL pública en OCI
    file_size_bytes     BIGINT NOT NULL,
    mime_type           VARCHAR(100) NOT NULL,
    is_visible_to_students BOOLEAN NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (course_subject_id) REFERENCES course_subjects(course_subject_id) ON DELETE RESTRICT,
    FOREIGN KEY (academic_year_id) REFERENCES academic_years(academic_year_id) ON DELETE RESTRICT,
    INDEX idx_course_subject (course_subject_id),
    INDEX idx_teacher (teacher_id),
    INDEX idx_subject_year (subject_id, academic_year_id),
    INDEX idx_visible (is_visible_to_students)
);
```

**Endpoints planificados:**
| Método | Path | Rol | Descripción |
|--------|------|-----|-------------|
| POST | `/api/materials` | TEACHER | Subir material (multipart) |
| GET | `/api/materials/course/{courseSubjectId}` | TEACHER, ADMIN, STAFF | Listar por curso |
| GET | `/api/materials/my-courses` | STUDENT | Ver material visible de sus cursos |
| PATCH | `/api/materials/{materialId}` | TEACHER | Actualizar metadata/visibilidad |
| DELETE | `/api/materials/{materialId}` | TEACHER, ADMIN | Eliminar (OCI + BD) |

**IDs que se cruzan solo como UUID (no clases completas):**
```java
CourseSubjectId  → course/
SubjectId        → academic/
AcademicYearId   → academic/
TeacherId        → teachers/
```