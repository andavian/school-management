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
- ✅ Módulo de estudiantes — domain layer completo (personal, health, enrollment, records)
- ✅ Application layer de students/personal completa (DTOs, mapper, 5 use cases)
- ⏳ Infrastructure layer de students (en construcción)
- ⏳ Módulo de padres/tutores (pendiente)

### 🎯 Características Principales

- **DNI como username**: Sistema adaptado a la realidad argentina (siempre 8 dígitos)
- **CUIL validado**: Dígito verificador ANSES/AFIP con compatibilidad con DNI embebido
- **Email opcional**: Para estudiantes menores sin email propio
- **Records Java 17**: Todos los Value Objects migrados de Lombok `@Value` a `record`
- **Token Rotation**: Máxima seguridad en refresh tokens
- **Arquitectura escalable**: Preparada para migrar a microservicios
- **Folios automáticos**: Asignación automática desde el Registro de Calificaciones
- **Flujo transaccional**: Creación de estudiante en 15 pasos atómicos (TODO O NADA)

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
shared/         → Shared Kernel (Dni, Cuil, Email, PhoneNumber, Address, IDs geográficos)
auth/           → Autenticación y autorización ✅
geography/      → Lugares geográficos (País, Provincia, Localidad) ✅
academic/       → Estructura académica (Años, Cursos, Materias) ✅
students/       → Gestión de estudiantes ⏳ (domain ✅ | personal application ✅)
teachers/       → Gestión de profesores ⏳
```

---

## 💻 Tecnologías

| Tecnología | Versión | Uso |
|------------|---------|-----|
| Java | 17 | Records, Value Objects inmutables |
| Spring Boot | 3.2.x | Framework principal |
| Spring Security | 6.x | Auth/Authz + JWT |
| Spring Data JPA | Boot managed | Persistencia |
| MySQL | 8 | Producción |
| H2 | test scope | Tests |
| jjwt | 0.12.3 | JWT access + refresh tokens |
| MapStruct | 1.5.5.Final | Mapeo type-safe entre capas |
| Lombok | Boot managed | Solo modelos complejos (@Builder, @Getter) |
| Flyway | Boot managed | Migraciones de esquema |
| SpringDoc OpenAPI | latest | Swagger UI |
| JUnit 5 + Mockito | Boot managed | Testing |

---

## 📁 Estructura del Proyecto

```
src/main/java/org/school/management/
│
├── shared/                                  # Shared Kernel
│   ├── person/domain/valueobject/
│   │   ├── Dni.java                         # ✅ DNI argentino (exactamente 8 dígitos)
│   │   ├── FullName.java                    # ✅ record: firstName + lastName
│   │   ├── Gender.java                      # ✅ Enum: MALE, FEMALE, OTHER
│   │   ├── Nationality.java
│   │   ├── PhoneNumber.java
│   │   ├── Email.java
│   │   ├── Cuil.java                        # ✅ CUIL con validación dígito verificador ANSES/AFIP
│   │   ├── CuilType.java                    # ✅ Enum con display names en español
│   │   └── Address.java                     # ✅ Domicilio postal (street, number, PlaceId, CP...)
│   ├── geography/domain/valueobject/
│   │   ├── CountryId.java                   # ✅ record UUID — of() + from() + generate()
│   │   ├── ProvinceId.java                  # ✅ record UUID
│   │   └── PlaceId.java                     # ✅ record UUID
│   └── domain/exception/
│       └── DomainException.java
│
├── auth/                                    # BOUNDED CONTEXT: Autenticación ✅
│   └── domain/valueobject/
│       ├── UserId.java                      # ✅ refactorizado a record
│       ├── RoleId.java                      # ✅ refactorizado a record
│       ├── BlacklistedTokenId.java          # ✅ refactorizado a record
│       ├── HashedPassword.java              # ✅ record — toString() oculta el hash
│       ├── PlainPassword.java               # ✅ record — valida fortaleza en constructor
│       └── RoleName.java                    # ✅ record — implementa GrantedAuthority
│
├── geography/                               # BOUNDED CONTEXT: Geografía ✅
│   └── domain/valueobject/
│       ├── IsoCode.java                     # ✅ refactorizado a record
│       ├── PhoneCode.java                   # ✅ refactorizado a record
│       ├── GeographicName.java              # ✅ refactorizado a record
│       ├── PostalCode.java                  # ✅ record — ofNullable() para campos opcionales
│       └── ProvinceCode.java                # ✅ record — ofNullable() para campos opcionales
│
├── academic/                                # BOUNDED CONTEXT: Académico ✅
│   └── domain/
│       ├── valueobject/                     # ✅ todos refactorizados a records
│       │   ├── Year.java, YearLevel.java, Division.java
│       │   ├── OrientationCode.java, SubjectCode.java
│       │   ├── WeeklyHours.java, PeriodNumber.java, RegistryNumber.java
│       │   └── ids/  AcademicYearId, GradeLevelId, OrientationId, SubjectId,
│       │             RegistryId, StudyPlanId, PeriodId, CourseId,
│       │             EvaluationId, EvaluationTypeId, WithdrawalReasonId
│       └── service/
│           ├── FolioAssignmentService.java  # assignNextFolio() @Transactional
│           └── RegistryNumberGenerator.java # generate(AcademicYearId, int year) → String
│
└── students/                                # BOUNDED CONTEXT: Estudiantes ⏳
    ├── personal/
    │   ├── domain/                          # ✅ COMPLETO
    │   │   ├── model/StudentPersonalData.java
    │   │   ├── valueobject/StudentPersonalDataId.java
    │   │   ├── repository/StudentPersonalDataRepository.java
    │   │   └── exception/  StudentNotFoundException, StudentAlreadyExistsException,
    │   │                   InvalidStudentDataException
    │   └── application/                     # ✅ COMPLETO
    │       ├── dto/request/  CreateStudentRequest.java, UpdateStudentRequest.java
    │       ├── dto/response/ StudentResponse.java, StudentSummaryResponse.java
    │       ├── mapper/       StudentPersonalDataApplicationMapper.java
    │       └── usecases/     GetStudentByIdUseCase, GetStudentByDniUseCase,
    │                         SearchStudentsUseCase, UpdateStudentUseCase,
    │                         CreateStudentUseCase (orquestador 15 pasos)
    ├── health/
    │   └── domain/                          # ✅ COMPLETO
    │       ├── model/StudentHealthRecord.java
    │       ├── valueobject/  HealthRecordId, BloodType (fromString por displayName)
    │       └── repository/StudentHealthRecordRepository.java
    ├── enrollment/
    │   └── domain/                          # ✅ COMPLETO
    │       ├── model/StudentEnrollment.java
    │       ├── valueobject/  EnrollmentId, EnrollmentType, EnrollmentStatus
    │       └── repository/StudentEnrollmentRepository.java
    └── records/
        └── domain/                          # ✅ COMPLETO
            ├── model/  StudentRecord.java, RecordDocument.java
            ├── valueobject/  RecordId, RecordNumber, DocumentId, DocumentTypeId...
            └── repository/  StudentRecordRepository.java
```

---

## 🗂️ Módulos Implementados

### ✅ Auth — Autenticación y Autorización
JWT con refresh tokens, rotación de tokens, blacklist, sesiones múltiples por dispositivo.
Todos los VOs refactorizados a records Java 17.

### ✅ Geography — Geografía Argentina
Jerarquía País → Provincia → Localidad. Endpoints públicos para autocompletado en formularios.
`GetPlaceByIdUseCase` retorna `PlaceResponse` con jerarquía completa.

### ✅ Academic — Estructura Académica
Años lectivos, orientaciones, cursos (1°A–7°B), materias, registro de calificaciones.
`FolioAssignmentService` y `RegistryNumberGenerator` disponibles para Students.
Todos los VOs e IDs refactorizados a records Java 17.

### ⏳ Students — Gestión de Estudiantes

**Domain Layer — todos los agregados ✅ Completos**
- `StudentPersonalData` — identidad civil, domicilio, validación CUIL↔DNI
- `StudentHealthRecord` — ficha médica, obra social, contacto de emergencia
- `StudentEnrollment` — matrícula por ciclo, estados, baja lógica
- `StudentRecord` — legajo digital con documentos adjuntos

**Application Layer — `personal/` ✅ Completo**

DTOs:
- `CreateStudentRequest` — datos personales + `HealthDataRequest` + `ParentRequest` + matrícula
- `UpdateStudentRequest` — solo contacto y domicilio (studentId va como `@PathVariable`)
- `StudentResponse` — response completo con `AddressResponse` y `PlaceResponse` nested
- `StudentSummaryResponse` — para listas y búsquedas

Use Cases:
- `GetStudentByIdUseCase` — resuelve Geography via `GetPlaceByIdUseCase`
- `GetStudentByDniUseCase` — delega resolución de places a GetById
- `SearchStudentsUseCase` — prioridad: dni > residencePlaceId > fullName > all
- `UpdateStudentUseCase` — llama `updatePersonalData(fullName, phone, email, address)`
- `CreateStudentUseCase` — orquestador @Transactional de 15 pasos

**Próximo: Infrastructure Layer `personal/`**
- `StudentPersonalDataJpaEntity` + `StudentPersonalDataJpaRepository`
- `StudentPersonalDataRepositoryAdapter`
- `StudentPersonalDataPersistenceMapper` (aplanar Address, bytesToUuid/uuidToBytes)
- `StudentController` + `StudentExceptionHandler`

---

## 🗄️ Base de Datos

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
| `V11` | `document_types`, `student_records`, `record_documents` |
| `V12` | `parents`, `student_parents` |
| `V14` | `withdrawal_reasons`, `student_enrollments` |

### Convenciones de BD
- **PK**: `BINARY(16)` (UUID binario) — los mappers incluyen `bytesToUuid` / `uuidToBytes`
- **Timestamps**: `TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP`
- **Flags**: `is_active`, `is_current`, `is_mandatory`, `requires_documentation` tipo `BOOLEAN`
- **Nunca** modificar migraciones ya ejecutadas — siempre crear `V{n+1}`

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

## 🔑 Endpoints API

### Auth
| Método | Endpoint | Auth | Descripción |
|--------|----------|------|-------------|
| POST | `/api/auth/login` | ❌ | Login con DNI + password |
| POST | `/api/auth/logout` | ✅ | Logout |
| POST | `/api/auth/refresh` | ❌ | Refresh del access token |
| GET | `/api/auth/profile` | ✅ | Perfil del usuario autenticado |
| POST | `/api/auth/change-password` | ✅ | Cambio de contraseña |
| GET | `/api/auth/sessions` | ✅ | Sesiones activas |
| DELETE | `/api/auth/sessions/{id}` | ✅ | Revocar sesión |

### Geography (todos públicos)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/geography/countries` | Listar países |
| GET | `/api/geography/provinces/search?q=` | Buscar provincias |
| GET | `/api/geography/places/{id}` | Lugar por ID (con jerarquía) |
| GET | `/api/geography/places/search?q=` | Buscar lugares |

### Academic
| Método | Endpoint | Auth | Descripción |
|--------|----------|------|-------------|
| POST | `/api/admin/academic-years` | ADMIN | Crear año académico |
| GET | `/api/admin/academic-years/current` | ✅ | Año actual |
| POST | `/api/admin/grade-levels` | ADMIN | Crear curso |
| GET | `/api/admin/grade-levels` | ✅ | Listar cursos |

### Students (infrastructure layer pendiente)
| Método | Endpoint | Auth | Descripción |
|--------|----------|------|-------------|
| POST | `/api/admin/students` | ADMIN | Crear estudiante (15 pasos atómicos) |
| GET | `/api/admin/students/{id}` | ADMIN/STAFF | Obtener por ID |
| GET | `/api/admin/students/dni/{dni}` | ADMIN/STAFF | Obtener por DNI |
| GET | `/api/admin/students` | ADMIN/STAFF | Buscar (dni, fullName, residencePlaceId) |
| PATCH | `/api/admin/students/{id}` | ADMIN/STAFF | Actualizar contacto/domicilio |

---

## 🔒 Seguridad

- **JWT access token**: corta duración (configurable)
- **Refresh token**: larga duración, almacenado hasheado en BD, rotación en cada uso
- **Blacklist**: access tokens revocados hasta expiración
- **Roles**: `ADMIN`, `TEACHER`, `STUDENT`, `PARENT`, `STAFF`
- **Password inicial estudiante**: `{DNI}Ipet132!`

---

## 🔑 Credenciales de Prueba

| Rol | DNI | Password |
|-----|-----|----------|
| ADMIN | `00000001` | `Admin123!` |
| TEACHER | `12345678` | `Teacher123!` |
| STUDENT (con email) | `11223344` | `11223344Ipet132!` |
| STUDENT (sin email) | `87654321` | `87654321Ipet132!` |

---

## 📊 Estado del Proyecto

### ✅ Implementado

- Auth, Geography, Academic completos
- **Refactor global**: todos los VOs de `auth/`, `geography/`, `academic/` y `shared/` migrados de Lombok `@Value` a `record` Java 17 con `of()` + `from()` estandarizados
- **Students domain**: los 4 agregados con domain layer completo
- **Students personal application layer**: 5 use cases + 4 DTOs + mapper
- Flyway V1–V7, V10–V14 ejecutados

### ⏳ Pendiente

- [ ] Infrastructure layer `students/personal/` ← **próximo**
- [ ] Application + Infrastructure `students/health/`
- [ ] Application + Infrastructure `students/enrollment/`
- [ ] Application + Infrastructure `students/records/`
- [ ] Agregado `students/parents/` — completo (domain + application + infrastructure)
- [ ] Teachers — asignación a cursos
- [ ] Calificaciones por período y promedio final
- [ ] Email service (password aleatorio para padres)
- [ ] Rate limiting, auditoría, métricas

---

## 📝 Decisiones Arquitectónicas Clave

| Decisión | Razón |
|----------|-------|
| **Records Java 17 para VOs** | Inmutabilidad nativa, equals/hashCode/toString sin boilerplate, sin Lombok `@Value` |
| **of() como factory principal** | Estándar del proyecto — todos los VOs tienen `of()`. `from()` como alias |
| **DNI siempre 8 dígitos** | Consistente con `Dni.java` del Shared Kernel |
| **CreateStudentRequest unificado** | Un solo request para 15 pasos atómicos — HealthDataRequest y ParentRequest como nested records |
| **studentId como @PathVariable** | No va en el body — estándar REST |
| **Validaciones de negocio en dominio** | No en DTOs — edad, CUIL↔DNI en `create()` del agregado |
| **BloodType.fromString() por displayName** | API recibe "A+", "B-" — no exponer nombres internos |
| **ofNullable() en VOs opcionales** | PostalCode, ProvinceCode — para persistence mappers con columnas nullable |
| **PlaceResponse como parámetro del mapper** | Application mapper no puede cruzar a Geography — use case resuelve los lugares |
| **RegistryNumberGenerator.generate(AcademicYearId, int)** | Requiere dos argumentos — retorna String, no RecordNumber |
| **BINARY(16) para UUIDs** | Consistente en todo el proyecto |
| **MapStruct 3 capas** | persistence, application y web mapper separados — type-safe en compile-time |
| **Flyway migraciones** | Control de versión de esquema — nunca `ddl-auto: create` |

---

**Última actualización**: Marzo 2026
**Versión**: 2.2.0
**Estado**: En desarrollo activo — Students domain completo + personal application layer completo
