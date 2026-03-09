# CLAUDE.md — Sistema de Gestión Escolar IPET 132

> Guía de contexto, arquitectura y comportamiento para agentes de IA trabajando en este proyecto.
> **Leer completo antes de modificar cualquier archivo.**

---

## 🎯 Propósito del Proyecto

Sistema de gestión escolar para el **IPET 132** (Argentina).
**Stack:** Java 17 + Spring Boot 3.2.x + Spring Security 6 + MySQL 8
**Package raíz:** `org.school.management`
**Estado actual:** Auth ✅ + Geography ✅ + Academic ✅ — Students en construcción.

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
students/     → Estudiantes, salud, matrícula, legajo ⏳
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
│   └── Email.java
├── geography/domain/valueobject/
│   ├── CountryId.java
│   ├── ProvinceId.java
│   └── PlaceId.java
└── domain/exception/
    └── DomainException.java    # Clase base abstracta — nunca lanzar directamente
```

**Regla de oro:** Si un concepto aparece en más de un bounded context, va al Shared Kernel.

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

### `students/` — A implementar

Estructura aprobada — **4 agregados separados** (NO una God Table):

```
students/
├── personal/     → StudentPersonalData (identidad civil, domicilio, fotos DNI)
├── health/       → StudentHealthRecord (ficha médica, obra social, alergias)
├── enrollment/   → StudentEnrollment (matrícula por ciclo, baja, promedio final)
└── records/      → StudentRecord + RecordDocuments (legajo digital con documentación)
```

Cada subdirectorio tiene su propia estructura `domain/ / application/ / infrastructure/`.

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
| **Roles como String** | MVP — roles fijos: ADMIN, TEACHER, STUDENT, PARENT, STAFF |
| **Token Rotation** | Seguridad OWASP |
| **Records para DTOs** | Inmutabilidad Java 17 |
| **MapStruct en 3 capas** | Type-safe en compile-time, sin reflection |
| **Flyway obligatorio** | Nunca `ddl-auto: create` |
| **Shared Kernel** | Evitar duplicación de DNI, Email, PhoneNumber, IDs geográficos |
| **Students en 4 agregados** | Evitar God Table — separación real de responsabilidades |
| **Folio automático** | `FolioAssignmentService` transaccional garantiza unicidad |
| **Password inicial estudiante** | `{DNI}Ipet132!` — simple para el admin |
| **Password padre** | Aleatorio seguro — enviado por email |
| **Geography endpoints públicos** | Formularios de alta necesitan autocompletado sin auth |
| **Ciclo básico sin orientación** | 1°–3° comunes; 4°–7° orientación obligatoria |
| **Dos capas de DTOs** | `application/dto/` puros; `infrastructure/web/dto/` con Jakarta |
| **Naming adapter vs impl** | Auth usa `*RepositoryImpl` (histórico); módulos nuevos usan `*RepositoryAdapter` |

---

## 📐 Convenciones de Código

### Value Object (inmutable con validación)

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
- **Consultar el Shared Kernel** antes de crear un Value Object — puede ya existir.
- **Usar `FolioAssignmentService`** al asignar folios a estudiantes — nunca hacerlo manualmente.
- **Geography es de lectura pública** — no requiere autenticación.

### ❌ Nunca hacer

- **Nunca importar** `jakarta.persistence.*` o `org.springframework.*` en `domain/`.
- **Nunca exponer** entidades JPA en la API — siempre DTO via mapper.
- **Nunca inyectar** `JpaRepository` directamente en un Use Case.
- **Nunca poner lógica de negocio** en controllers ni en entidades JPA.
- **Nunca modificar** migraciones Flyway ya ejecutadas.
- **Nunca usar** `ddl-auto: create` o `update`.
- **Nunca hardcodear** secretos, contraseñas o URLs.
- **Nunca cruzar** bounded contexts con clases completas — solo IDs o Shared Kernel.
- **Nunca duplicar** `Dni`, `Email`, `PhoneNumber` — están en `shared/`.
- **Nunca usar herencia** entre entidades de distintos bounded contexts.

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
13. `infrastructure/persistence/mapper/` — MapStruct domain ↔ entity
14. `infrastructure/web/dto/` — DTOs de API con Jakarta
15. `infrastructure/web/controller/` — REST con `@PreAuthorize`
16. `infrastructure/web/mapper/` — MapStruct application ↔ web DTO
17. `infrastructure/web/exception/` — `@RestControllerAdvice`
18. `infrastructure/seeder/` — datos iniciales para perfil `dev`
19. `db/migration/V{n}__create_{context}_tables.sql`

### 🔗 Al implementar Students — dependencias permitidas

```java
// ✅ Permitido — solo IDs y Shared Kernel

// ❌ Prohibido — clases completas de otro bounded context

```

Flujo transaccional de creación de estudiante (14 pasos, TODO O NADA):

```
1.  Validar DNI no existente
2.  Obtener AcademicYear activo (debe existir uno ACTIVE)
3.  Validar GradeLevel existe y está activo
4.  FolioAssignmentService.assignNextFolio()
5.  Generar password: {DNI}Ipet132!
6.  Crear User en auth (dni, password hasheado, roles: [STUDENT])
7.  Crear StudentPersonalData
8.  Crear StudentHealthRecord
9.  RecordNumberGenerator.generate() → LEG-{año}-{secuencia}
10. Crear StudentRecord (registry_id + folio)
11. Buscar o crear Parent (con su propio User, password aleatorio)
12. Crear StudentParent (relación + flags)
13. Crear Enrollment
14. Commit → retornar response
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

**Próxima:** `V8__create_students_tables.sql`

Convención de columnas: PK UUID, timestamps DATETIME, booleanos `is_active` / `is_current`.

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
- Shared Kernel, Flyway V1–V7, Seeders, OpenAPI

### En construcción
- `students/` — 4 agregados, flujo transaccional de 14 pasos, legajo digital
- `teachers/` — Asignación a cursos
- Calificaciones por período y promedio final
- Rate limiting, auditoría, métricas, email service
