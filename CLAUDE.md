# CLAUDE.md — Sistema de Gestión Escolar IPET 132

> Guía de contexto, arquitectura y comportamiento para agentes de IA trabajando en este proyecto.
> **Leer completo antes de modificar cualquier archivo.**

---

## 🎯 Propósito del Proyecto

Sistema de gestión escolar para el **IPET 132** (Argentina).
**Stack:** Java 17 + Spring Boot 3.2.x + Spring Security 6 + MySQL 8
**Package raíz:** `org.school.management`
**Estado actual:** Auth ✅ + Geography ✅ + Academic ✅ + Students domain/personal ✅ (en construcción)

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
students/     → Estudiantes, salud, matrícula, legajo ⏳ (domain/personal ✅)
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
│   ├── FullName.java
│   ├── Gender.java        # Enum: MALE, FEMALE, OTHER
│   ├── Nationality.java
│   ├── PhoneNumber.java
│   ├── Email.java
│   ├── Cuil.java          # CUIL argentino — validación dígito verificador ANSES/AFIP ← NUEVO
│   ├── CuilType.java      # Enum: MALE_ARGENTINEAN, FEMALE_ARGENTINEAN, LEGAL_ENTITY... ← NUEVO
│   └── Address.java       # Domicilio postal — encapsula street, number, floor, apt, PlaceId, CP ← NUEVO
├── geography/domain/valueobject/
│   ├── CountryId.java
│   ├── ProvinceId.java
│   └── PlaceId.java
└── domain/exception/
    └── DomainException.java    # Clase base abstracta — nunca lanzar directamente
```

**Regla de oro:** Si un concepto aparece en más de un bounded context, va al Shared Kernel.

### Notas sobre los nuevos Value Objects del Shared Kernel

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
- El persistence mapper es responsable de **aplanar** los campos de `Address` a las columnas de BD (`address_street`, `address_number`, `address_floor`, `address_apartment`, `residence_place_id`, `postal_code`)
- `toStringFormatted(String localityName)` para documentos/PDFs — requiere nombre de localidad resuelto externamente
- **No override de `equals`/`hashCode`** — el `record` compara todos los campos (semántica correcta)

---

## 📁 Estructura por Bounded Context

### `auth/` completado

```
auth/
├── domain/
│   ├── model/           User, RefreshToken, BlacklistedToken
│   ├── valueobject/     UserId, HashedPassword, PlainPassword, RoleName, ...
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

### `geography/` completado

```
geography/
├── domain/
│   ├── model/           Country, Province, Place, PlaceWithHierarchy
│   ├── valueobject/     IsoCode, PhoneCode, ProvinceCode, PostalCode,
│   │                    GeographicName, PlaceType (enum), ids/
│   └── repository/      CountryRepository, ProvinceRepository,
│                        PlaceRepository, GeographyQueryRepository
├── application/
│   ├── usecases/        GetPlaceById, SearchPlaces, ListPlacesByProvince, CreatePlace,
│   │                    ListProvincesByCountry, SearchProvinces, ListCountries,
│   │                    GetCountryByIsoCode, GlobalSearch, GetGeographyStatistics
│   ├── dto/             request/ + response/ (Records)
│   └── mappers/         GeographyApplicationMapper (MapStruct)
└── infrastructure/
    ├── web/controller/  GeographyController (público), GeographyAdminController (ADMIN)
    ├── persistence/     entity/, repository/, adapter/, mapper/
    └── seeder/          GeographyDataSeeder — Argentina: 1 país, 24 provincias, ~45 lugares
```

### `academic/` completado

```
academic/
├── domain/
│   ├── model/           AcademicYear, Orientation, GradeLevel, Subject,
│   │                    StudyPlan, EvaluationPeriod, QualificationRegistry
│   ├── valueobject/     Year, YearLevel, Division, RegistryNumber, SubjectCode,
│   │                    WeeklyHours, OrientationCode, enums/, ids/
│   ├── repository/      7 interfaces de repositorio (puertos)
│   ├── service/         FolioAssignmentService ← CRÍTICO para Students
│   │                    RegistryNumberGenerator
│   │                    AcademicYearActivationService
│   │                    GradeLevelValidationService
│   │                    StudyPlanManagementService
│   └── exception/       20+ excepciones organizadas por entidad
├── application/
│   ├── usecases/        22 use cases (AcademicYear×6, Orientation×6,
│   │                    GradeLevel×5, Subject×5)
│   ├── dto/             request/ + response/ con validaciones Jakarta
│   └── mapper/          AcademicApplicationMapper (MapStruct)
└── infrastructure/
    ├── web/
    │   ├── controller/  AcademicYearController, OrientationController,
    │   │                GradeLevelController, SubjectController
    │   └── exception/   AcademicExceptionHandler (@RestControllerAdvice)
    ├── persistence/      entity/, repository/, adapter/, mapper/
    └── seeder/           AcademicDataSeeder — 2 años, 2 orientaciones, 37 cursos, ~60 materias
```

### `students/` — En construcción

Estructura aprobada — **4 agregados separados** (NO una God Table):

```
students/
├── personal/     → StudentPersonalData (identidad civil, domicilio) ✅ domain layer completo
├── health/       → StudentHealthRecord (ficha médica, obra social, alergias) ⏳
├── enrollment/   → StudentEnrollment (matrícula por ciclo, baja, promedio final) ⏳
└── records/      → StudentRecord + RecordDocuments (legajo digital con documentación) ⏳
```

Cada subdirectorio tiene su propia estructura `domain/ / application/ / infrastructure/`.

#### `students/personal/` — Domain Layer ✅ COMPLETO

```
students/personal/
└── domain/
    ├── model/
    │   └── StudentPersonalData.java        # Agregado principal — @Builder + @Getter + Lombok
    ├── valueobject/
    │   └── StudentPersonalDataId.java      # record UUID — consistente con el resto del proyecto
    ├── repository/
    │   └── StudentPersonalDataRepository.java   # Puerto del dominio
    └── exception/
        ├── StudentNotFoundException.java
        ├── StudentAlreadyExistsException.java
        └── InvalidStudentDataException.java
```

**Decisiones de diseño tomadas en `students/personal/domain/`:**

- `StudentPersonalData` usa `@Builder` de Lombok (no constructor manual) + `@Getter` + `@EqualsAndHashCode(onlyExplicitlyIncluded = true)`
- Los campos mutables (`phone`, `email`, `address`, `updatedAt`) no son `final` — se modifican via métodos de negocio (`updateContactInfo`, `updateAddress`, `updatePersonalData`)
- `create(StudentPersonalDataBuilder builder)` es el **factory method con validaciones de dominio** — siempre usar esto, nunca `.builder().build()` directo
- Validación cruzada **CUIL ↔ DNI** en `create()` — `cuil.extractDni()` debe coincidir con `dni`
- `birthDate` solo valida que no sea futuro — sin rango de edad rígido (flexible para casos especiales)
- `Address` está embebido en `StudentPersonalData` (no `PlaceId` suelto) — el persistence mapper aplana a columnas BD
- **No hay `deleteByStudentId`** en el puerto — la baja es lógica y se gestiona desde `StudentEnrollment`
- Excepciones siguen el patrón: **constructor genérico + factory methods estáticos nombrados** (`StudentNotFoundException.byId(id)`, `StudentNotFoundException.byDni(dni)`, `StudentAlreadyExistsException.withDni(dni)`, `StudentAlreadyExistsException.withCuil(cuil)`)

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
| Lombok | (Boot managed) | Boilerplate |
| Flyway | (Boot managed) | Migraciones de esquema |
| SpringDoc OpenAPI | latest | Swagger UI |
| JUnit 5 + Mockito | (Boot managed) | Testing |

---

## 🔐 Decisiones de Dominio — NO Cambiar Sin Discutir

| Decisión | Razón |
|----------|-------|
| **DNI como username** | Identificador universal en Argentina |
| **Email opcional** | Estudiantes menores no tienen email |
| **UUID como PK** | Preparado para microservicios |
| **BINARY(16) para UUIDs en BD** | Consistente en todo el proyecto — persistence mappers incluyen `bytesToUuid` / `uuidToBytes` |
| **Roles como String** | MVP — roles fijos: ADMIN, TEACHER, STUDENT, PARENT, STAFF |
| **Token Rotation** | Seguridad OWASP |
| **Records para DTOs y Value Objects simples** | Inmutabilidad Java 17 |
| **Lombok @Builder para modelos de dominio complejos** | Ergonomía — `StudentPersonalData` tiene +15 campos |
| **MapStruct en 3 capas** | Type-safe en compile-time, sin reflection |
| **Flyway obligatorio** | Nunca `ddl-auto: create` |
| **Shared Kernel** | Evitar duplicación — `Dni`, `Email`, `PhoneNumber`, `Cuil`, `Address`, IDs geográficos |
| **Students en 4 agregados** | Evitar God Table — separación real de responsabilidades |
| **Folio automático** | `FolioAssignmentService` transaccional garantiza unicidad |
| **Password inicial estudiante** | `{DNI}Ipet132!` — simple para el admin |
| **Password padre** | Aleatorio seguro — enviado por email |
| **Geography endpoints públicos** | Formularios de alta necesitan autocompletado sin auth |
| **Ciclo básico sin orientación** | 1°–3° comunes; 4°–7° orientación obligatoria |
| **Dos capas de DTOs** | `application/dto/` puros; `infrastructure/web/dto/` con Jakarta |
| **Naming adapter vs impl** | Auth usa `*RepositoryImpl` (histórico); módulos nuevos usan `*RepositoryAdapter` |
| **Baja de estudiante es lógica** | No hay delete físico — se gestiona desde `StudentEnrollment` |
| **Address encapsula PlaceId** | `Address` en Shared Kernel incluye `PlaceId` — domicilio completo como VO |
| **Excepciones: constructor + factory methods** | Flexibilidad + consistencia de mensajes (`byId`, `byDni`, `withDni`, `withCuil`) |
| **Validación CUIL↔DNI en dominio** | Regla de negocio argentina — el DNI embebido en el CUIL debe coincidir |

---

## 📐 Convenciones de Código

### Value Object simple (record)

```java
// domain/valueobject/YearLevel.java
public record YearLevel(int value) {
    public YearLevel {
        if (value < 1 || value > 7)
            throw new IllegalArgumentException("Year level must be between 1 and 7");
    }
    public static YearLevel of(int value) { return new YearLevel(value); }
    public boolean requiresOrientation() { return value >= 4; }
}
```

### Value Object complejo con lógica (record con métodos)

```java
// shared/person/domain/valueobject/Cuil.java
public record Cuil(String value) {
    public Cuil { /* validar prefijo, dígito verificador */ }
    public static Cuil of(String value) { return new Cuil(value); }
    public Dni extractDni() { ... }
    public String formatted() { ... }
    public CuilType getType() { ... }
}
```

### Modelo de dominio con Lombok (cuando tiene muchos campos)

```java
// students/personal/domain/model/StudentPersonalData.java
@Getter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class StudentPersonalData {
    @EqualsAndHashCode.Include
    private final StudentPersonalDataId studentId;
    // campos final para inmutables, sin final para mutables
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
// Uso en use case:
throw StudentNotFoundException.byDni(dni.value());
```

### Domain Service

```java
// domain/service/FolioAssignmentService.java
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FolioAssignmentService {
    private final QualificationRegistryRepository registryRepository;

    public Integer assignNextFolio() {
        // 1. obtener registro ACTIVE, 2. verificar no full,
        // 3. asignar folio, 4. incrementar, 5. guardar
    }
}
```

### Use Case

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class GetAcademicYearUseCase {

    private final AcademicYearRepository academicYearRepository; // puerto del dominio
    private final AcademicApplicationMapper mapper;

    public AcademicYearResponse execute(UUID id) {
        return academicYearRepository.findById(AcademicYearId.of(id))
            .map(mapper::toAcademicYearResponse)
            .orElseThrow(() -> new AcademicYearNotFoundException(id));
    }
}
```

### Repository (Puerto + Adaptador)

```java
// domain/repository/AcademicYearRepository.java  ← PUERTO
public interface AcademicYearRepository {
    Optional<AcademicYear> findById(AcademicYearId id);
    Optional<AcademicYear> findCurrentYear();
    AcademicYear save(AcademicYear year);
}

// infrastructure/persistence/adapter/AcademicYearRepositoryAdapter.java  ← ADAPTADOR
@Repository
@RequiredArgsConstructor
public class AcademicYearRepositoryAdapter implements AcademicYearRepository {
    private final AcademicYearJpaRepository jpaRepository;
    private final AcademicPersistenceMapper mapper;

    @Override
    public Optional<AcademicYear> findCurrentYear() {
        return jpaRepository.findByIsCurrentTrue().map(mapper::toDomain);
    }
}
```

### Controller (Adaptador REST)

```java
@RestController
@RequestMapping("/api/admin/academic-years")
@RequiredArgsConstructor
@Tag(name = "Academic Years")
public class AcademicYearController {

    private final CreateAcademicYearUseCase createUseCase; // nunca JpaRepository

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AcademicYearResponse> create(
            @Valid @RequestBody CreateAcademicYearRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createUseCase.execute(request));
    }
}
```

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
- **Consultar el Shared Kernel** antes de crear un Value Object — puede ya existir (`Cuil`, `Address`, `Dni`, `Email`, `PhoneNumber`, `PlaceId`...).
- **Usar `FolioAssignmentService`** al asignar folios a estudiantes — nunca hacerlo manualmente.
- **Geography es de lectura pública** — no requiere autenticación.
- **Usar factory methods de excepción** — `StudentNotFoundException.byDni(dni)` no `new StudentNotFoundException("...")`.
- **Usar `StudentPersonalData.create(builder)`** — nunca `.builder().build()` directo en el dominio students.

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
- **Nunca usar herencia** entre entidades de distintos bounded contexts.
- **Nunca hacer delete físico** de estudiantes — la baja es lógica via `StudentEnrollment`.

### 🔍 Al analizar código existente

1. Identificar a qué bounded context y capa pertenece el archivo.
2. Verificar si la modificación cruza alguna frontera arquitectónica.
3. Si se detecta una violación existente, mencionarla pero **no corregirla** salvo pedido explícito.
4. Respetar el naming del módulo: `*RepositoryImpl` (auth), `*RepositoryAdapter` (módulos nuevos).

### 🧩 Al crear un nuevo Bounded Context

Orden estricto de implementación:

1. `domain/model/` — entidades del dominio con métodos de negocio
2. `domain/valueobject/` — value objects con validación en constructor
3. `domain/repository/` — interfaces (puertos)
4. `domain/exception/` — excepciones semánticas por entidad
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

// ❌ Prohibido — clases completas de otro bounded context
import org.school.management.academic.domain.model.GradeLevel; // NUNCA
import org.school.management.auth.domain.model.User;           // NUNCA
```

Flujo transaccional de creación de estudiante (14 pasos, TODO O NADA):

```
1.  Validar DNI no existente (StudentPersonalDataRepository.existsByDni)
2.  Validar CUIL no existente (StudentPersonalDataRepository.existsByCuil)
3.  Obtener AcademicYear activo (debe existir uno ACTIVE)
4.  Validar GradeLevel existe y está activo
5.  FolioAssignmentService.assignNextFolio()
6.  Generar password: {DNI}Ipet132!
7.  Crear User en auth (dni, password hasheado, roles: [STUDENT])
8.  Crear StudentPersonalData (via StudentPersonalData.create(builder))
9.  Crear StudentHealthRecord
10. RecordNumberGenerator.generate() → LEG-{año}-{secuencia}
11. Crear StudentRecord (registry_id + folio)
12. Buscar o crear Parent (con su propio User, password aleatorio)
13. Crear StudentParent (relación + flags)
14. Crear Enrollment
15. Commit → retornar response
```

### 🧪 Al generar tests

```java
// Unit test
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class CreateAcademicYearUseCaseTest {

    @Mock
    private AcademicYearRepository academicYearRepository; // mock del PUERTO

    @InjectMocks
    private CreateAcademicYearUseCase useCase;

    @Test
    void execute_whenYearDoesNotExist_thenReturnResponse() { ... }

    @Test
    void execute_whenYearAlreadyExists_thenThrowAcademicYearAlreadyExistsException() { ... }
}

// Integration test
@SpringBootTest
@Tag("integration")
@ActiveProfiles("test")
@Transactional
class AcademicYearControllerIntegrationTest { /* usa H2 */ }
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

**Correcciones aplicadas respecto al diseño original (V10–V14):**
- `BINARY(16)` para todos los UUIDs (no `VARCHAR(36)`)
- `cuil VARCHAR(11)` agregado en `student_personal_data` — obligatorio, normalizado sin guiones
- `residence_place_id` en lugar de `place_id` — nombre semántico para domicilio
- `status VARCHAR(20) DEFAULT 'PENDING'` en `record_documents` — estado por documento
- `record_documents.updated_at` con `ON UPDATE CURRENT_TIMESTAMP`
- `parents.updated_at` con `ON UPDATE CURRENT_TIMESTAMP`
- `withdrawal_reasons.requires_documentation BOOLEAN` — indica si la baja requiere adjunto
- `student_enrollments` UNIQUE KEY es `(student_id, academic_year_id)` — permite cambio de curso en el mismo año
- Tabla renombrada de `enrollments` a `student_enrollments`

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

### Completado
- `auth/` — JWT, refresh, blacklist, sesiones, creación de usuarios
- `geography/` — Geografía argentina con búsqueda y jerarquía
- `academic/` — Años, orientaciones, cursos, materias, registro de calificaciones, 22 use cases, Swagger
- Shared Kernel ampliado: `Cuil`, `CuilType`, `Address` agregados
- Flyway V1–V7, V10–V12, V14 ejecutados
- Seeders, OpenAPI

### En construcción
- `students/personal/` — domain layer ✅ completo | application layer ⏳ siguiente
- `students/health/` — pendiente
- `students/enrollment/` — pendiente
- `students/records/` — pendiente
- `teachers/` — Asignación a cursos
- Calificaciones por período y promedio final
- Rate limiting, auditoría, métricas, email service

### Próximo paso
**Application layer de `students/personal/`:**
- `CreateStudentRequest.java` — DTO con validaciones Jakarta
- `StudentPersonalDataResponse.java` — DTO response
- `StudentPersonalDataApplicationMapper.java` — MapStruct domain → response
