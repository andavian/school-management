# AGENTS.md — School Management IPET 132

> Compact instruction file for AI coding agents. Full architecture docs: `CLAUDE.md`

## Commands

```bash
mvn clean verify                          # full build + all tests
mvn test -Dgroups="unit"                  # unit tests only
mvn clean package -DskipTests             # build jar, skip tests
mvn spring-boot:run -Dspring-boot.run.profiles=dev    # run with MySQL (dev seeders)
mvn spring-boot:run -Dspring-boot.run.profiles=local  # run + Mailhog (docker run -p 1025:1025 -p 8025:8025 mailhog/mailhog)
```

- `test` profile uses H2 in-memory (`ddl-auto: create-drop`), rate-limit disabled. Unit tests use `@ExtendWith(MockitoExtension.class)` + `@Tag("unit")`.
- `dev`/`prod` use MySQL with Flyway migrations. `ddl-auto: none`.
- Swagger UI at `http://localhost:8080/swagger-ui.html`

## Architecture (non-obvious)

**Three principles combined:**
1. **Hexagonal (Ports & Adapters):** `domain/` must never import `jakarta.persistence.*` or `org.springframework.*`. Repository interfaces (ports) live in `domain/repository/`, adapters in `infrastructure/persistence/adapter/`.
2. **Vertical Slicing:** 12 bounded contexts under `org.school.management/` — `shared/`, `auth/`, `geography/`, `academic/`, `students/`, `teachers/`, `grades/`, `course/`, `attendance/`, `storage/`, `teaching-materials/`, `resources/`. A BC never imports full classes from another BC — only IDs and Shared Kernel types.
3. **Screaming Architecture:** package names say *what* the code does, not how.

**Three MapStruct layers per BC (never skip):** `*PersistenceMapper`, `*ApplicationMapper`, `*WebMapper`.

**Order for new BCs:** `domain/model/` → `domain/valueobject/` → `domain/repository/` → `domain/exception/` → `domain/service/` → `application/dto/request/` → `application/dto/response/` → `application/usecases/` → `application/mapper/` → `infrastructure/persistence/entity/` → `.../repository/` → `.../adapter/` → `.../mapper/` → `infrastructure/web/dto/` → `.../controller/` → `.../mapper/` → `.../exception/` → `infrastructure/seeder/` → Flyway migration → (if account activation) `infrastructure/event/`.

## Critical conventions

- **UUID → BINARY(16):** ALL UUID fields in JPA entities MUST use `@Convert(converter = UuidBinaryConverter.class)` with `columnDefinition = "BINARY(16)"`. Even `@Id` fields. Never type `@Id` as `byte[]`.
- **Value Objects are `record`** (Java 17), never Lombok `@Value`. IDs have `of(UUID)`, `from(UUID)`, `from(String)`, `generate()`. Primary factory = `of()`.
- **MapStruct mappers** use `componentModel = "spring"`, never field `INSTANCE`. Persistence mappers use `default methods` for compound VOs (FullName, Address).
- **Exception factory methods:** `TeacherNotFoundException.byId(id)`, not `new TeacherNotFoundException(...)`.
- **ProblemDetail (RFC 9457)** for all `@RestControllerAdvice`. HTTP codes: 404→`*NotFound`, 403→`*AccessDenied`, 409→`*AlreadyExists`, 422→invalid args, 500→`IllegalStateException`.
- **Never inject `JpaRepository`** directly in a Use Case — inject the domain repository port.
- **Use cases** are `@Service` + `@Transactional` + `@Slf4j`. One operation per class. Package-private `buildResponse()` methods for sharing within the same BC.
- **Controllers** use `SecurityContextHelper.extractUserId(userDetails)` from `auth/infra/web/` — never duplicate this cast.
- **DTOs** in `dto/request/` and `dto/response/` subfolders, held inside a container class (`XWebDto`).
- **Web requests** validate with Jakarta annotations; domain layer throws its own exceptions.
- **Flyway** for ALL schema changes: new `V{n}__description.sql` — next available: **V22**. Never edit existing migrations.
- **No physical delete** of students, teachers, or parents — use logical/state transitions.
- **Email is async** (`@Async` on `JavaMailEmailService`) — failures must never roll back transactions.

## Shared Kernel (never duplicate these)

`shared/person/domain/valueobject/`: `Dni`, `Cuil`, `FullName`, `Gender`, `Nationality`, `PhoneNumber`, `Email`, `Address`
`shared/geography/domain/valueobject/`: `CountryId`, `ProvinceId`, `PlaceId`
`shared/domain/`: `DomainException` (base), `EmailService` (port), `DomainEvent`, `AccountActivatedEvent`, `DomainEventPublisher`
`shared/infrastructure/persistence/converter/UuidBinaryConverter.java`

- `FullName.getFullName()` → `"firstName lastName"` — never call `fullName()`.
- DNI argentino has NO verification digit (it's sequential). 7 or 8 digits.
- `Address`: `students/` column is `residence_place_id`; `teachers/` and `parents/` use `place_id`.
- CUIL validates with ANSES/AFIP algorithm. Valid test CUILs: DNI `12345678` → `20123456786`, DNI `87654321` → `20876543215`.
- Account activation flow: `CreateTeacherUseCase` → `CreateUserUseCase.inactive()` → `GenerateConfirmationTokenUseCase` → email; then `POST /api/auth/activate-account` → `ActivateAccountUseCase` publishes `AccountActivatedEvent` → `TeacherAccountActivatedListener` (`@TransactionalEventListener(BEFORE_COMMIT)`).

## Notable cross-BC rules

- `auth/` uses `infra` as package name (historical); new BCs MUST use `infrastructure`.
- Cross a BC boundary via use cases (e.g., `GetPlaceByIdUseCase` from `geography/`), never via repositories.
- `storage/StorageService` is the domain port for file uploads — never use OCI SDK directly from use cases/domain.
- Ownership check in `teaching-materials/`: pass `null` as teacherId for ADMIN/STAFF bypass.
- `RegistryNumberGenerator` is ONLY for `QualificationRegistry` — use `RecordNumber.fromDni(dni)` for student records.
- No FK to `teachers` in tables of other BCs — use `teacher_id` UUID only.

## Test conventions

### Structure
- `@ExtendWith(MockitoExtension.class)` + `@Tag("unit")` for unit tests. Use `@Mock` + `@InjectMocks`.
- AAA pattern with `// given // when // then` comments. AssertJ assertions (`assertThat(...).isEqualTo(...)`).
- One scenario per method: `execute_whenX_thenY`. File: `{UseCaseName}Test.java` in `application/usecases/`.

### Mocking rules
- Never mock simple value objects (Dni, Cuil, FullName, Email, UUID-based IDs) — use `of()`/`from()` factories.
- Never mock enums (RoleName, Gender, FinalGradeStatus). Use real values.
- `Role` → `Role.reconstruct(RoleId.generate(), RoleName.of("ROLE"), ...)`. Never `mock(Role.class)`.
- Models with `@Builder` (Parent, StudentRecord, etc.) → build via builder, never `mock()`.
- Only `mock()` complex models when the use case calls their methods.
- `TokenHashUtil` is static — cannot mock; use `anyString()` matchers in verify.
- `SecurityContextHelper.extractUserId(any())` and `SecurityContextHelper.extractRoles(any())` can be mocked via `mockStatic`.

### Strict stubbing (Mockito strict mode)
- When repo returns `Optional.empty()` or empty list → remove stubs for mappers that won't be called.
- If a code path uses a mapper only conditionally, stub only for the path being tested.
- `UnnecessaryStubbingException` means a stub is declared but never consumed — remove it.

### Running subsets
```bash
mvn test -Dgroups="unit" -Dtest="org.school.management.{bc}.**.*Test"  # one BC
mvn test -Dgroups="unit" -Dtest="org.school.management.{bc}.application.usecases.*Test,org.school.management.{bc2}.application.usecases.*Test"  # multiple
```

### Known pre-existing failures (3 tests, not caused by recent changes)
- `CreateTeacherUseCaseTest`: 2 errors — `TokenHasher` not mocked (null pointer).
- `CorrectAttendanceUseCaseTest.correctCourse_whenPresentToAbsent_thenRecalculatesSummary`: 1 failure.

## Bruno API Testing (endpoint tests)

**MCP server:** `bruno-mcp` installed globally (`npm install -g bruno-mcp --legacy-peer-deps`). Configured in `opencode.json` as a local MCP server. Tool available: `run-collection`.

```bash
# Run the collection via Bruno CLI directly
bru run "src/test/bruno/School Management System API" --env "src/test/bruno/School Management System API/environments/Local Development.yml"

# Or via the MCP tool (from OpenCode session)
# run-collection with collection="src/test/bruno/School Management System API" environment="src/test/bruno/School Management System API/environments/Local Development.yml"
```

Server must be running (`mvn spring-boot:run -Dspring-boot.run.profiles=dev`) before running Bruno tests.

### Notes

Bruno collection at `src/test/bruno/School Management System API/`. Requests organized as `NN-BoundedContext/` folders.

### Auth flow

- Login request stores token via `bru.setEnvVar("jwt_token", accessToken, { persist: true })`.
- All requests use `auth: inherit` → collection-level Bearer `{{jwt_token}}` from `opencollection.yml`.
- Multi-step flows: first request is always a login (`01 - POST Login Admin.yml`), runner executes sequentially.

### Request conventions

- URL always `"{{baseUrl}}/api/..."` — never hardcoded host.
- Body: `json` for JSON APIs, `multipart-form` for file uploads.
- Never set `Content-Type` header — Bruno infers from body type.
- `settings` block always includes: `encodeUrl: true`, `timeout: 0`, `followRedirects: true`, `maxRedirects: 5`.

### After-response script (copy this block)

Every request copies this "universal ID extractor" that maps response fields to env vars:

```javascript
if (res.status >= 200 && res.status < 300 && res.status !== 204) {
    const body = res.getBody();
    const source = Array.isArray(body) ? body[0] : body;
    if (source && typeof source === "object") {
        const vars = {
            accessToken: "jwt_token", refreshToken: "refresh_token",
            userId: "current_user_id", academicYearId: "academic_year_id",
            gradeLevelId: "grade_level_id", subjectId: "subject_id",
            teacherId: "teacher_id", studentId: "current_student_id",
            parentId: "parent_id", enrollmentId: "enrollment_id",
            courseSubjectId: "course_subject_id", evaluationId: "evaluation_id",
            finalGradeId: "final_grade_id", materialId: "material_id",
            resourceId: "resource_id", unitId: "unit_id",
            reservationId: "reservation_id", recordId: "record_id",
            documentId: "document_id", documentTypeId: "document_type_id",
            dailyAttendanceId: "daily_attendance_id",
            courseAttendanceId: "course_attendance_id",
            placeId: "place_id", provinceId: "province_id", countryId: "country_id"
        };
        Object.entries(vars).forEach(([field, envName]) => {
            if (source[field]) bru.setEnvVar(envName, source[field], { persist: true });
        });
    }
}
```

Use `bru.setEnvVar(name, value, { persist: true })` (persists across requests). Use `bru.setVar(name, value)` (request-scoped) only for temporary values like `upload_metadata` in multipart requests.

### Tests (always include)

```javascript
test("<Name> responde <STATUS>", () => { expect(res.status).to.equal(<expected>); });
test("<Name> devuelve cuerpo valido", () => {
    if (res.status !== 204) expect(res.getBody()).to.not.be.undefined;
});
```

Expected status: POST → 201, GET/PATCH → 200, DELETE not used.

### Multipart file upload

Only exception to the standard pattern. Requires a `before-request` script that:
1. Uses `require('fs')` / `require('path')` to read a test file from disk.
2. Builds metadata JSON and stores it via `bru.setVar("upload_metadata", JSON.stringify(...))`.
3. Body type `multipart-form` with fields: `file` (the file buffer) and `metadata` referencing `{{upload_metadata}}`.

### Environment variables

- Pre-seeded IDs (Flyway data) go in `environments/Local Development.yml`.
- Runtime-populated IDs: add empty string placeholder (`value: ""`) in the env file, populate from after-response script.
- Secret vars (`admin_password`, `jwt_token`, `refresh_token`, `activation_token`) marked `secret: true`.

### Sync with OpenAPI

Collection references `http://localhost:8080/v3/api-docs.yaml` with auto-sync enabled (`autoCheck: true`, interval 5 min). Server must be running to sync.

## Dev credentials

| Role | DNI | Password |
|------|-----|----------|
| ADMIN | `10000001` | `Admin123!` |
| TEACHER | `12345678` | `Teacher123!` |
| STUDENT | `11223344` | `11223344Ipet132!` |
| PARENT | `98765432` | `Parent123!` |
