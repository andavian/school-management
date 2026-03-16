# CLAUDE.md — Sistema de Gestión Escolar IPET 132

> Guía de contexto, arquitectura y comportamiento para agentes de IA trabajando en este proyecto.
> **Leer completo antes de modificar cualquier archivo.**

---

## 🎯 Propósito del Proyecto

Sistema de gestión escolar para el **IPET 132** (Argentina).
**Stack:** Java 17 + Spring Boot 3.3.4 + Spring Security 6 + MySQL 8
**Package raíz:** `org.school.management`
**Estado actual:** Auth ✅ + Geography ✅ + Academic ✅ + Students ✅ + Teachers ✅ + Email Service ✅

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
grades/       → Calificaciones ⏳ próximo
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
- `sendTeacherInvitation(to, firstName, lastName, dni, temporaryPassword, activationLink)` — invitación docente
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
- `CreateTeacherUseCase` en `auth/` crea el User con rol TEACHER y genera password temporal
- `CreateTeacherResponse` tiene: `userId`, `dni`, `temporaryPassword`, `invitationSent`

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
│                    CreateTeacherUseCase (delega User en auth/CreateTeacherUseCase)
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
| POST | `/api/admin/teachers` | ADMIN | Crear profesor |
| GET | `/api/admin/teachers/{teacherId}` | ADMIN, STAFF | Obtener por ID |
| GET | `/api/admin/teachers` | ADMIN, STAFF | Buscar (dni / lastName / todos) |
| PATCH | `/api/admin/teachers/{teacherId}` | ADMIN, STAFF | Actualizar datos |

**Notas `teachers/`:**
- Password inicial: aleatorio seguro generado en `auth/CreateTeacherUseCase`
- Cuenta inicia con `active = false` — requiere activación (pendiente link de activación)
- `CreateTeacherUseCase` en `teachers/` orquesta: valida unicidad → crea User (via auth/) → crea Teacher → envía email
- Email de invitación: texto plano con usuario y password temporal (sin link por ahora)

### `shared/email/` ✅ COMPLETO

**Puerto:** `shared/domain/service/EmailService.java`
**Adaptador:** `shared/infrastructure/email/JavaMailEmailService.java`
**Config:** `shared/infrastructure/config/AsyncConfig.java`

- SMTP configurado para **OCI Email Delivery** (prod) y **Mailhog** (local)
- `@Async` — envío nunca bloquea el hilo transaccional
- Fallos silenciosos — log + catch, nunca propagan excepción
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
| **PersistenceMapper con default methods** | Para Teachers y Parents — VOs compuestos complejos |
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
| **Password padre aleatorio seguro** | Pendiente email service — generado con SecureRandom |
| **Password inicial estudiante** | `{DNI}Ipet132!` — simple para el admin |
| **Folio automático** | `FolioAssignmentService` transaccional garantiza unicidad |
| **Baja de estudiante es lógica** | No hay delete físico — via `StudentEnrollment.withdraw()` |
| **Sin delete físico en puertos** | Ningún repositorio de students/teachers expone delete |
| **Address.placeId en teachers/parents** | Columna `place_id` — distinto a `residence_place_id` en students |
| **EmailService en shared/domain/service** | Puerto transversal — usado por teachers, parents y futuro students |
| **@Async en JavaMailEmailService** | Email no bloquea la transacción principal |
| **Email falla silenciosamente** | Log + catch — nunca propaga ni revierte la transacción |
| **Teacher.active = false al crear** | Requiere activación de cuenta — pendiente link |
| **grades/ como BC separado** | Razón de cambio diferente a academic/ — actores distintos (TEACHER vs ADMIN) |
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
- **Usar `default methods`** para VOs compuestos en persistence mapper (patrón teachers/parents).
- **Usar `ProblemDetail`** en todos los `@RestControllerAdvice`.
- **`RecordNumber.fromDni(dni)`** al crear un legajo — nunca usar `RegistryNumberGenerator` para esto.
- **Inyectar `EmailService`** en use cases que crean usuarios — teachers y parents.
- **Tests en `src/test/java/`** — mismo paquete que producción pero bajo `test/`, no `main/`.
- **`getFullName()`** para obtener el nombre completo de `FullName` — nunca `fullName()`.

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

### 🔗 Dependencias permitidas en `grades/` (próximo BC)

```java
// ✅ Permitido — solo IDs y Shared Kernel
import org.school.management.academic.domain.valueobject.ids.AcademicYearId;
import org.school.management.academic.domain.valueobject.ids.GradeLevelId;
import org.school.management.academic.domain.valueobject.ids.SubjectId;
import org.school.management.academic.domain.valueobject.ids.PeriodId;
import org.school.management.students.personal.domain.valueobject.StudentPersonalDataId;
import org.school.management.teachers.domain.valueobject.TeacherId;
import org.school.management.auth.domain.valueobject.UserId;

// ❌ Prohibido — clases completas de otro bounded context
import org.school.management.academic.domain.model.Subject;   // NUNCA
import org.school.management.teachers.domain.model.Teacher;   // NUNCA
```

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
| V15+ | Reservado para `grades/` |

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
| TEACHER | `12345678` | `Teacher123!` |
| STUDENT (con email) | `11223344` | `11223344Ipet132!` |
| STUDENT (sin email) | `87654321` | `87654321Ipet132!` |

---

## ⏳ Estado del Proyecto

### ✅ Completado

- `auth/` — JWT, refresh, blacklist, sesiones, creación de usuarios
- `geography/` — Geografía argentina con búsqueda y jerarquía
- `academic/` — Años, orientaciones, cursos, materias, registro de calificaciones, 22 use cases
- `shared/email/` — EmailService (puerto + JavaMailEmailService con OCI SMTP + AsyncConfig)
- **`students/` — COMPLETO** — 5 agregados de punta a punta
- **`teachers/` — COMPLETO** — domain + application + infrastructure + 4 endpoints
- **`parents/` — CORREGIDO** — cuil agregado en todas las capas, residencePlaceId → placeId
- **Tests unitarios** — 22 tests: GetTeacherById, CreateTeacher, UpdateTeacher, CreateParent, LinkParentToStudent
- Flyway V1–V7, V10–V14

### ⏳ Pendiente

- [ ] `grades/` — Calificaciones ← **próximo**
- [ ] Activación de cuenta teacher — link en email (requiere `confirmationToken` en `CreateTeacherResponse`)
- [ ] Seeder de teachers para perfil `dev`
- [ ] Seeder de students y parents para perfil `dev`
- [ ] Tests unitarios para students (CreateStudentUseCase — 15 pasos)
- [ ] Rate limiting, auditoría, métricas

### 🎯 Próximo paso — `grades/` (bounded context separado)

**Modelo de negocio IPET 132:**
- **2 períodos cuatrimestrales** por año
- **Calificación continua** — notas numéricas (1-10) o conceptuales (Logrado/En proceso/Pendiente) que carga el TEACHER durante el período
- **Nota de período** — 2 notas cuatrimestrales por materia que asienta el PRECEPTOR/STAFF con número de libro y folio del calificador
- **Nota mínima de aprobación:** 7
- **Instancias de recuperación:**
    - Coloquio (diciembre y febrero) — va separada, con libro y folio de actas
    - Examen de materia previa — también separado con libro y folio
- **Promedio** — el sistema calcula desde las notas continuas cuando el profesor lo solicita

**Estados de materia (`SubjectStatus`):**

| Estado | Descripción | Va al libro matriz |
|--------|-------------|-------------------|
| `APPROVED` | Nota período ≥ 7 o aprueba coloquio/examen | ✅ Sí |
| `COLOQUIO` | Nota período < 7, va a instancia diciembre/febrero | ✅ Sí |
| `PREVIA` | No aprueba coloquio — queda con examen pendiente | ✅ Sí |
| `PENDING` | En curso — período no cerrado | ❌ No |
| `OWES` | **Solo al cerrar folio** — pase o abandono | ⚠️ Solo al cerrar folio |

> **CRÍTICO:** `OWES` (adeuda) NO se asienta en el libro matriz durante el ciclo normal. Solo aparece cuando se cierra el folio del alumno por pase a otra institución o abandono. El cierre de folio es un evento de dominio ligado a `StudentEnrollment.withdraw()`.

**Actores:**

| Actor | Acción |
|-------|--------|
| TEACHER | Carga notas continuas, solicita promedio |
| STAFF/PRECEPTOR | Asienta notas de período y exámenes con libro/folio |
| ADMIN | Controla y cierra para libro matriz |

**Agregados planificados:**
1. `Grade` — calificación continua (teacher)
2. `PeriodGrade` — nota cuatrimestral con libro/folio (preceptor)
3. `ExamGrade` — coloquio o examen previo con libro/folio (preceptor)
4. `SubjectStatus` — estado final por materia/estudiante/año (derivado + persistido)