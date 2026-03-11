# CLAUDE.md — Sistema de Gestión Escolar IPET 132

> Guía de contexto, arquitectura y comportamiento para agentes de IA trabajando en este proyecto.
> **Leer completo antes de modificar cualquier archivo.**

---

## 🎯 Propósito del Proyecto

Sistema de gestión escolar para el **IPET 132** (Argentina).
**Stack:** Java 17 + Spring Boot 3.2.x + Spring Security 6 + MySQL 8
**Package raíz:** `org.school.management`
**Estado actual:** Auth ✅ + Geography ✅ + Academic ✅ + Students personal application layer ✅

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
students/     → Estudiantes, salud, matrícula, legajo ⏳ (personal domain+application ✅)
teachers/     → Profesores (futuro)
```

**Regla:** Un bounded context **no importa clases completas de otro**.
Solo se comparten IDs (ej: `GradeLevelId`, `PlaceId`) o tipos del Shared Kernel.

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
│   ├── Gender.java        # Enum: MALE, FEMALE, OTHER
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
└── domain/exception/
    └── DomainException.java    # Clase base abstracta — nunca lanzar directamente
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
@Repository
@RequiredArgsConstructor
public class StudentPersonalDataRepositoryAdapter implements StudentPersonalDataRepository {
    private final StudentPersonalDataJpaRepository jpaRepository;
    private final StudentPersonalDataPersistenceMapper mapper;
}
```

---

## 📁 Estructura por Bounded Context

### `auth/` ✅ completado

```
auth/
├── domain/
│   ├── model/           User, RefreshToken, BlacklistedToken, Role
│   ├── valueobject/     UserId, HashedPassword, PlainPassword, RoleName, RoleId,
│   │                    BlacklistedTokenId  ← todos refactorizados a records
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

**Notas `auth/` VOs:**
- `RoleName` implementa `GrantedAuthority` — es un record que implementa interfaz de Spring Security
- `RoleName.student()`, `.admin()`, `.teacher()`, `.parent()`, `.staff()` — factory methods por rol
- `PlainPassword` — valida fortaleza en constructor compacto (upper, lower, digit, special)
- `HashedPassword` — `toString()` retorna `"HashedPassword{***}"` por seguridad

### `geography/` ✅ completado

```
geography/
├── domain/
│   ├── model/           Country, Province, Place, PlaceWithHierarchy
│   ├── valueobject/     IsoCode, PhoneCode, ProvinceCode, PostalCode,
│   │                    GeographicName, PlaceType (enum) ← todos refactorizados a records
│   └── repository/      CountryRepository, ProvinceRepository,
│                        PlaceRepository, GeographyQueryRepository
├── application/
│   ├── usecases/        GetPlaceById (retorna PlaceResponse), SearchPlaces, ...
│   ├── dto/response/    PlaceResponse (con jerarquía: provinceName, countryName),
│   │                    PlaceSummaryResponse, CountryResponse, ProvinceResponse
│   └── mappers/         GeographyApplicationMapper (MapStruct)
└── infrastructure/
    ├── web/controller/  GeographyController (público), GeographyAdminController (ADMIN)
    ├── persistence/     entity/, repository/, adapter/, mapper/
    └── seeder/          GeographyDataSeeder — Argentina: 1 país, 24 provincias, ~45 lugares
```

**Notas `geography/`:**
- `PostalCode.ofNullable(String)` → `Optional<PostalCode>` para campos opcionales en persistence mapper
- `ProvinceCode.ofNullable(String)` → `Optional<ProvinceCode>` ídem
- `GetPlaceByIdUseCase.execute(GetPlaceByIdRequest)` → retorna `PlaceResponse` (no PlaceWithHierarchyResponse)
- `PlaceResponse` tiene: `placeId`, `name`, `provinceName`, `countryName`, `provinceCode`, `countryIsoCode`, `fullAddress`, `fullDescription`

### `academic/` ✅ completado

```
academic/
├── domain/
│   ├── model/           AcademicYear, Orientation, GradeLevel, Subject,
│   │                    StudyPlan, EvaluationPeriod, QualificationRegistry
│   ├── valueobject/     Year, YearLevel, Division, RegistryNumber, SubjectCode,
│   │                    WeeklyHours, OrientationCode, PeriodNumber ← todos records
│   ├── valueobject/ids/ AcademicYearId, GradeLevelId, OrientationId, SubjectId,
│   │                    RegistryId, StudyPlanId, PeriodId, CourseId,
│   │                    EvaluationId, EvaluationTypeId, WithdrawalReasonId ← todos records
│   ├── repository/      AcademicYearRepository (findCurrentYear()),
│   │                    GradeLevelRepository (findById(GradeLevelId)),
│   │                    QualificationRegistryRepository (findActiveRegistryForYear(AcademicYearId))
│   ├── service/         FolioAssignmentService ← CRÍTICO: assignNextFolio() @Transactional
│   │                    RegistryNumberGenerator ← generate(AcademicYearId, int year) → String
│   │                    AcademicYearActivationService, GradeLevelValidationService,
│   │                    StudyPlanManagementService
│   └── exception/       20+ excepciones — GradeLevelNotFoundException(GradeLevelId | String)
├── application/
│   ├── usecases/        22 use cases (AcademicYear×6, Orientation×6, GradeLevel×5, Subject×5)
│   ├── dto/             request/ + response/ con validaciones Jakarta
│   └── mapper/          AcademicApplicationMapper (MapStruct)
└── infrastructure/
    ├── web/controller/  AcademicYearController, OrientationController,
    │                    GradeLevelController, SubjectController
    ├── persistence/     entity/, repository/, adapter/, mapper/
    └── seeder/          AcademicDataSeeder — 2 años, 2 orientaciones, 37 cursos, ~60 materias
```

**Notas críticas `academic/`:**
- `AcademicYear` NO tiene `getActiveRegistry()` — consultar `QualificationRegistryRepository.findActiveRegistryForYear()`
- `RegistryNumberGenerator.generate(AcademicYearId, int year)` → retorna `String`, no `RecordNumber`
  → En el use case: `RecordNumber.of(registryNumberGenerator.generate(academicYearId, year))`
- `GradeLevelId.from(UUID)` — no tiene `of()` en versión original; refactorizado ahora tiene ambos
- `QualificationRegistry` es agregado separado de `AcademicYear` — tiene su propio repositorio

### `students/` — En construcción

Estructura aprobada — **4 agregados separados** (NO una God Table):

```
students/
├── personal/     → StudentPersonalData ✅ domain + application completos
├── health/       → StudentHealthRecord ✅ domain completo | application ⏳
├── enrollment/   → StudentEnrollment   ✅ domain completo | application ⏳
└── records/      → StudentRecord       ✅ domain completo | application ⏳
```

#### `students/personal/` — ✅ Domain + Application COMPLETOS

```
students/personal/
├── domain/
│   ├── model/       StudentPersonalData.java   # @Builder+@Getter, factory method create()
│   ├── valueobject/ StudentPersonalDataId.java # record UUID — from(UUID|String) + generate()
│   ├── repository/  StudentPersonalDataRepository.java  # Puerto — recibe VOs, no UUIDs primitivos
│   └── exception/   StudentNotFoundException (byId, byDni)
│                    StudentAlreadyExistsException (withDni, withCuil)
│                    InvalidStudentDataException
└── application/
    ├── dto/
    │   ├── request/
    │   │   ├── CreateStudentRequest.java   # con HealthDataRequest + ParentRequest nested
    │   │   └── UpdateStudentRequest.java   # solo contacto/domicilio — studentId va en @PathVariable
    │   └── response/
    │       ├── StudentResponse.java        # con AddressResponse + PlaceResponse nested
    │       └── StudentSummaryResponse.java # para listas y búsquedas
    ├── mapper/
    │   └── StudentPersonalDataApplicationMapper.java  # domain → response, recibe PlaceResponse como param
    └── usecases/
        ├── GetStudentByIdUseCase.java      # buildResponse() package-private — reutilizado por otros
        ├── GetStudentByDniUseCase.java     # delega buildResponse() a GetStudentByIdUseCase
        ├── SearchStudentsUseCase.java      # dni > residencePlaceId > fullName > all (prioridad)
        ├── UpdateStudentUseCase.java       # llama updatePersonalData(fullName, phone, email, address)
        └── CreateStudentUseCase.java       # orquestador de 15 pasos @Transactional
```

**Decisiones de diseño `students/personal/application/`:**

- DTOs en `dto/request/` y `dto/response/` — nunca en `dto/` directamente
- `UpdateStudentRequest` no incluye `studentId` — va como `@PathVariable` en el controller
- Validación de edad eliminada del DTO — pertenece al dominio (`StudentPersonalData.create()`)
- DNI siempre 8 dígitos (`\\d{8}`) — consistente con `Dni.java` del Shared Kernel
- `CreateStudentRequest` agrupa datos de todos los agregados en un solo request para el flujo atómico
- `HealthDataRequest.emergencyContactFirstName` + `emergencyContactLastName` — separados para `FullName.of()`
- `SearchStudentsUseCase` recibe parámetros primitivos (no el DTO) — testeable sin infraestructura web
- `StudentPersonalDataApplicationMapper.toStudentResponse()` requiere `PlaceResponse` como parámetros — el mapper no puede llamar a Geography directamente
- `GetPlaceByIdUseCase.execute(new GetPlaceByIdRequest(placeId))` — forma correcta de cruzar a Geography

#### Flujo transaccional `CreateStudentUseCase` (15 pasos, TODO O NADA)

```
1.  Validar DNI no existente      → StudentPersonalDataRepository.existsByDni(Dni)
2.  Validar CUIL no existente     → StudentPersonalDataRepository.existsByCuil(String)
3.  Obtener AcademicYear activo   → AcademicYearRepository.findCurrentYear()
4.  Validar GradeLevel activo     → GradeLevelRepository.findById(GradeLevelId.from(UUID))
5.  Asignar folio                 → FolioAssignmentService.assignNextFolio()
6.  Generar password              → "{DNI}Ipet132!" → PlainPassword.of()
7.  Crear User en auth            → User.create(dni, plainPassword, Set.of(Role.create(RoleName.student())), encoder)
                                  → UserRepository.save()
8.  Crear StudentPersonalData     → StudentPersonalData.create(builder) → studentRepository.save()
9.  Crear StudentHealthRecord     → StudentHealthRecord.create(builder) → healthRecordRepository.save()
10. Obtener registry activo       → QualificationRegistryRepository.findActiveRegistryForYear(academicYearId)
11. Generar número de legajo      → registryNumberGenerator.generate(academicYearId, year) → RecordNumber.of(String)
    Crear StudentRecord           → StudentRecord.create(builder) → studentRecordRepository.save()
12. TODO: Buscar o crear Parent   → pendiente — agregado parents/ no implementado
13. TODO: Crear StudentParent     → pendiente — agregado parents/ no implementado
14. Crear StudentEnrollment       → StudentEnrollment.create(builder) → enrollmentRepository.save()
15. Commit → retornar StudentResponse via getStudentByIdUseCase.buildResponse(student)
```

#### `students/health/` — Domain ✅ completo

- `StudentHealthRecord` — `@Builder + @Getter`, factory method `create(builder)`
- `BloodType` enum — tiene `fromString(String)` que busca por `displayName` (A+, B-, etc.)
  → En el DTO: `@Pattern(regexp = "^(A|B|AB|O)[+-]$")` valida el formato
- `HealthRecordId` — record UUID con `of()`, `from()`, `generate()`
- `StudentHealthRecordRepository` — `findByStudentId(StudentPersonalDataId)`, `existsByStudentId()`

#### `students/enrollment/` — Domain ✅ completo

- `StudentEnrollment` — `@Builder + @Getter`, factory method `create(builder)`
- `EnrollmentType.TRANSFER` requiere `previousSchool` — validado en `create()`
- `EnrollmentId` — record UUID
- `StudentEnrollmentRepository` — `existsActiveEnrollment(studentId, academicYearId)`

#### `students/records/` — Domain ✅ completo

- `StudentRecord` — `@Builder + @Getter`, factory method `create(builder)`, gestiona `List<RecordDocument>`
- `RecordNumber` — record String, formato `LEG-{año}-{secuencia}` ← **OJO**: el generador produce `REG-{año}-{secuencia}`
  → Verificar consistencia entre `RegistryNumber` (REG) y `RecordNumber` (LEG) al implementar infrastructure
- `RecordId` — record UUID
- `StudentRecordRepository` — `findByStudentIdAndAcademicYearId()`, `existsByStudentIdAndAcademicYearId()`

---

## ⚙️ Stack y Versiones

| Tecnología | Versión | Uso |
|------------|---------|-----|
| Java | 17 | Records, Value Objects inmutables |
| Spring Boot | 3.2.x | Framework principal |
| Spring Security | 6.x | Auth/Authz + JWT |
| Spring Data JPA | (Boot managed) | Persistencia |
| MySQL | 8 | Producción |
| H2 | (test scope) | Tests |
| jjwt | 0.12.3 | JWT access + refresh tokens |
| MapStruct | 1.5.5.Final | Mapeo type-safe entre capas |
| Lombok | (Boot managed) | Solo en modelos de dominio complejos (@Builder, @Getter) |
| Flyway | (Boot managed) | Migraciones de esquema |
| SpringDoc OpenAPI | latest | Swagger UI |
| JUnit 5 + Mockito | (Boot managed) | Testing |

---

## 🔐 Decisiones de Dominio — NO Cambiar Sin Discutir

| Decisión | Razón |
|----------|-------|
| **DNI como username** | Identificador universal en Argentina |
| **DNI siempre 8 dígitos** | Consistente con `Dni.java` del Shared Kernel — no 7 u 8 |
| **Email opcional** | Estudiantes menores no tienen email |
| **UUID como PK** | Preparado para microservicios |
| **BINARY(16) para UUIDs en BD** | Consistente en todo el proyecto — persistence mappers incluyen `bytesToUuid` / `uuidToBytes` |
| **Records para todos los VOs** | Java 17 nativo — sin Lombok `@Value`. Inmutabilidad, equals/hashCode, toString gratis |
| **of() como factory method principal** | Estándar del proyecto — todos los VOs tienen `of()`. `from()` como alias para compatibilidad |
| **Lombok solo en modelos de dominio complejos** | `@Builder + @Getter` para clases con +10 campos (StudentPersonalData, etc.) |
| **MapStruct en 3 capas** | Type-safe en compile-time, sin reflection |
| **Flyway obligatorio** | Nunca `ddl-auto: create` |
| **Shared Kernel** | `Dni`, `Email`, `PhoneNumber`, `Cuil`, `Address`, IDs geográficos — nunca duplicar |
| **Students en 4 agregados** | Evitar God Table — separación real de responsabilidades |
| **CreateStudentRequest unificado** | Un solo request para el flujo atómico de 15 pasos — HealthDataRequest y ParentRequest como nested records |
| **studentId como @PathVariable** | No va en el body del UpdateStudentRequest |
| **Validaciones de negocio en dominio** | No en DTOs — edad, CUIL↔DNI, etc. van en `create()` del agregado |
| **Folio automático** | `FolioAssignmentService` transaccional garantiza unicidad |
| **Password inicial estudiante** | `{DNI}Ipet132!` — simple para el admin |
| **Password padre** | Aleatorio seguro — enviado por email (pendiente email service) |
| **Geography endpoints públicos** | Formularios de alta necesitan autocompletado sin auth |
| **Use Cases cruzan BCs via use case público** | `GetPlaceByIdUseCase` desde Students — nunca repositorio directo de otro BC |
| **PlaceResponse como parámetro del mapper** | Mapper de application no puede llamar a Geography — el use case resuelve los lugares y los pasa |
| **BloodType.fromString() por displayName** | API recibe "A+", "B-" — no exponer nombres internos del enum |
| **Baja de estudiante es lógica** | No hay delete físico — se gestiona desde `StudentEnrollment` |
| **Address encapsula PlaceId** | `Address` en Shared Kernel incluye `PlaceId` — domicilio completo como VO |
| **Excepciones: constructor + factory methods** | `byId()`, `byDni()`, `withDni()`, `withCuil()` — flexibilidad y consistencia |
| **Validación CUIL↔DNI en dominio** | Regla de negocio argentina — el DNI embebido en el CUIL debe coincidir |
| **Naming adapter vs impl** | Auth usa `*RepositoryImpl` (histórico); módulos nuevos usan `*RepositoryAdapter` |

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

### ❌ Nunca hacer

- **Nunca importar** `jakarta.persistence.*` o `org.springframework.*` en `domain/`.
- **Nunca exponer** entidades JPA en la API — siempre DTO via mapper.
- **Nunca inyectar** `JpaRepository` directamente en un Use Case.
- **Nunca poner lógica de negocio** en controllers ni en entidades JPA.
- **Nunca modificar** migraciones Flyway ya ejecutadas.
- **Nunca usar** `ddl-auto: create` o `update`.
- **Nunca hardcodear** secretos, contraseñas o URLs.
- **Nunca cruzar** bounded contexts con clases completas — solo IDs o Shared Kernel.
- **Nunca duplicar** `Dni`, `Email`, `PhoneNumber`, `Cuil`, `Address` — están en `shared/`.
- **Nunca usar Lombok `@Value`** en Value Objects — usar `record` de Java 17.
- **Nunca poner validaciones de negocio** en DTOs — van en el dominio.
- **Nunca incluir `studentId`** en el body del UpdateStudentRequest — va como `@PathVariable`.
- **Nunca hacer delete físico** de estudiantes — la baja es lógica via `StudentEnrollment`.
- **Nunca llamar `registryNumberGenerator.generate(int year)` con un solo argumento** — firma correcta: `generate(AcademicYearId, int year)`.

### 🔍 Al analizar código existente

1. Identificar a qué bounded context y capa pertenece el archivo.
2. Verificar si la modificación cruza alguna frontera arquitectónica.
3. Si se detecta una violación existente, mencionarla pero **no corregirla** salvo pedido explícito.
4. Respetar el naming del módulo: `*RepositoryImpl` (auth), `*RepositoryAdapter` (módulos nuevos).
5. Verificar que los VOs usen `of()` y no `from()` como factory method principal.

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
10. `infrastructure/persistence/entity/` — entidades JPA sin lógica
11. `infrastructure/persistence/repository/` — `XJpaRepository`
12. `infrastructure/persistence/adapter/` — `XRepositoryAdapter implements XRepository`
13. `infrastructure/persistence/mapper/` — MapStruct domain ↔ entity (incluir `bytesToUuid`/`uuidToBytes`)
14. `infrastructure/web/dto/` — DTOs de API con Jakarta
15. `infrastructure/web/controller/` — REST con `@PreAuthorize`
16. `infrastructure/web/mapper/` — MapStruct application ↔ web DTO
17. `infrastructure/web/exception/` — `@RestControllerAdvice`
18. `infrastructure/seeder/` — datos iniciales para perfil `dev`
19. `db/migration/V{n}__create_{context}_tables.sql`

### 🔗 Al implementar Students — dependencias permitidas

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

// ❌ Prohibido — clases completas de otro bounded context
import org.school.management.academic.domain.model.GradeLevel; // NUNCA
import org.school.management.auth.domain.model.User;           // NUNCA
import org.school.management.geography.domain.repository.PlaceRepository; // NUNCA desde Students
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
| V11 | `document_types`, `student_records`, `record_documents` |
| V12 | `parents`, `student_parents` |
| V14 | `withdrawal_reasons`, `student_enrollments` |

**Convenciones de BD:**
- PK: `BINARY(16)` (UUID binario) — persistence mappers convierten con `bytesToUuid` / `uuidToBytes`
- Timestamps: `TIMESTAMP` con `DEFAULT CURRENT_TIMESTAMP` y `ON UPDATE CURRENT_TIMESTAMP`
- Booleanos: `is_active`, `is_current`, `is_mandatory`, `requires_documentation`
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
- **Refactor global de VOs** — todos los `@Value` class de Lombok migrados a `record` de Java 17:
  - `shared/geography/`: `CountryId`, `ProvinceId`, `PlaceId`
  - `geography/`: `GeographicName`, `IsoCode`, `PhoneCode`, `PostalCode`, `ProvinceCode`
  - `academic/ids/`: `AcademicYearId`, `GradeLevelId`, `OrientationId`, `SubjectId`, `RegistryId`,
    `StudyPlanId`, `PeriodId`, `CourseId`, `EvaluationId`, `EvaluationTypeId`, `WithdrawalReasonId`
  - `academic/`: `Year`, `YearLevel`, `Division`, `OrientationCode`, `SubjectCode`,
    `WeeklyHours`, `PeriodNumber`, `RegistryNumber`
  - `auth/`: `UserId`, `RoleId`, `BlacklistedTokenId`, `HashedPassword`, `PlainPassword`, `RoleName`
- `students/personal/` — domain layer ✅ + application layer ✅ completos
- `students/health/` — domain layer ✅ completo
- `students/enrollment/` — domain layer ✅ completo
- `students/records/` — domain layer ✅ completo
- Flyway V1–V7, V10–V12, V14 ejecutados
- Seeders, OpenAPI

### ⏳ En construcción

- `students/personal/` — infrastructure layer (JPA entity, adapter, controller) ← **próximo**
- `students/health/` — application + infrastructure layers
- `students/enrollment/` — application + infrastructure layers
- `students/records/` — application + infrastructure layers
- `students/parents/` — todo (domain, application, infrastructure) — TODO en CreateStudentUseCase pasos 12-13
- `teachers/` — asignación a cursos
- Calificaciones por período y promedio final
- Rate limiting, auditoría, métricas, email service (password aleatorio para padres)

### 🎯 Próximo paso — Infrastructure layer de `students/personal/`

```
infrastructure/
├── persistence/
│   ├── entity/      StudentPersonalDataJpaEntity.java
│   ├── repository/  StudentPersonalDataJpaRepository.java
│   ├── adapter/     StudentPersonalDataRepositoryAdapter.java
│   └── mapper/      StudentPersonalDataPersistenceMapper.java  ← aplanar Address, bytesToUuid/uuidToBytes
├── web/
│   ├── controller/  StudentController.java  ← @PreAuthorize por rol
│   ├── mapper/      StudentWebMapper.java   ← application DTO ↔ web DTO
│   └── exception/   StudentExceptionHandler.java (@RestControllerAdvice)
└── (seeder opcional para dev)
```
