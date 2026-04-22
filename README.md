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
- ✅ Catálogo de tipos de documento — **COMPLETO** (CRUD + toggle is_active, 6 endpoints, 12 tipos predefinidos)
- ✅ Módulo de docentes — **COMPLETO** (con flujo de activación de cuenta por email)
- ✅ Servicio de email — **COMPLETO** (OCI SMTP, link de activación, async)
- ✅ Módulo de calificaciones — **COMPLETO** (evaluaciones, notas de período, nota final, libro matriz)
- ✅ Módulo de cursos — **COMPLETO** (asignación profesor-materia-curso, inscripción de alumnos)
- ✅ Módulo de asistencia — **COMPLETO** (diaria por preceptor, por materia por docente, resúmenes y alumnos en riesgo)
- ✅ Infraestructura de eventos de dominio — **COMPLETO** (`DomainEvent`, `AccountActivatedEvent`, `DomainEventPublisher`)
- ✅ Rate limiting — **COMPLETO** (Bucket4j in-memory, por IP, endpoints de auth protegidos)
- ✅ Almacenamiento de archivos — **COMPLETO** (OCI Object Storage, documentos del legajo + material didáctico)
- ✅ Material didáctico de profesores — **COMPLETO** (upload a OCI, control de visibilidad, ownership check)
- ✅ Gestión de recursos didácticos — **COMPLETO** (catálogo, unidades físicas, sistema de reservas)
- ✅ Seeders de desarrollo — **COMPLETO** (cadena completa con 4 escenarios de legajo para pruebas)

### 🎯 Características Principales

- **DNI como username** — sistema adaptado a la realidad argentina (correlativo, sin dígito verificador)
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
- **Rate limiting por IP** — Bucket4j in-memory, 5 intentos/min en login (prod)
- **Almacenamiento OCI** — archivos en OCI Object Storage, URL pública + presigned URLs
- **Ownership check en materiales** — TEACHER solo puede editar/eliminar su propio material
- **Catálogo de tipos de documento** — CRUD con toggle `is_active`, 12 tipos predefinidos por categoría (PERSONAL, ACADEMIC, MEDICAL, LEGAL)
- **Gestión de recursos didácticos** — Catálogo de proyectores, netbooks, televisores, salas multimedia, etc.
- **Unidades físicas** con estado y condición
- **Sistema completo de reservas** con validación de disponibilidad en tiempo real y asignación automática
- **Seeders completos** — cadena `@Order(3→5→6→7→8→9→10)` con 4 escenarios de legajo para pruebas E2E

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
shared/              → Shared Kernel (Dni, Cuil, Email, PhoneNumber, Address, Gender,
                                      IDs geográficos, UuidBinaryConverter, EmailService,
                                      DomainEvent, AccountActivatedEvent, DomainEventPublisher)
auth/                → Autenticación y autorización ✅
geography/           → Lugares geográficos (País, Provincia, Localidad) ✅
academic/            → Estructura académica (Años, Cursos, Materias) ✅
students/            → Gestión de estudiantes ✅ COMPLETO
teachers/            → Gestión de profesores ✅ COMPLETO
grades/              → Calificaciones ✅ COMPLETO
course/              → Asignación profesor-materia-curso ✅ COMPLETO
attendance/          → Asistencia diaria y por materia ✅ COMPLETO
storage/             → Almacenamiento de archivos en OCI Object Storage ✅ COMPLETO
teaching-materials/  → Material didáctico de profesores ✅ COMPLETO
resources/           → Gestión de recursos didácticos institucionales y sistema de reservas. ✅ COMPLETO
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
| Bucket4j | 8.10.1 | Rate limiting in-memory por IP |
| OCI Java SDK | 3.43.0 | OCI Object Storage (subida de archivos) |
| JUnit 5 + Mockito | Boot managed | Testing |

---

## 📁 Estructura del Proyecto

```
src/main/java/org/school/management/
│
├── shared/                                          # Shared Kernel
│   ├── person/domain/valueobject/
│   │   ├── Dni.java, Cuil.java, FullName.java, Gender.java
│   │   ├── Nationality.java, PhoneNumber.java, Email.java, Address.java
│   ├── geography/domain/valueobject/
│   │   └── CountryId.java, ProvinceId.java, PlaceId.java
│   ├── domain/
│   │   ├── exception/DomainException.java
│   │   ├── service/EmailService.java
│   │   └── event/
│   │       ├── DomainEvent.java, AccountActivatedEvent.java, DomainEventPublisher.java
│   └── infrastructure/
│       ├── persistence/converter/UuidBinaryConverter.java
│       ├── event/SpringDomainEventPublisher.java
│       ├── email/JavaMailEmailService.java
│       └── config/AsyncConfig.java
│
├── auth/                                            # BOUNDED CONTEXT: Autenticación ✅
│   ├── domain/exception/UnauthorizedException.java
│   ├── application/usecases/
│   │   ├── CreateUserUseCase.java
│   │   ├── ActivateAccountUseCase.java
│   │   ├── GenerateConfirmationTokenUseCase.java
│   │   ├── LoginUseCase.java, ChangePasswordUseCase.java, GetUserProfileUseCase.java
│   └── infrastructure/
│       ├── web/SecurityContextHelper.java
│       ├── persistence/
│       └── security/
│           ├── SecurityConfig.java, JwtTokenProvider.java, JwtAuthenticationFilter.java
│           └── ratelimit/RateLimitFilter.java, RateLimitProperties.java
│
├── geography/                                       # BOUNDED CONTEXT: Geografía ✅
├── academic/                                        # BOUNDED CONTEXT: Académico ✅
│
├── students/                                        # BOUNDED CONTEXT: Estudiantes ✅ COMPLETO
│   ├── personal/   ✅ — CreateStudentUseCase (15 pasos)
│   ├── health/     ✅
│   ├── enrollment/ ✅
│   ├── records/    ✅ — legajo + UploadRecordDocumentUseCase + RecordDocumentRepository
│   │               ✅ — DocumentType CRUD (catálogo + toggle is_active, 6 endpoints)
│   └── parents/    ✅
│
├── teachers/                                        # BOUNDED CONTEXT: Profesores ✅ COMPLETO
├── grades/                                          # BOUNDED CONTEXT: Calificaciones ✅ COMPLETO
├── course/                                          # BOUNDED CONTEXT: Cursos ✅ COMPLETO
├── attendance/                                      # BOUNDED CONTEXT: Asistencia ✅ COMPLETO
│
├── storage/                                         # BOUNDED CONTEXT: Almacenamiento ✅ COMPLETO
│   ├── domain/
│   │   ├── service/StorageService.java
│   │   └── model/UploadedFile.java
│   └── infrastructure/
│       ├── config/OciStorageProperties.java
│       └── oci/OciObjectStorageService.java
│
└── teaching-materials/                              # BOUNDED CONTEXT: Material Didáctico ✅ COMPLETO
    ├── domain/
    │   ├── model/TeachingMaterial.java
    │   ├── valueobject/TeachingMaterialId.java, MaterialType.java
    │   ├── repository/TeachingMaterialRepository.java
    │   └── exception/TeachingMaterialNotFoundException.java
    │               TeachingMaterialAccessDeniedException.java
    ├── application/
    │   ├── dto/request/  UploadMaterialRequest.java, UpdateMaterialRequest.java
    │   ├── dto/response/ TeachingMaterialResponse.java
    │   ├── mapper/       TeachingMaterialApplicationMapper.java
    │   └── usecases/     UploadTeachingMaterialUseCase.java
    │                     GetMaterialsByCourseUseCase.java
    │                     GetMaterialsForStudentUseCase.java
    │                     UpdateMaterialUseCase.java
    │                     DeleteMaterialUseCase.java
    └── infrastructure/
        ├── persistence/  TeachingMaterialEntity, TeachingMaterialJpaRepository,
        │                 TeachingMaterialPersistenceMapper, TeachingMaterialRepositoryAdapter
        └── web/          TeachingMaterialWebDto, TeachingMaterialWebMapper,
                          TeachingMaterialController (5 endpoints),
                          TeachingMaterialExceptionHandler
```

---

## 🗂️ Módulos Implementados

### ✅ Auth — Autenticación y Autorización

JWT con refresh tokens, rotación de tokens, blacklist, sesiones múltiples por dispositivo.
Flujo de activación de cuenta teacher via link en email (JWT CONFIRMATION de 48h) + eventos de dominio.

### ✅ Rate Limiting

Protección de endpoints de autenticación contra fuerza bruta y abuso, implementado con **Bucket4j in-memory** por IP.

| Endpoint | Límite (prod) | Límite (dev) |
|----------|--------------|--------------|
| `POST /api/auth/login` | 5 req/min | 100 req/min |
| `POST /api/auth/activate-account` | 3 req/min | 100 req/min |
| `POST /api/auth/refresh-token` | 10 req/min | 100 req/min |

### ✅ Almacenamiento — OCI Object Storage

Puerto `StorageService` en `storage/domain/service/` implementado por `OciObjectStorageService`.

- Sube archivos a OCI Object Storage con estructura de carpetas organizada
- Genera URLs públicas y presigned URLs (acceso temporal seguro)
- Tipos permitidos: `application/pdf`, `image/jpeg`, `image/png` — máx 10 MB
- Estructura en bucket:
    - `records/{studentId}/{uuid}-{fileName}` — documentos del legajo
    - `materials/{teacherId}/{courseSubjectId}/{uuid}-{fileName}` — material didáctico

### ✅ Students — COMPLETO (5 agregados)

#### Students Personal

| Método | Path | Rol |
|--------|------|-----|
| POST | `/api/admin/students` | ADMIN |
| GET | `/api/admin/students/{id}` | ADMIN, STAFF |
| GET | `/api/admin/students/dni/{dni}` | ADMIN, STAFF |
| GET | `/api/admin/students` | ADMIN, STAFF |
| PATCH | `/api/admin/students/{id}` | ADMIN, STAFF |

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
| POST | `/api/admin/students/{studentId}/record/{recordId}/upload` | ADMIN, STAFF |
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

### ✅ Course — COMPLETO

| Método | Path | Rol | Descripción |
|--------|------|-----|-------------|
| POST | `/api/courses/course-subjects` | ADMIN, STAFF | Crear asignación materia-curso |
| GET | `/api/courses/course-subjects` | ADMIN, STAFF, TEACHER | Listar por curso y año |
| PATCH | `/api/courses/course-subjects/{id}/teacher` | ADMIN, STAFF | Asignar docente |
| POST | `/api/courses/enrollments` | ADMIN, STAFF | Inscribir alumno a materia |
| GET | `/api/courses/enrollments/{enrollmentId}/courses` | ADMIN, STAFF, TEACHER | Materias del alumno |

### ✅ Attendance — COMPLETO

| Método | Path | Rol | Descripción |
|--------|------|-----|-------------|
| POST | `/api/attendance/daily` | ADMIN, STAFF | Registrar asistencia diaria del curso |
| PATCH | `/api/attendance/daily/{id}/justify` | ADMIN, STAFF | Justificar ausencia |
| PATCH | `/api/attendance/daily/{id}` | ADMIN, STAFF | Corregir registro diario |
| POST | `/api/attendance/course` | ADMIN, STAFF, TEACHER | Registrar asistencia por materia |
| PATCH | `/api/attendance/course/{id}` | ADMIN, STAFF, TEACHER | Corregir registro por materia |
| GET | `/api/attendance/course/summary` | ADMIN, STAFF, TEACHER | Resumen por alumno/materia/período |
| GET | `/api/attendance/course/at-risk` | ADMIN, STAFF | Alumnos en riesgo de quedar libres |

### ✅ Teaching Materials — COMPLETO

Material didáctico subido por profesores, asociado a cursos específicos, con control de visibilidad para alumnos.

| Método | Path | Rol | Descripción |
|--------|------|-----|-------------|
| POST | `/api/materials` | TEACHER | Subir material (multipart/form-data) |
| GET | `/api/materials/course/{courseSubjectId}` | TEACHER, ADMIN, STAFF | Listar todos por curso |
| GET | `/api/materials/my-courses` | STUDENT | Ver material visible de sus cursos |
| PATCH | `/api/materials/{materialId}` | TEACHER, ADMIN, STAFF | Actualizar metadata/visibilidad |
| DELETE | `/api/materials/{materialId}` | TEACHER, ADMIN | Eliminar (OCI + BD) |

**Decisiones de diseño:**
- TEACHER solo puede editar/eliminar su propio material; ADMIN puede hacerlo con cualquiera
- `GET /my-courses` recibe `courseSubjectIds` como query params (obtenidos previamente vía `GET /api/courses/enrollments/{enrollmentId}/courses`)
- Sin FK a `teachers` en BD — evita acoplamiento entre BCs a nivel de esquema
- Tipos: `APUNTE`, `EJERCICIO`, `EXAMEN`, `GUIA`, `VIDEO`, `OTRO`

### ✅ Resources — **COMPLETO** (Nuevo)

Gestión de recursos didácticos institucionales y sistema de reservas.

**Funcionalidades principales:**
- Catálogo de recursos (`PROJECTOR`, `LAPTOP`, `MULTIMEDIA_ROOM`, `COMPUTER_LAB`, etc.)
- Unidades físicas individuales con código único, número de serie y estado
- Sistema de reservas con validación de disponibilidad horaria
- Asignación automática de unidades físicas disponibles
- Estados de reserva: `CONFIRMED`, `IN_USE`, `RETURNED`, `CANCELLED`
- Control de devolución y cancelación con liberación de unidades

**Endpoints principales:**

| Método | Path | Rol | Descripción |
|--------|------|-----|-------------|
| POST | `/api/resources` | ADMIN, STAFF | Crear recurso en catálogo |
| GET | `/api/resources` | Todos | Listar recursos (con filtros) |
| GET | `/api/resources/{resourceId}` | Todos | Obtener detalle |
| PATCH | `/api/resources/{resourceId}` | ADMIN, STAFF | Actualizar recurso |
| POST | `/api/resources/{resourceId}/units` | ADMIN, STAFF | Crear unidad física |
| PATCH | `/api/resources/units/{unitId}` | ADMIN, STAFF | Actualizar estado de unidad |
| POST | `/api/resources/reservations` | TEACHER, ADMIN, STAFF | Crear reserva |
| GET | `/api/resources/reservations/my` | TEACHER, ADMIN, STAFF | Ver mis reservas |
| GET | `/api/resources/reservations/availability` | Todos | Consultar disponibilidad en rango horario |
| PATCH | `/api/resources/reservations/{id}/start` | ADMIN, STAFF | Marcar como en uso (retiro) |
| PATCH | `/api/resources/reservations/{id}/return` | ADMIN, STAFF | Registrar devolución |
| PATCH | `/api/resources/reservations/{id}/cancel` | TEACHER, ADMIN, STAFF | Cancelar reserva |

---

## 🗄️ Migraciones Flyway


| Versión | Contenido                                                                                                                                           |
|---------|-----------------------------------------------------------------------------------------------------------------------------------------------------|
| V1      | Tabla `roles`                                                                                                                                       |
| V2      | Tabla `users`                                                                                                                                       |
| V3      | Tabla `blacklisted_tokens`                                                                                                                          |
| V4      | Tabla `refresh_tokens`                                                                                                                              |
| v5      | `password_resets`                                                                                                                                   |
| v6      | `recovery_codes`                                                                                                                                    |
| V7      | `recovery codes`                                                                                                                                    |
| V8      | `countries`, `provinces`, `places`                                                                                                                  |
| V9      | `academic_years`, `orientations`, `grade_levels`, `subjects`, `qualification_registries`, `study_plans`, `evaluation_periods`, extensiones academic |
| V10     | `student_personal_data`, `student_health_records`                                                                                                   |
| V11     | `document_types`, `student_records`, `record_documents`                                                                                             |
| V12     | `parents`, `student_parents` (incluye `cuil` en `parents`)                                                                                          |
| V13     | `teachers`                                                                                                                                          |
| V14     | `withdrawal_reasons`, `student_enrollments`                                                                                                         |
| V15     | `courses`, `course_subjects`, `student_course_subjects`                                                                                             |
| V16     | `evaluation_types`, `evaluations`, `period_grades`, `final_grades`                                                                                  |
| V17     | `countries`, `provinces` — datos Argentina (seed SQL)                                                                                               |
| V18     | `places` — localidades Argentina (seed SQL)                                                                                                         |
| V19     | `attendance_daily_records`, `attendance_course_records`, `attendance_period_summaries`                                                              |
| V20     | `teaching_materials` ✅                                                                                                                              |
| V21     | `resources`, `resource_units`, `reservations`, `reservation_units`                                                                                                     |

**Próxima migración disponible: V22**

---

## ⚙️ Configuración

```bash
# 1. Crear base de datos
mysql -u root -p
CREATE DATABASE school132_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 2. Configurar .env con credenciales

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

### Variables de Entorno

```env
# Base de datos
DB_HOST=localhost
DB_PORT=3306
DB_NAME=school132_db
DB_USER=root
DB_PASSWORD=tu_password

# JWT
JWT_SECRET_KEY=tu_clave_secreta_de_al_menos_32_caracteres

# OCI Email Delivery
OCI_EMAIL_REGION=sa-saopaulo-1
OCI_SMTP_USERNAME=tu_usuario_smtp_oci
OCI_SMTP_PASSWORD=tu_token_oci

# OCI Object Storage
OCI_TENANCY_OCID=ocid1.tenancy.oc1..xxx
OCI_USER_OCID=ocid1.user.oc1..xxx
OCI_FINGERPRINT=aa:bb:cc:dd:...
OCI_PRIVATE_KEY_PATH=/ruta/al/oci_api_key.pem
OCI_REGION=sa-saopaulo-1
OCI_NAMESPACE=tu_namespace
OCI_BUCKET_NAME=ipet132-documents
```

---

## 🔑 Credenciales de Prueba (perfil `dev`)

| Rol | DNI        | Password |
|-----|------------|----------|
| ADMIN | `10000001` | `Admin123!` |
| TEACHER | `12345678` | `Teacher123!` (Juan García — Matemática) |
| TEACHER | `23456789` | `Teacher123!` (María López — Física) |
| TEACHER | `34567890` | `Teacher123!` (Carlos Fernández — Electrotecnia) |
| STUDENT (con email) | `11223344` | `11223344Ipet132!` (Lucas Romero — 1°A) |
| STUDENT (sin email) | `87654321` | `87654321Ipet132!` (Sofía Torres — 1°A) |
| STUDENT | `44556677` | `44556677Ipet132!` (Martín Díaz — 4°A Electricista) |
| STUDENT | `55667788` | `55667788Ipet132!` (Ana Gómez — 4°C Electromecánico) |
| PARENT | `98765432` | `Parent123!` (Roberto Romero) |

---

## 🔒 Seguridad

- **JWT access token** — corta duración (configurable)
- **Refresh token** — larga duración, almacenado hasheado en BD, rotación en cada uso
- **Confirmation token** — JWT de 48h para activación de cuenta teacher
- **Blacklist** — access tokens revocados hasta expiración
- **Rate limiting** — Bucket4j in-memory por IP en endpoints de auth
- **Roles** — `ADMIN`, `TEACHER`, `STUDENT`, `PARENT`, `STAFF`
- **Ownership check** — TEACHER solo modifica/elimina su propio material didáctico
- **ProblemDetail** — respuestas de error estandarizadas (RFC 9457)
- **Archivos** — almacenados en OCI Object Storage privado, acceso via presigned URLs

---

## 🧪 Tests

Tests unitarios implementados con JUnit 5 + Mockito + AssertJ.

| Test | Módulo | Tests |
|------|--------|-------|
| `ActivateAccountUseCaseTest` | auth | 5 |
| `TeacherAccountActivatedListenerTest` | teachers | 3 |
| `GetTeacherByIdUseCaseTest` | teachers | 3 |
| `CreateTeacherUseCaseTest` | teachers | 4 |
| `UpdateTeacherUseCaseTest` | teachers | 4 |
| `CreateParentUseCaseTest` | parents | 5 |
| `LinkParentToStudentUseCaseTest` | parents | 6 |
| `CreateStudentUseCaseTest` | students | 10 |
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
| `GetDocumentTypesUseCaseTest` | document-types | 5 |
| `GetDocumentTypeByIdUseCaseTest` | document-types | 2 |
| `CreateDocumentTypeUseCaseTest` | document-types | 4 |
| `UpdateDocumentTypeUseCaseTest` | document-types | 3 |
| `ToggleDocumentTypeStatusUseCaseTest` | document-types | 4 |
| **Total** | | **114** |

---

## 📊 Estado del Proyecto

### ✅ Implementado

- Auth, Geography, Academic completos + refactor de fronteras del BC `auth/`
- `shared/email/` — EmailService + JavaMailEmailService (OCI SMTP) + AsyncConfig
- `shared/event/` — DomainEvent, AccountActivatedEvent, DomainEventPublisher
- **`students/` — COMPLETO** — 5 agregados + upload de documentos a OCI
- **`students/records/DocumentType` — COMPLETO** — CRUD + toggle is_active, 6 endpoints, 12 tipos predefinidos, 16 tests
- **`teachers/` — COMPLETO** — flujo activación via eventos de dominio
- **`parents/` — COMPLETO** — cuil obligatorio, placeId consistente
- **`grades/` — COMPLETO** — 7 endpoints + seeder
- **`course/` — COMPLETO** — 5 endpoints + seeder
- **`attendance/` — COMPLETO** — 7 endpoints + V19
- **`storage/` — COMPLETO** — OCI Object Storage, puerto + adaptador
- **`teaching-materials/` — COMPLETO** — 5 endpoints, upload OCI, ownership check
- **`resources/` — COMPLETO** — Catálogo de recursos didácticos, unidades físicas y sistema completo de reservas
- **Rate Limiting** — Bucket4j in-memory, 3 endpoints protegidos, configurable por perfil
- **114 tests unitarios** — auth (8), teachers (11), parents (11), students (10), grades (19), course (9), attendance (30), document-types (16)
- **Seeders completos** — cadena `@Order(3→5→6→7→8→9→10)` con escenarios realistas para pruebas
- Flyway V1–V21

### ⏳ Pendiente

- [ ] Notificaciones — base lista: EmailService + DomainEventPublisher ya implementados
- [ ] Auditoría (registrar quién hizo qué y cuándo)
- [ ] Métricas / monitoreo

---

## 📝 Decisiones Arquitectónicas Clave

| Decisión | Razón |
|----------|-------|
| **`auth/` solo gestiona identidad y sesión** | Cada BC crea y activa sus propias entidades |
| **`CreateUserUseCase` factory puro** | Agnóstico del tipo de usuario — llamado por teachers/, students/, parents/ |
| **Activación via eventos de dominio** | `ActivateAccountUseCase` publica `AccountActivatedEvent` — cada BC reacciona independientemente |
| **`@TransactionalEventListener(BEFORE_COMMIT)`** | Atomicidad garantizada con la transacción principal |
| **`SecurityContextHelper` estático** | Centraliza el cast `UserDetails → User` — nunca duplicar |
| **`storage/` como BC separado** | Razón de cambio diferente — el proveedor cloud puede cambiar sin afectar otros BCs |
| **`StorageService` como puerto del dominio** | Los use cases no conocen OCI — solo el adaptador |
| **Rate limiting in-memory** | Sin Redis — apropiado para despliegue en único nodo |
| **`filePath` = objectName OCI** | Necesario para delete y presigned URLs — `fileName` guarda la URL pública |
| **Sin FK a `teachers` en `teaching_materials`** | Evita acoplamiento entre BCs a nivel de esquema de BD |
| **Ownership check con `null` bypass** | ADMIN/STAFF pasan `null` como teacherId — use case interpreta null como permiso total |
| **`GET /my-courses` con query params** | El frontend ya conoce los courseSubjectIds del estudiante — no se duplica la lógica de inscripción |
| **DNI sin dígito verificador** | El DNI argentino es correlativo — el validador fue removido para corrección |
| **Instancia real para modelos con `final` + `@Builder`** | Mockito no puede interceptar métodos en campos `final` — construir con builder |
| **`DocumentType.isActive` con toggle semántico** | Dos endpoints explícitos `/activate` y `/deactivate` en lugar de un PATCH genérico con booleano |
| **`record_id` resuelto en runtime en RecordDocumentSeeder** | Los UUIDs de records son generados dinámicamente en StudentAndParentDataSeeder — no se acoplan via constantes |
| **Seeders con `@Order` explícito** | Cadena determinística `3→5→6→7→8→9→10` garantiza dependencias entre seeders sin condiciones en runtime |

---

**Última actualización:** Abril 2026
**Versión:** 0.11.0
**Estado:** En desarrollo activo — `DocumentType catalog` ✅ completado