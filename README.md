# 🎓 Sistema de Gestión Escolar IPET 132

Sistema integral de gestión escolar desarrollado con **Spring Boot**, siguiendo principios de **Arquitectura Hexagonal**, **Vertical Slicing** y **Screaming Architecture**.

---

## 📖 Descripción del Proyecto

Sistema de gestión escolar para el **IPET 132** (Argentina) que permite:

- ✅ Autenticación y autorización con JWT
- ✅ Login con **DNI** como identificador principal
- ✅ Gestión de sesiones y tokens de refresco con rotación (OWASP)
- ✅ Sistema de roles y permisos (ADMIN, TEACHER, STUDENT, PARENT, STAFF)
- ✅ Gestión de geografía argentina (países, provincias, localidades)
- ✅ Gestión académica completa (años, orientaciones, cursos, materias, legajo)
- ✅ Módulo de estudiantes — **COMPLETO** (personal, salud, inscripción, legajo, padres)
- ✅ Módulo de docentes — **COMPLETO** (con flujo de activación de cuenta por email)
- ✅ Servicio de email — **COMPLETO** (OCI SMTP, link de activación, async)
- ✅ Módulo de calificaciones — **COMPLETO** (evaluaciones, notas de período, nota final, libro matriz)
- ✅ Módulo de cursos — **COMPLETO** (asignación profesor-materia-curso, inscripción de alumnos)
- ✅ Módulo de asistencia — **COMPLETO** (diaria por preceptor, por materia por docente, resúmenes y alumnos en riesgo)
- ✅ Infraestructura de eventos de dominio — **COMPLETO** (`DomainEvent`, `AccountActivatedEvent`, `DomainEventPublisher`)

### 🎯 Características Principales

- **DNI como username** — sistema adaptado a la realidad argentina (siempre 8 dígitos)
- **CUIL validado** — dígito verificador ANSES/AFIP, obligatorio en students, teachers y parents
- **Email opcional para estudiantes** — menores sin email propio
- **Email obligatorio para padres y profesores** — notificaciones y credenciales
- **Activación de cuenta teacher via eventos de dominio** — JWT de activación (48h) + `AccountActivatedEvent` desacoplado
- **Fronteras de BC limpias en `auth/`** — `auth/` solo gestiona identidad y sesión, sin conocer otros BCs
- **`SecurityContextHelper` centralizado** — extracción de userId sin duplicación en cada controller
- **Records Java 17** — todos los Value Objects son `record` nativo
- **Token Rotation** — máxima seguridad en refresh tokens
- **Flujo transaccional de 15 pasos** — creación de estudiante TODO O NADA
- **BINARY(16) para UUIDs** — conversión transparente via `UuidBinaryConverter` compartido
- **ProblemDetail (RFC 9457)** — respuestas de error estandarizadas en toda la API
- **Sin delete físico** — bajas lógicas via `StudentEnrollment`
- **Email asíncrono** — nunca bloquea ni revierte transacciones de negocio
- **Control de asistencia ponderado** — ABSENT=1.0, JUSTIFIED=1.0, LATE=0.2, WITHDRAWN=0.2

---

## 🏗️ Arquitectura

### Arquitectura Hexagonal (Ports & Adapters)

```
┌─────────────────────────────────────────────────────────┐
│                    INFRASTRUCTURE                        │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │ REST API     │  │ Persistence  │  │ Security     │  │
│  │ (Controllers)│  │ (JPA/MySQL)  │  │ (JWT)        │  │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘  │
├─────────┼─────────────────┼──────────────────┼──────────┤
│         │   APPLICATION LAYER                           │
│         │  ┌─────────────────────────────────┐         │
│         └──│  Use Cases + DTOs + Mappers     │         │
│            └────────────┬────────────────────┘         │
├─────────────────────────┼───────────────────────────────┤
│         DOMAIN LAYER (Core Business)                    │
│  ┌──────────────────────┴──────────────────────────┐   │
│  │  Entities, Value Objects (records), Domain      │   │
│  │  Services, Domain Events, Repository Interfaces │   │
│  └─────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
```

### Vertical Slicing (Bounded Contexts)

```
shared/         → Shared Kernel (Dni, Cuil, Email, PhoneNumber, Address, Gender,
                                 IDs geográficos, UuidBinaryConverter, EmailService,
                                 DomainEvent, AccountActivatedEvent, DomainEventPublisher)
auth/           → Autenticación y autorización ✅
geography/      → Lugares geográficos (País, Provincia, Localidad) ✅
academic/       → Estructura académica (Años, Cursos, Materias) ✅
students/       → Gestión de estudiantes ✅ COMPLETO
teachers/       → Gestión de profesores ✅ COMPLETO
grades/         → Calificaciones ✅ COMPLETO
course/         → Asignación profesor-materia-curso ✅ COMPLETO
attendance/     → Asistencia diaria y por materia ✅ COMPLETO
```

---

## 💻 Tecnologías

| Tecnología | Versión | Uso |
|------------|---------|-----|
| Java | 17 | Records, Value Objects inmutables, pattern matching instanceof |
| Spring Boot | 3.3.4 | Framework principal |
| Spring Security | 6.x | Auth/Authz + JWT |
| Spring Data JPA | Boot managed | Persistencia |
| MySQL | 8 | Producción |
| H2 | test scope | Tests |
| jjwt | 0.12.6 | JWT access + refresh + confirmation tokens |
| MapStruct | 1.6.2 | Mapeo type-safe entre capas (3 capas: persistence/application/web) |
| Lombok | Boot managed | Solo modelos complejos (@Builder, @Getter) |
| Flyway | Boot managed | Migraciones de esquema |
| SpringDoc OpenAPI | 2.5.0 | Swagger UI |
| spring-boot-starter-mail | Boot managed | SMTP via JavaMailSender (OCI Email Delivery) |
| JUnit 5 + Mockito | Boot managed | Testing |

---

## 📁 Estructura del Proyecto

```
src/main/java/org/school/management/
│
├── shared/                                          # Shared Kernel
│   ├── person/domain/valueobject/
│   │   ├── Dni.java, Cuil.java, FullName.java
│   │   ├── Gender.java, Nationality.java
│   │   ├── PhoneNumber.java, Email.java
│   │   └── Address.java
│   ├── geography/domain/valueobject/
│   │   └── CountryId.java, ProvinceId.java, PlaceId.java
│   ├── domain/
│   │   ├── exception/DomainException.java
│   │   ├── service/EmailService.java               # Puerto — sin dependencias Spring
│   │   └── event/
│   │       ├── DomainEvent.java                    # Interfaz base — eventId() + occurredOn()
│   │       ├── AccountActivatedEvent.java           # Publicado al activar cuenta
│   │       └── DomainEventPublisher.java            # Puerto — publish(DomainEvent)
│   └── infrastructure/
│       ├── persistence/converter/
│       │   └── UuidBinaryConverter.java            # UUID ↔ BINARY(16)
│       ├── event/
│       │   └── SpringDomainEventPublisher.java     # Implementa DomainEventPublisher
│       ├── email/
│       │   └── JavaMailEmailService.java           # OCI SMTP + @Async
│       └── config/
│           └── AsyncConfig.java                    # @EnableAsync
│
├── auth/                                            # BOUNDED CONTEXT: Autenticación ✅
│   ├── application/usecases/
│   │   ├── CreateUserUseCase.java                  # factory puro de User — agnóstico del rol
│   │   ├── ActivateAccountUseCase.java             # activa User + publica AccountActivatedEvent
│   │   ├── GenerateConfirmationTokenUseCase.java   # JWT 48h encapsulado en auth/
│   │   ├── LoginUseCase.java
│   │   ├── ChangePasswordUseCase.java
│   │   └── GetUserProfileUseCase.java
│   └── infrastructure/
│       ├── web/
│       │   ├── SecurityContextHelper.java          # extractUserId() — static, centralizado
│       │   └── controllers/AuthController.java
│       ├── persistence/                            entity/, repository/, adapter/, mappers/
│       └── security/                              SecurityConfig, JwtTokenProvider, JwtAuthenticationFilter
│
├── geography/                                       # BOUNDED CONTEXT: Geografía ✅
│
├── academic/                                        # BOUNDED CONTEXT: Académico ✅
│   └── domain/service/
│       ├── FolioAssignmentService.java
│       └── RegistryNumberGenerator.java
│
├── students/                                        # BOUNDED CONTEXT: Estudiantes ✅ COMPLETO
│   ├── personal/   ✅
│   ├── health/     ✅
│   ├── enrollment/ ✅
│   ├── records/    ✅
│   └── parents/    ✅ (cuil obligatorio, placeId consistente)
│
├── teachers/                                        # BOUNDED CONTEXT: Profesores ✅ COMPLETO
│   ├── domain/
│   │   ├── model/Teacher.java                      # assignActivationToken(), activate(), isPendingActivation()
│   │   ├── valueobject/ TeacherId, EmploymentStatus, EmploymentType, TeacherSpecialization
│   │   ├── repository/TeacherRepository.java
│   │   └── exception/  TeacherNotFoundException, TeacherAlreadyExistsException,
│   │                   InvalidTeacherDataException
│   ├── application/
│   │   ├── dto/request/  CreateTeacherRequest, UpdateTeacherRequest
│   │   ├── dto/response/ TeacherResponse, TeacherSummaryResponse
│   │   ├── mapper/       TeacherApplicationMapper
│   │   └── usecases/     GetTeacherByIdUseCase, CreateTeacherUseCase (orquestador completo),
│   │                     UpdateTeacherUseCase, SearchTeachersUseCase
│   └── infrastructure/
│       ├── persistence/  TeacherEntity, TeacherJpaRepository,
│       │                 TeacherPersistenceMapper, TeacherRepositoryAdapter
│       ├── event/        TeacherAccountActivatedListener  ← reacciona a AccountActivatedEvent
│       └── web/          TeacherWebDto, TeacherWebMapper,
│                         TeacherController, TeacherExceptionHandler
│
├── grades/                                          # BOUNDED CONTEXT: Calificaciones ✅ COMPLETO
│   ├── domain/
│   │   ├── model/        Evaluation, PeriodGrade, FinalGrade
│   │   ├── valueobject/  EvaluationId, EvaluationTypeId, EvaluationStatus (movidos de academic/)
│   │   │                 FinalGradeId, PeriodGradeId, FinalGradeStatus
│   │   ├── repository/   EvaluationRepository, PeriodGradeRepository, FinalGradeRepository
│   │   └── exception/    GradeNotFoundException, GradeAlreadyValidatedException,
│   │                     InvalidGradeException, GradeAlreadyRecordedInRegistryException
│   ├── application/
│   │   ├── dto/request/  CreateEvaluationRequest, GradeEvaluationRequest,
│   │   │                 RecordExamGradeRequest
│   │   ├── dto/response/ EvaluationResponse, PeriodGradeResponse, FinalGradeResponse
│   │   ├── mapper/       GradesApplicationMapper
│   │   └── usecases/     CreateEvaluationUseCase, GradeEvaluationUseCase,
│   │                     ValidateEvaluationUseCase, CalculatePeriodGradeUseCase,
│   │                     RecordExamGradeUseCase, CalculateFinalGradeUseCase,
│   │                     RecordFinalGradeInRegistryUseCase
│   └── infrastructure/
│       ├── persistence/  EvaluationEntity, PeriodGradeEntity, FinalGradeEntity,
│       │                 EvaluationTypeEntity + JpaRepositories + Adapters + Mappers
│       ├── web/          GradesWebDto, GradesWebMapper,
│       │                 GradesController (7 endpoints), GradesExceptionHandler
│       └── seeder/       GradesDataSeeder (@Profile("dev"), @Order(10))
│
├── course/                                          # BOUNDED CONTEXT: Cursos ✅ COMPLETO
│   ├── domain/
│   │   ├── model/        CourseSubject, StudentCourseSubject
│   │   ├── valueobject/  CourseSubjectId, StudentCourseSubjectId, CourseStatus,
│   │   │                 SubjectEnrollmentStatus
│   │   ├── repository/   CourseSubjectRepository, StudentCourseSubjectRepository
│   │   └── exception/    CourseSubjectNotFoundException, StudentAlreadyEnrolledException, ...
│   ├── application/
│   │   ├── dto/          CreateCourseSubjectRequest, AssignTeacherRequest, EnrollStudentRequest
│   │   ├── mapper/       CourseApplicationMapper
│   │   └── usecases/     CreateCourseSubjectUseCase, AssignTeacherToCourseUseCase,
│   │                     EnrollStudentInCourseUseCase, GetCourseSubjectsByGradeLevelUseCase,
│   │                     GetStudentCoursesUseCase
│   └── infrastructure/
│       ├── persistence/  CourseSubjectEntity, StudentCourseSubjectEntity
│       │                 + JpaRepositories + Adapters + PersistenceMappers
│       ├── web/          CourseWebDto, CourseWebMapper, CourseController (5 endpoints),
│       │                 CourseExceptionHandler
│       └── seeder/       CourseDataSeeder (@Profile("dev"), @Order(6))
│
└── attendance/                                      # BOUNDED CONTEXT: Asistencia ✅ COMPLETO
    ├── domain/
    │   ├── model/        DailyAttendance, CourseAttendance, AttendanceSummary
    │   ├── valueobject/  DailyAttendanceId, CourseAttendanceId, AttendanceSummaryId
    │   │                 AttendanceStatus (PRESENT=0, ABSENT=1, JUSTIFIED=1, LATE=0.2, WITHDRAWN=0.2)
    │   ├── repository/   DailyAttendanceRepository, CourseAttendanceRepository,
    │   │                 AttendanceSummaryRepository
    │   └── exception/    AttendanceAlreadyRecordedException, AttendanceNotFoundException
    ├── application/
    │   ├── dto/          RecordDailyAttendanceRequest, RecordCourseAttendanceRequest,
    │   │                 JustifyAbsenceRequest, CorrectAttendanceRequest
    │   │                 + DailyAttendanceResponse, CourseAttendanceResponse, AttendanceSummaryResponse
    │   ├── mapper/       AttendanceApplicationMapper
    │   └── usecases/     RecordDailyAttendanceUseCase, RecordCourseAttendanceUseCase,
    │                     JustifyAbsenceUseCase, CorrectAttendanceUseCase,
    │                     GetAttendanceSummaryUseCase, GetAtRiskStudentsUseCase
    └── infrastructure/
        ├── persistence/  DailyAttendanceEntity, CourseAttendanceEntity, AttendanceSummaryEntity
        │                 + JpaRepositories + Adapters + PersistenceMappers (default methods)
        └── web/          AttendanceWebDto, AttendanceWebMapper,
                          AttendanceController (7 endpoints), AttendanceExceptionHandler
```

---

## 🗂️ Módulos Implementados

### ✅ Auth — Autenticación y Autorización

JWT con refresh tokens, rotación de tokens, blacklist, sesiones múltiples por dispositivo.
Flujo de activación de cuenta teacher via link en email (JWT CONFIRMATION de 48h) + eventos de dominio.

**Responsabilidad exclusiva de `auth/`:** identidad y sesión. La creación de entidades de dominio (teachers, students, parents) pertenece a cada BC.

**Use cases de `auth/`:**

| Use Case | Responsabilidad |
|----------|----------------|
| `CreateUserUseCase` | Factory puro de `User` — recibe dni, password, rol. No sabe qué tipo de usuario es. |
| `ActivateAccountUseCase` | Valida token, activa `User`, publica `AccountActivatedEvent` |
| `GenerateConfirmationTokenUseCase` | Genera JWT de activación — encapsula `JwtTokenProvider` |
| `LoginUseCase` | Autenticación por DNI + password |
| `ChangePasswordUseCase` | Cambio de password del usuario autenticado |
| `GetUserProfileUseCase` | Perfil del usuario autenticado |

**`SecurityContextHelper`** — clase estática en `auth/infra/web/`:
```java
SecurityContextHelper.extractUserId(userDetails) // todos los controllers lo llaman — nunca duplicar
```

**Endpoint de activación (público):**
```
POST /api/auth/activate-account
Body: { "token": "<jwt>", "newPassword": "MiNuevaPass123!" }
```

### ✅ Shared — Infraestructura de Eventos de Dominio

Puerto `DomainEventPublisher` en `shared/domain/event/` implementado por `SpringDomainEventPublisher`.

**Flujo de activación desacoplado:**
```
ActivateAccountUseCase
  → activa User
  → publica AccountActivatedEvent(userId, dni, roleName)
      └─ TeacherAccountActivatedListener @TransactionalEventListener(BEFORE_COMMIT)
           └─ activa Teacher — atómico con la transacción principal
```

Cuando se necesite activación para otros roles (parents, etc.) solo hay que agregar un nuevo listener — sin modificar `auth/`.

### ✅ Geography — Geografía Argentina

Jerarquía País → Provincia → Localidad.

### ✅ Academic — Estructura Académica

Años lectivos, orientaciones, cursos (1°A–7°B), materias, registro de calificaciones.
IPET 132 usa **2 períodos cuatrimestrales** por año.

### ✅ Email Service

Puerto `EmailService` en `shared/domain/service/` implementado por `JavaMailEmailService`.
SMTP configurado para **OCI Email Delivery** en prod y **Mailhog** en local.
Envío `@Async` — nunca bloquea transacciones. Fallos silenciosos (log + catch).
`sendTeacherInvitation()` incluye link de activación con JWT de 48h.

```bash
# Levantar Mailhog para desarrollo local
docker run -p 1025:1025 -p 8025:8025 mailhog/mailhog
# UI: http://localhost:8025
```

### ✅ Students — COMPLETO (5 agregados)

#### Students Personal

| Método | Path | Rol |
|--------|------|-----|
| POST | `/api/admin/students` | ADMIN |
| GET | `/api/admin/students/{id}` | ADMIN, STAFF |
| GET | `/api/admin/students/dni/{dni}` | ADMIN, STAFF |
| GET | `/api/admin/students` | ADMIN, STAFF |
| PATCH | `/api/admin/students/{id}` | ADMIN, STAFF |

**CreateStudentUseCase — 15 pasos atómicos:**
```
1-2.  Validar unicidad DNI y CUIL
3.    Obtener AcademicYear activo
4.    Validar GradeLevel activo
5.    Asignar folio → FolioAssignmentService.assignNextFolio()
6.    Generar password → {DNI}Ipet132!
7.    Crear User (rol STUDENT) → CreateUserUseCase.execute(CreateUserRequest.active(...))
8.    Crear StudentPersonalData
9.    Crear StudentHealthRecord
10.   Obtener QualificationRegistry activo
11.   Crear StudentRecord (recordNumber = DNI)
12.   Buscar Parent por DNI → si no existe: CreateUserUseCase.execute(CreateUserRequest.active(...)) + crear Parent + enviar email
13.   Crear StudentParent
14.   Crear StudentEnrollment
15.   Commit → retornar StudentResponse
```

#### Students Health

| Método | Path | Rol |
|--------|------|-----|
| GET | `/api/admin/students/{studentId}/health` | ADMIN, STAFF |
| PATCH | `/api/admin/students/{studentId}/health` | ADMIN, STAFF |

#### Students Enrollment

| Método | Path | Rol |
|--------|------|-----|
| GET | `/api/admin/students/{studentId}/enrollments` | ADMIN, STAFF |
| GET | `/api/admin/students/{studentId}/enrollments/{academicYearId}` | ADMIN, STAFF |
| PATCH | `/api/admin/students/{studentId}/enrollments/{enrollmentId}` | ADMIN |

#### Students Records (Legajo)

| Método | Path | Rol |
|--------|------|-----|
| GET | `/api/admin/students/{studentId}/record` | ADMIN, STAFF |
| POST | `/api/admin/students/{studentId}/record/documents` | ADMIN, STAFF |
| PATCH | `/api/admin/students/{studentId}/record/documents/{documentId}` | ADMIN, STAFF |
| PATCH | `/api/admin/students/{studentId}/record/status` | ADMIN |

#### Students Parents

| Método | Path | Rol |
|--------|------|-----|
| POST | `/api/admin/parents` | ADMIN, STAFF |
| PATCH | `/api/admin/parents/{parentId}` | ADMIN, STAFF |
| GET | `/api/admin/students/{studentId}/parents` | ADMIN, STAFF |
| POST | `/api/admin/students/{studentId}/parents` | ADMIN, STAFF |

### ✅ Teachers — COMPLETO

| Método | Path | Rol | Descripción |
|--------|------|-----|-------------|
| POST | `/api/admin/teachers` | ADMIN | Crear profesor + enviar email con link activación |
| GET | `/api/admin/teachers/{teacherId}` | ADMIN, STAFF | Obtener por ID |
| GET | `/api/admin/teachers` | ADMIN, STAFF | Buscar (dni / lastName / todos) |
| PATCH | `/api/admin/teachers/{teacherId}` | ADMIN, STAFF | Actualizar (PATCH semántico) |

**Flujo de creación y activación:**
1. ADMIN crea el profesor → `teachers/CreateTeacherUseCase` orquesta todo
2. Genera password temporal segura (SecureRandom, 12 chars)
3. Llama `auth/CreateUserUseCase` con `CreateUserRequest.inactive(dni, password, "ROLE_TEACHER")`
4. Llama `auth/GenerateConfirmationTokenUseCase` → JWT 48h
5. Persiste `Teacher` con `assignActivationToken(token)`
6. Envía email de invitación con link (async — falla silenciosamente)
7. Profesor activa cuenta → `POST /api/auth/activate-account`
8. `ActivateAccountUseCase` activa `User` + publica `AccountActivatedEvent`
9. `TeacherAccountActivatedListener` reacciona → activa `Teacher` (misma transacción)

**Estados de empleo:** `ACTIVE` → `INACTIVE` / `RETIRED` (via PATCH)

### ✅ Grades — COMPLETO

| Método | Path | Rol | Descripción |
|--------|------|-----|-------------|
| POST | `/api/grades/evaluations` | TEACHER | Crear evaluación |
| PATCH | `/api/grades/evaluations/{id}/grade` | TEACHER | Cargar nota |
| PATCH | `/api/grades/evaluations/{id}/validate` | ADMIN, STAFF | Validar evaluación |
| POST | `/api/grades/period-grades/calculate` | ADMIN, STAFF | Calcular nota de período |
| POST | `/api/grades/final-grades/exam` | ADMIN, STAFF | Asentar nota de examen/coloquio |
| POST | `/api/grades/final-grades/calculate` | ADMIN, STAFF | Calcular nota final |
| PATCH | `/api/grades/final-grades/{id}/registry` | ADMIN | Registrar en libro matriz |

**Notas clave:**
- Nota mínima de aprobación: **7** (`MIN_PASSING_GRADE` constante en cada modelo)
- El folio del alumno se obtiene de `StudentRecord` — fue asignado en `CreateStudentUseCase`
- `EvaluationId`, `EvaluationTypeId`, `EvaluationStatus` viven en `grades/domain/valueobject/`
- Seeder puebla 5 tipos de evaluación con UUIDs fijos para perfil `dev`

### ✅ Course — COMPLETO

| Método | Path | Rol | Descripción |
|--------|------|-----|-------------|
| POST | `/api/courses/course-subjects` | ADMIN, STAFF | Crear asignación materia-curso |
| GET | `/api/courses/course-subjects` | ADMIN, STAFF, TEACHER | Listar por curso y año |
| PATCH | `/api/courses/course-subjects/{id}/teacher` | ADMIN, STAFF | Asignar docente |
| POST | `/api/courses/enrollments` | ADMIN, STAFF | Inscribir alumno a materia |
| GET | `/api/courses/enrollments/{enrollmentId}/courses` | ADMIN, STAFF, TEACHER | Materias del alumno |

### ✅ Attendance — COMPLETO

Control de asistencia diaria por preceptor y por materia por docente.
Cálculo automático de porcentaje de asistencia y detección de alumnos en riesgo de quedar libres.

| Método | Path | Rol | Descripción |
|--------|------|-----|-------------|
| POST | `/api/attendance/daily` | ADMIN, STAFF | Registrar asistencia diaria del curso |
| PATCH | `/api/attendance/daily/{id}/justify` | ADMIN, STAFF | Justificar ausencia (ABSENT→JUSTIFIED) |
| PATCH | `/api/attendance/daily/{id}` | ADMIN, STAFF | Corregir registro diario |
| POST | `/api/attendance/course` | ADMIN, STAFF, TEACHER | Registrar asistencia por materia |
| PATCH | `/api/attendance/course/{id}` | ADMIN, STAFF, TEACHER | Corregir registro por materia |
| GET | `/api/attendance/course/summary` | ADMIN, STAFF, TEACHER | Resumen por alumno/materia/período |
| GET | `/api/attendance/course/at-risk` | ADMIN, STAFF | Alumnos en riesgo de quedar libres |

**Reglas IPET 132:**
- Mínimo **85%** de asistencia para aprobar
- `ABSENT` = 1.0 falta | `JUSTIFIED` = 1.0 falta (la justificación registra el motivo, no exime)
- `LATE` = 0.2 faltas (5 tardanzas = 1 falta) | `WITHDRAWN` = 0.2 faltas
- Libre si `weightedAbsences / totalClasses > 0.15` (condición estricta)
- `AttendanceSummary.recalculate()` se ejecuta en cada carga/corrección — consistencia garantizada

---

## 🗄️ Migraciones Flyway

| Migración | Descripción |
|-----------|-------------|
| `V1` | Tabla `users` |
| `V2` | Tabla `blacklisted_tokens` |
| `V3` | Admin por defecto (solo dev) |
| `V4` | Tabla `refresh_tokens` |
| `V5` | `countries`, `provinces`, `places` |
| `V6` | `academic_years`, `orientations`, `grade_levels`, `subjects`, `qualification_registries` |
| `V7` | `study_plans`, `evaluation_periods` |
| `V10` | `student_personal_data`, `student_health_records` |
| `V11` | `document_types`, `student_records`, `record_documents` |
| `V12` | `parents` (con `cuil`), `student_parents` |
| `V13` | `teachers` |
| `V14` | `withdrawal_reasons`, `student_enrollments` |
| `V15` | `courses`, `course_subjects`, `student_course_subjects` |
| `V17` | `evaluation_types`, `evaluations`, `period_grades`, `final_grades` |
| `V19` | `countries`, `provinces` — datos Argentina |
| `V20` | `places` — localidades Argentina |
| `V21` | `attendance_daily_records`, `attendance_course_records`, `attendance_period_summaries` |

---

## ⚙️ Configuración

```bash
# 1. Crear base de datos
mysql -u root -p
CREATE DATABASE school132_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 2. Configurar application.yml con credenciales de BD y OCI SMTP

# 3. Compilar
mvn clean install

# 4. Ejecutar en perfil dev
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 5. Ejecutar en perfil local (con Mailhog)
docker run -p 1025:1025 -p 8025:8025 mailhog/mailhog
mvn spring-boot:run -Dspring-boot.run.profiles=local

# Disponible en: http://localhost:8080
# Swagger UI:    http://localhost:8080/swagger-ui.html
# Mailhog UI:    http://localhost:8025
```

### Configuración OCI Email Delivery

```yaml
spring:
  mail:
    host: smtp.email.${OCI_EMAIL_REGION:us-ashburn-1}.oci.oraclecloud.com
    port: 587
    username: ${OCI_SMTP_USERNAME}
    password: ${OCI_SMTP_PASSWORD}
    properties:
      mail.smtp.auth: true
      mail.smtp.starttls.enable: true

app:
  frontend:
    url: http://localhost:3000          # en prod: URL del frontend
  security:
    jwt:
      confirmation-token-expiration: 172800   # 48 horas en segundos
  school:
    name: "IPET 132"
```

---

## 🔑 Credenciales de Prueba

| Rol | DNI | Password |
|-----|-----|----------|
| ADMIN | `00000001` | `Admin123!` |
| TEACHER | `12345678` | `Teacher123!` |
| STUDENT (con email) | `11223344` | `11223344Ipet132!` |
| STUDENT (sin email) | `87654321` | `87654321Ipet132!` |

---

## 🔒 Seguridad

- **JWT access token** — corta duración (configurable)
- **Refresh token** — larga duración, almacenado hasheado en BD, rotación en cada uso
- **Confirmation token** — JWT de 48h para activación de cuenta teacher
- **Blacklist** — access tokens revocados hasta expiración
- **Roles** — `ADMIN`, `TEACHER`, `STUDENT`, `PARENT`, `STAFF`
- **Password inicial estudiante** — `{DNI}Ipet132!`
- **Password teacher/padre** — aleatorio seguro (enviado por email)
- **Activación teacher** — link en email con JWT CONFIRMATION (48h) + `AccountActivatedEvent` desacoplado
- **ProblemDetail** — respuestas de error estandarizadas (RFC 9457)

---

## 🧪 Tests

Tests unitarios implementados con JUnit 5 + Mockito + AssertJ.

**Ruta:** `src/test/java/org/school/management/`

| Test | Módulo | Tests |
|------|--------|-------|
| `GetTeacherByIdUseCaseTest` | teachers | 3 |
| `CreateTeacherUseCaseTest` | teachers | 4 |
| `UpdateTeacherUseCaseTest` | teachers | 4 |
| `CreateParentUseCaseTest` | parents | 5 |
| `LinkParentToStudentUseCaseTest` | parents | 6 |
| `CreateEvaluationUseCaseTest` | grades | 3 |
| `GradeEvaluationUseCaseTest` | grades | 4 |
| `ValidateEvaluationUseCaseTest` | grades | 4 |
| `CalculatePeriodGradeUseCaseTest` | grades | 3 |
| `RecordFinalGradeInRegistryUseCaseTest` | grades | 5 |
| `CreateCourseSubjectUseCaseTest` | course | 4 |
| `EnrollStudentInCourseUseCaseTest` | course | 5 |
| `AttendanceSummaryTest` | attendance | 9 |
| `RecordDailyAttendanceUseCaseTest` | attendance | 4 |
| `RecordCourseAttendanceUseCaseTest` | attendance | 5 |
| `JustifyAbsenceUseCaseTest` | attendance | 5 |
| `CorrectAttendanceUseCaseTest` | attendance | 4 |
| `GetAtRiskStudentsUseCaseTest` | attendance | 3 |
| **Total** | | **80** |

```bash
mvn test -Dgroups="unit"      # ejecutar solo unit tests
mvn test jacoco:report         # reporte de cobertura
```

---

## 📊 Estado del Proyecto

### ✅ Implementado

- Auth, Geography, Academic completos + **refactor de fronteras del BC `auth/`**
- `shared/email/` — EmailService + JavaMailEmailService (OCI SMTP) + AsyncConfig
- `shared/event/` — DomainEvent, AccountActivatedEvent, DomainEventPublisher + SpringDomainEventPublisher
- **`students/` — COMPLETO** — 5 agregados de punta a punta
- **`teachers/` — COMPLETO** — domain + application + infrastructure + flujo activación via eventos
- **`parents/` — COMPLETO** — cuil obligatorio, placeId consistente
- **`grades/` — COMPLETO** — domain + application + infrastructure + 7 endpoints + seeder + 19 tests
- **`course/` — COMPLETO** — domain + application + infrastructure + 5 endpoints + seeder + 9 tests
- **`attendance/` — COMPLETO** — domain + application + infrastructure + 7 endpoints + V21 + 30 tests
- **Flujo activación teacher** — JWT 48h, email con link, `AccountActivatedEvent`, `TeacherAccountActivatedListener`
- **Seeders dev** — Academic, Course, Grades, Teacher (3 docentes), StudentAndParent (4 alumnos + 4 padres)
- **80 tests unitarios** — teachers (11), parents (11), grades (19), course (9), attendance (30)
- Flyway V1–V7, V10–V15, V17, V19, V20, V21

### ⏳ Pendiente

- [ ] `ActivateAccountUseCaseTest` — 5 casos (token inválido, user no encontrado, happy path, verifica evento publicado)
- [ ] `TeacherAccountActivatedListenerTest` — 3 casos (rol teacher, otro rol ignorado, teacher no encontrado)
- [ ] Tests unitarios para `CreateStudentUseCase` (15 pasos)
- [ ] Rate limiting, auditoría, métricas

---

## 📝 Decisiones Arquitectónicas Clave

| Decisión | Razón |
|----------|-------|
| **`auth/` solo gestiona identidad y sesión** | Cada BC es responsable de crear y activar sus propias entidades — `auth/` no conoce teachers ni students |
| **`CreateUserUseCase` en `auth/` — factory puro** | Recibe rol, dni, password — agnóstico del tipo de usuario — llamado por teachers/, students/, parents/ |
| **`CreateUserRequest.active() / inactive()`** | Factory methods semánticos — students/parents activos, teachers inactivos hasta activar cuenta |
| **Activación via eventos de dominio** | `ActivateAccountUseCase` publica `AccountActivatedEvent` — cada BC reacciona con su listener independientemente |
| **`@TransactionalEventListener(BEFORE_COMMIT)`** | Atomicidad garantizada — si el listener falla, toda la transacción se revierte |
| **`AccountActivatedEvent.roleName` como String** | Evita dependencia de `auth/RoleName` en `shared/` — los eventos son agnósticos del BC emisor |
| **`DomainEventPublisher` como puerto en `shared/domain`** | Los use cases no importan Spring — el dominio permanece puro |
| **`SecurityContextHelper` estático** | Centraliza el cast `UserDetails → User` — nunca duplicar `extractUserId()` en cada controller |
| **`AdminController` y `UserController` eliminados** | Colisionaban con controllers de BCs — deuda técnica resuelta; cada BC expone sus propios endpoints |
| **DTOs de create student/teacher eliminados de `auth/`** | `auth/` no crea entidades de dominio — cada BC tiene sus propios DTOs de request/response |
| **`teachers/CreateTeacherUseCase` es el orquestador** | Genera password, llama `CreateUserUseCase`, genera token, persiste `Teacher`, envía email |
| **`grades/` como BC separado** | Actores distintos (TEACHER vs ADMIN), frecuencia de cambio diferente |
| **EvaluationId/TypeId/Status en `grades/`** | Pertenecen exclusivamente al dominio de calificaciones — movidos desde `academic/` |
| **MIN_PASSING_GRADE = 7 como constante** | Regla de negocio IPET 132 — nunca hardcodear el umbral |
| **`attendance/` como BC separado** | Actores distintos (preceptor vs profesor); volumen alto; razón de cambio diferente a course/ |
| **AttendanceStatus con peso de falta** | Encapsula regla de negocio en el enum — ABSENT=1.0, LATE=0.2, WITHDRAWN=0.2 |
| **MIN_ATTENDANCE_PERCENTAGE = 85** | Regla IPET 132 — constante en AttendanceSummary, nunca hardcodear |
| **JUSTIFIED descuenta igual que ABSENT** | Regla IPET 132 — la justificación registra el motivo pero no exime la falta |
| **atRisk = weightedAbsences/total > 0.15** | Condición estricta (>) — exactamente 15% NO es libre |
| **recalculate() en cada carga/corrección** | Consistencia garantizada en la transacción — no on-demand |
| **CourseStatus y SubjectEnrollmentStatus en `course/`** | Mismo patrón que EvaluationStatus → grades/; pertenecen al BC que los usa |
| **StudentCourseSubject sin attendedClasses** | Campo no existe en BD — solo total_classes en course_subjects |
| **Seeders resuelven place_id en runtime** | Geography usa UUIDs dinámicos — searchByName() + filter exact match |
| **UuidBinaryConverter en shared/** | Un solo converter para todos los BCs |
| **ProblemDetail para errores** | RFC 9457, nativo en Spring 6 |
| **RecordNumber = DNI** | Legajo único y permanente — compatible con ministerio |

---

**Última actualización:** Marzo 2026
**Versión:** 7.0.0
**Estado:** En desarrollo activo — refactor `auth/` ✅ | eventos de dominio ✅ | tests pendientes ⏳