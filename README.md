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
- ⏳ Módulo de docentes (en construcción)
- ⏳ Calificaciones por período y promedio final

### 🎯 Características Principales

- **DNI como username** — sistema adaptado a la realidad argentina (siempre 8 dígitos)
- **CUIL validado** — dígito verificador ANSES/AFIP con compatibilidad con DNI embebido
- **Email opcional para estudiantes** — menores sin email propio
- **Email obligatorio para padres** — notificaciones y credenciales
- **Records Java 17** — todos los Value Objects son `record` nativo
- **Token Rotation** — máxima seguridad en refresh tokens
- **Flujo transaccional de 15 pasos** — creación de estudiante TODO O NADA
- **BINARY(16) para UUIDs** — conversión transparente via `UuidBinaryConverter` compartido
- **ProblemDetail (RFC 9457)** — respuestas de error estandarizadas en toda la API
- **Sin delete físico** — bajas lógicas via `StudentEnrollment`

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
│  │  Services, Repository Interfaces (Ports)        │   │
│  └─────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
```

### Vertical Slicing (Bounded Contexts)

```
shared/         → Shared Kernel (Dni, Cuil, Email, PhoneNumber, Address, Gender,
                                 IDs geográficos, UuidBinaryConverter)
auth/           → Autenticación y autorización ✅
geography/      → Lugares geográficos (País, Provincia, Localidad) ✅
academic/       → Estructura académica (Años, Cursos, Materias) ✅
students/       → Gestión de estudiantes ✅ COMPLETO
teachers/       → Gestión de profesores ⏳
```

---

## 💻 Tecnologías

| Tecnología | Versión | Uso |
|------------|---------|-----|
| Java | 17 | Records, Value Objects inmutables, pattern matching instanceof |
| Spring Boot | 3.2.x | Framework principal |
| Spring Security | 6.x | Auth/Authz + JWT |
| Spring Data JPA | Boot managed | Persistencia |
| MySQL | 8 | Producción |
| H2 | test scope | Tests |
| jjwt | 0.12.3 | JWT access + refresh tokens |
| MapStruct | 1.5.5.Final | Mapeo type-safe entre capas (3 capas: persistence/application/web) |
| Lombok | Boot managed | Solo modelos complejos (@Builder, @Getter) |
| Flyway | Boot managed | Migraciones de esquema |
| SpringDoc OpenAPI | latest | Swagger UI |
| JUnit 5 + Mockito | Boot managed | Testing |

---

## 📁 Estructura del Proyecto

```
src/main/java/org/school/management/
│
├── shared/                                          # Shared Kernel
│   ├── person/domain/valueobject/
│   │   ├── Dni.java                                 # DNI argentino (exactamente 8 dígitos)
│   │   ├── FullName.java                            # record: firstName + lastName
│   │   ├── Gender.java                              # Enum puro — usar directo en entidades JPA
│   │   ├── Nationality.java, PhoneNumber.java, Email.java
│   │   ├── Cuil.java                                # CUIL con validación dígito verificador ANSES/AFIP
│   │   ├── CuilType.java
│   │   └── Address.java                             # Domicilio postal (street, number, PlaceId, CP...)
│   ├── geography/domain/valueobject/
│   │   └── CountryId.java, ProvinceId.java, PlaceId.java
│   ├── domain/exception/
│   │   └── DomainException.java
│   └── infrastructure/persistence/converter/
│       └── UuidBinaryConverter.java                 # AttributeConverter UUID ↔ BINARY(16)
│
├── auth/                                            # BOUNDED CONTEXT: Autenticación ✅
│   └── domain/model/
│       └── User.java                                # Implementa UserDetails directamente
│
├── geography/                                       # BOUNDED CONTEXT: Geografía ✅
│
├── academic/                                        # BOUNDED CONTEXT: Académico ✅
│   └── domain/service/
│       ├── FolioAssignmentService.java              # assignNextFolio() — @Transactional
│       └── RegistryNumberGenerator.java             # solo para QualificationRegistry
│
└── students/                                        # BOUNDED CONTEXT: Estudiantes ✅ COMPLETO
    ├── personal/                                    # ✅ COMPLETO
    │   ├── domain/
    │   │   ├── model/StudentPersonalData.java
    │   │   ├── valueobject/StudentPersonalDataId.java
    │   │   ├── repository/StudentPersonalDataRepository.java
    │   │   └── exception/  StudentNotFoundException, StudentAlreadyExistsException,
    │   │                   InvalidStudentDataException
    │   ├── application/
    │   │   ├── dto/request/  CreateStudentRequest.java  # incluye HealthDataRequest + ParentRequest
    │   │   │                 UpdateStudentRequest.java
    │   │   ├── dto/response/ StudentResponse.java, StudentSummaryResponse.java
    │   │   ├── mapper/       StudentPersonalDataApplicationMapper.java
    │   │   └── usecases/     GetStudentByIdUseCase, GetStudentByDniUseCase,
    │   │                     SearchStudentsUseCase, UpdateStudentUseCase,
    │   │                     CreateStudentUseCase  ← orquestador 15 pasos ✅ COMPLETO
    │   └── infrastructure/
    │       ├── persistence/  entity, JpaRepository, adapter, PersistenceMapper
    │       └── web/          controller (5 endpoints), StudentWebDto, mapper, exception handler
    │
    ├── health/                                      # ✅ COMPLETO
    │   ├── domain/
    │   │   ├── model/StudentHealthRecord.java
    │   │   ├── valueobject/  HealthRecordId, BloodType (fromString por displayName)
    │   │   ├── repository/StudentHealthRecordRepository.java
    │   │   └── exception/HealthRecordNotFoundException
    │   ├── application/
    │   │   ├── dto/          UpdateHealthRecordRequest (PATCH semántico), HealthRecordResponse
    │   │   ├── mapper/       StudentHealthRecordApplicationMapper
    │   │   └── usecases/     GetHealthRecordByStudentIdUseCase, UpdateHealthRecordUseCase
    │   └── infrastructure/
    │       ├── persistence/  entity (emergency_contact_name concatenado), adapter, mapper
    │       └── web/          HealthRecordWebDto, mapper, controller (GET/PATCH), exception handler
    │
    ├── enrollment/                                  # ✅ COMPLETO
    │   ├── domain/
    │   │   ├── model/StudentEnrollment.java          # complete(), withdraw(), graduate()
    │   │   ├── valueobject/  EnrollmentId, EnrollmentStatus, EnrollmentType
    │   │   ├── repository/StudentEnrollmentRepository.java  # sin delete físico
    │   │   └── exception/    EnrollmentNotFoundException, EnrollmentAlreadyCompletedException,
    │   │                     EnrollmentAlreadyWithdrawnException, InvalidEnrollmentException
    │   ├── application/
    │   │   ├── dto/          UpdateEnrollmentRequest, EnrollmentResponse
    │   │   ├── mapper/       StudentEnrollmentApplicationMapper
    │   │   └── usecases/     GetEnrollmentByStudentIdUseCase, GetActiveEnrollmentUseCase,
    │   │                     UpdateEnrollmentUseCase (cierre de ciclo + baja)
    │   └── infrastructure/
    │       ├── persistence/  entity, JpaRepository (JPQL para checks), adapter, mapper
    │       └── web/          EnrollmentWebDto, mapper, controller (3 endpoints), exception handler
    │
    ├── records/                                     # ✅ COMPLETO
    │   ├── domain/
    │   │   ├── model/        StudentRecord.java, RecordDocument.java, DocumentType.java
    │   │   ├── valueobject/  RecordId, RecordNumber (= DNI), DocumentId, DocumentTypeId,
    │   │   │                 DocumentStatus, DocumentCategory, RecordStatus
    │   │   ├── repository/   StudentRecordRepository  # findByStudentId — un legajo por estudiante
    │   │   └── exception/    RecordNotFoundException, DocumentNotFoundException,
    │   │                     RecordAlreadyApprovedException, DocumentAlreadyApprovedException,
    │   │                     IncompleteRecordException, RecordNotReadyForApprovalException
    │   ├── application/
    │   │   ├── dto/          AddDocumentRequest, UpdateRecordStatusRequest,
    │   │   │                 RecordDocumentResponse, StudentRecordResponse
    │   │   ├── mapper/       StudentRecordApplicationMapper (métodos default)
    │   │   └── usecases/     GetRecordByStudentIdUseCase, AddDocumentToRecordUseCase,
    │   │                     ReviewDocumentUseCase, UpdateRecordStatusUseCase
    │   └── infrastructure/
    │       ├── persistence/  StudentRecordEntity, RecordDocumentEntity,
    │       │                 StudentRecordJpaRepository, RecordDocumentJpaRepository,
    │       │                 StudentRecordPersistenceMapper (default methods),
    │       │                 StudentRecordRepositoryAdapter (sync manual de documentos)
    │       └── web/          RecordWebDto, mapper, controller (4 endpoints),
    │                         RecordExceptionHandler (7 handlers)
    │
    └── parents/                                     # ✅ COMPLETO
        ├── domain/
        │   ├── model/        Parent.java             # entidad global — un padre, múltiples hijos
        │   │                 StudentParent.java       # vínculo con flags operativos
        │   ├── valueobject/  ParentId, StudentParentId,
        │   │                 ParentRelationship (FATHER, MOTHER, GUARDIAN, GRANDPARENT, OTHER)
        │   ├── repository/   ParentRepository, StudentParentRepository
        │   └── exception/    ParentNotFoundException, ParentAlreadyExistsException,
        │                     DuplicatePrimaryContactException, InvalidParentDataException
        ├── application/
        │   ├── dto/          CreateParentRequest, UpdateParentRequest, LinkParentRequest,
        │   │                 ParentResponse, StudentParentResponse
        │   ├── mapper/       ParentApplicationMapper (métodos default)
        │   └── usecases/     GetParentsByStudentIdUseCase, CreateParentUseCase,
        │                     UpdateParentUseCase, LinkParentToStudentUseCase
        └── infrastructure/
            ├── persistence/  ParentEntity, StudentParentEntity,
            │                 ParentJpaRepository, StudentParentJpaRepository,
            │                 ParentPersistenceMapper, ParentRepositoryAdapter,
            │                 StudentParentRepositoryAdapter
            └── web/          ParentWebDto, ParentWebMapper, ParentController (4 endpoints),
                              ParentExceptionHandler (5 handlers)
```

---

## 🗂️ Módulos Implementados

### ✅ Auth — Autenticación y Autorización

JWT con refresh tokens, rotación de tokens, blacklist, sesiones múltiples por dispositivo.
`User` implementa `UserDetails` directamente — en controllers usar pattern matching Java 17:

```java
if (userDetails instanceof User user) { return user.getUserId().value(); }
```

### ✅ Geography — Geografía Argentina

Jerarquía País → Provincia → Localidad. Endpoints públicos para autocompletado en formularios.

### ✅ Academic — Estructura Académica

Años lectivos, orientaciones, cursos (1°A–7°B), materias, registro de calificaciones.
`FolioAssignmentService.assignNextFolio()` — asignación atómica y transaccional de folios.

### ✅ Students — COMPLETO (5 agregados)

---

#### Students Personal

**Endpoints:**

| Método | Path | Rol | Descripción |
|--------|------|-----|-------------|
| POST | `/api/admin/students` | ADMIN | Crear estudiante (15 pasos atómicos) |
| GET | `/api/admin/students/{id}` | ADMIN, STAFF | Obtener por ID |
| GET | `/api/admin/students/dni/{dni}` | ADMIN, STAFF | Obtener por DNI |
| GET | `/api/admin/students` | ADMIN, STAFF | Buscar (dni / fullName / residencePlaceId) |
| PATCH | `/api/admin/students/{id}` | ADMIN, STAFF | Actualizar contacto y domicilio |

**CreateStudentUseCase — 15 pasos completos:**

```
1-2.  Validar unicidad DNI y CUIL
3.    Obtener AcademicYear activo
4.    Validar GradeLevel activo
5.    Asignar folio → FolioAssignmentService.assignNextFolio()
6.    Generar password → {DNI}Ipet132!
7.    Crear User (rol STUDENT)
8.    Crear StudentPersonalData
9.    Crear StudentHealthRecord
10.   Obtener QualificationRegistry activo
11.   Crear StudentRecord (recordNumber = DNI)
12.   Buscar Parent por DNI → si no existe: crear User (PARENT) + crear Parent
13.   Crear StudentParent (relationship, isPrimaryContact, isAuthorizedPickup)
14.   Crear StudentEnrollment
15.   Commit → retornar StudentResponse
```

---

#### Students Health

**Decisión de diseño clave:** La tabla `student_health_records` (V10) tiene una sola columna
`emergency_contact_name VARCHAR(200)`. Se concatena "firstName lastName" en esa columna —
la separación ocurre en el `@AfterMapping` del PersistenceMapper (split por primer espacio).

| Método | Path | Rol | Descripción |
|--------|------|-----|-------------|
| GET | `/api/admin/students/{studentId}/health` | ADMIN, STAFF | Obtener ficha médica |
| PATCH | `/api/admin/students/{studentId}/health` | ADMIN, STAFF | Actualizar ficha (null conserva valor) |

---

#### Students Enrollment

| Método | Path | Rol | Descripción |
|--------|------|-----|-------------|
| GET | `/api/admin/students/{studentId}/enrollments` | ADMIN, STAFF | Historial de inscripciones |
| GET | `/api/admin/students/{studentId}/enrollments/{academicYearId}` | ADMIN, STAFF | Inscripción de un año |
| PATCH | `/api/admin/students/{studentId}/enrollments/{enrollmentId}` | ADMIN | Cierre de ciclo o baja |

`UpdateEnrollmentUseCase` soporta dos operaciones mutuamente excluyentes:
- **Cierre de ciclo** — `finalAverage` + `passed` + `completionDate`
- **Baja** — `withdrawalReasonId` + `withdrawalObservations` + `withdrawalDate`

---

#### Students Records (Legajo)

**Decisión de diseño clave:** `RecordNumber` = DNI del estudiante. El legajo es único y permanente
— no cambia entre años académicos. `StudentRecord.findByStudentId()` es la búsqueda principal.

| Método | Path | Rol | Descripción |
|--------|------|-----|-------------|
| GET | `/api/admin/students/{studentId}/record` | ADMIN, STAFF | Obtener legajo con documentos |
| POST | `/api/admin/students/{studentId}/record/documents` | ADMIN, STAFF | Subir documento |
| PATCH | `/api/admin/students/{studentId}/record/documents/{documentId}` | ADMIN, STAFF | Aprobar/rechazar documento |
| PATCH | `/api/admin/students/{studentId}/record/status` | ADMIN | Cambiar estado del legajo |

Workflow del legajo: `INCOMPLETE` → `COMPLETE` → `UNDER_REVIEW` → `APPROVED` / `REJECTED`

---

#### Students Parents

**Decisiones de diseño clave:**
- `Parent` es entidad global — puede tener hijos en distintas escuelas del sistema
- Un padre se identifica por DNI — único e inmutable
- Solo puede haber un `isPrimaryContact = true` por estudiante
- Password del padre: aleatorio seguro (pendiente email service para envío)

| Método | Path | Rol | Descripción |
|--------|------|-----|-------------|
| POST | `/api/admin/parents` | ADMIN, STAFF | Crear padre/tutor |
| PATCH | `/api/admin/parents/{parentId}` | ADMIN, STAFF | Actualizar datos del padre |
| GET | `/api/admin/students/{studentId}/parents` | ADMIN, STAFF | Listar padres de un estudiante |
| POST | `/api/admin/students/{studentId}/parents` | ADMIN, STAFF | Vincular padre a estudiante |

---

### Migraciones Flyway

| Migración | Descripción |
|-----------|-------------|
| `V1` | Tabla `users` |
| `V2` | Tabla `blacklisted_tokens` |
| `V3` | Admin por defecto (solo dev) |
| `V4` | Tabla `refresh_tokens` |
| `V5` | `countries`, `provinces`, `places` |
| `V6` | `academic_years`, `orientations`, `grade_levels`, `subjects`, `qualification_registries` |
| `V7` | `study_plans`, `evaluation_periods`, `grades` |
| `V10` | `student_personal_data`, `student_health_records` |
| `V11` | `document_types`, `student_records` (record_number = DNI, único por estudiante), `record_documents` |
| `V12` | `parents`, `student_parents` |
| `V14` | `withdrawal_reasons`, `student_enrollments` |

---

## ⚙️ Configuración

```bash
# 1. Crear base de datos
mysql -u root -p
CREATE DATABASE ipet132_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 2. Configurar application-dev.yml con credenciales de BD

# 3. Compilar
mvn clean install

# 4. Ejecutar en perfil dev (carga seeders automáticamente)
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Disponible en: http://localhost:8080
# Swagger UI:    http://localhost:8080/swagger-ui.html
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
- **Blacklist** — access tokens revocados hasta expiración
- **Roles** — `ADMIN`, `TEACHER`, `STUDENT`, `PARENT`, `STAFF`
- **Password inicial estudiante** — `{DNI}Ipet132!`
- **Password padre** — aleatorio seguro (pendiente email service)
- **ProblemDetail** — respuestas de error estandarizadas (RFC 9457)

---

## 📊 Estado del Proyecto

### ✅ Implementado

- Auth, Geography, Academic completos
- **Refactor global** — todos los VOs migrados a `record` Java 17
- **`UuidBinaryConverter`** en `shared/infrastructure/` — conversión BINARY(16) ↔ UUID
- **`students/` — COMPLETO** — 5 agregados de punta a punta:
    - `personal/` — 5 use cases, CreateStudentUseCase 15 pasos
    - `health/` — PATCH semántico, BloodType por displayName
    - `enrollment/` — cierre de ciclo, baja lógica, estados terminales
    - `records/` — legajo por DNI, workflow de aprobación de documentos
    - `parents/` — entidad global, vínculo estudiante-padre, contacto principal exclusivo
- Flyway V1–V7, V10–V12, V14

### ⏳ Pendiente

- [ ] `teachers/` — domain + application + infrastructure ← **próximo**
- [ ] Calificaciones por período y promedio final
- [ ] Email service — credenciales para padres
- [ ] Tests unitarios e integración
- [ ] Seeders para `students/` y `parents/`
- [ ] Rate limiting, auditoría, métricas

---

## 📝 Decisiones Arquitectónicas Clave

| Decisión | Razón |
|----------|-------|
| **RecordNumber = DNI** | Legajo único y permanente por estudiante — compatible con ministerio |
| **Un legajo por estudiante** | El DNI no cambia — el legajo tampoco |
| **RegistryNumberGenerator solo para QualificationRegistry** | Genera REG-YYYY-NNNNNN — nunca para StudentRecord |
| **Sin @OneToMany en StudentRecordEntity** | Evita problemas con BINARY(16) en colecciones — documentos sincronizados manualmente |
| **Parent es entidad global** | Un padre puede tener hijos en distintas escuelas |
| **isPrimaryContact exclusivo** | Un solo contacto principal por estudiante — validado en use case |
| **Password padre aleatorio** | Más seguro que DNI — pendiente email service |
| **emergency_contact_name concatenado** | Schema V10 tiene columna única — split en @AfterMapping |
| **Records Java 17 para VOs** | Inmutabilidad nativa, equals/hashCode/toString sin boilerplate |
| **of() como factory principal** | Estándar del proyecto — `from()` como alias |
| **UuidBinaryConverter en shared/** | Un solo converter para todos los BCs |
| **@Id como UUID + @Convert** | `JpaRepository<Entity, UUID>` transparente |
| **@PrePersist / @PreUpdate** | Timestamps nunca nulos independientemente del dominio |
| **Gender directo en entidades JPA** | Enum puro del Shared Kernel — sin GenderEntity duplicado |
| **@AfterMapping o default methods** | Para VOs compuestos que necesitan múltiples columnas |
| **Sin INSTANCE en mappers Spring** | `componentModel = "spring"` genera bean |
| **ProblemDetail para errores** | RFC 9457, nativo en Spring 6 |
| **User implementa UserDetails directo** | Cast via pattern matching Java 17 en `extractUserId()` |
| **Sin delete físico** | Ningún repositorio de students expone delete — baja lógica via enrollment |
| **CreateStudentRequest unificado** | Un request para el flujo atómico de 15 pasos |
| **DTOs en request/ y response/** | Separación clara — nunca en dto/ directamente |

---

**Última actualización:** Marzo 2026
**Versión:** 3.0.0
**Estado:** En desarrollo activo — `students/` ✅ COMPLETO | `teachers/` ⏳ próximo