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
- ✅ Módulo de estudiantes — `personal/` completo (domain + application + infrastructure)
- ✅ Módulo de estudiantes — `health/` completo (domain + application + infrastructure)
- ⏳ Infrastructure layer de `students/enrollment/`, `records/` (en construcción)
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
- **BINARY(16) para UUIDs**: Conversión transparente via `UuidBinaryConverter` compartido
- **ProblemDetail (RFC 9457)**: Respuestas de error estandarizadas en toda la API

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
shared/         → Shared Kernel (Dni, Cuil, Email, PhoneNumber, Address, Gender, IDs geográficos,
                                 UuidBinaryConverter)
auth/           → Autenticación y autorización ✅
geography/      → Lugares geográficos (País, Provincia, Localidad) ✅
academic/       → Estructura académica (Años, Cursos, Materias) ✅
students/       → Gestión de estudiantes ⏳ (personal ✅ COMPLETO)
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
│   │   ├── Dni.java                                 # ✅ DNI argentino (exactamente 8 dígitos)
│   │   ├── FullName.java                            # ✅ record: firstName + lastName
│   │   ├── Gender.java                              # ✅ Enum puro — usar directo en entidades JPA
│   │   ├── Nationality.java, PhoneNumber.java, Email.java
│   │   ├── Cuil.java                                # ✅ CUIL con validación dígito verificador ANSES/AFIP
│   │   ├── CuilType.java
│   │   └── Address.java                             # ✅ Domicilio postal (street, number, PlaceId, CP...)
│   ├── geography/domain/valueobject/
│   │   ├── CountryId.java, ProvinceId.java, PlaceId.java  # ✅ records UUID con of()+from()+generate()
│   ├── domain/exception/
│   │   └── DomainException.java
│   └── infrastructure/persistence/converter/
│       └── UuidBinaryConverter.java                 # ✅ NUEVO — AttributeConverter UUID ↔ BINARY(16)
│
├── auth/                                            # BOUNDED CONTEXT: Autenticación ✅
│   └── domain/model/
│       └── User.java                                # ✅ Implementa UserDetails directamente (no wrapper)
│
├── geography/                                       # BOUNDED CONTEXT: Geografía ✅
│
├── academic/                                        # BOUNDED CONTEXT: Académico ✅
│
└── students/                                        # BOUNDED CONTEXT: Estudiantes ⏳
    ├── personal/                                    # ✅ COMPLETO
    │   ├── domain/
    │   │   ├── model/StudentPersonalData.java
    │   │   ├── valueobject/StudentPersonalDataId.java  # of(UUID) + from(UUID|String) + generate()
    │   │   ├── repository/StudentPersonalDataRepository.java
    │   │   └── exception/  StudentNotFoundException, StudentAlreadyExistsException,
    │   │                   InvalidStudentDataException
    │   ├── application/
    │   │   ├── dto/request/  CreateStudentRequest.java, UpdateStudentRequest.java
    │   │   ├── dto/response/ StudentResponse.java, StudentSummaryResponse.java
    │   │   ├── mapper/       StudentPersonalDataApplicationMapper.java
    │   │   └── usecases/     GetStudentByIdUseCase, GetStudentByDniUseCase,
    │   │                     SearchStudentsUseCase, UpdateStudentUseCase,
    │   │                     CreateStudentUseCase (orquestador 15 pasos)
    │   └── infrastructure/
    │       ├── persistence/
    │       │   ├── entity/    StudentPersonalDataEntity.java   # UUID @Id + UuidBinaryConverter
    │       │   │                                               # Gender del SharedKernel directo
    │       │   │                                               # @PrePersist / @PreUpdate
    │       │   ├── repository/ StudentPersonalDataJpaRepository.java  # JpaRepository<Entity, UUID>
    │       │   ├── adapter/   StudentPersonalDataRepositoryAdapter.java
    │       │   └── mapper/    StudentPersonalDataPersistenceMapper.java  # @AfterMapping para FullName+Address
    │       └── web/
    │           ├── controller/  StudentController.java    # 5 endpoints, extractUserId via User cast
    │           ├── dto/         StudentWebDto.java        # clase contenedora con todos los web DTOs
    │           ├── mapper/      StudentWebMapper.java
    │           └── exception/   StudentExceptionHandler.java  # ProblemDetail (RFC 9457)
    ├── health/                                          # ✅ COMPLETO
    │   ├── domain/
    │   │   ├── model/StudentHealthRecord.java
    │   │   ├── valueobject/  HealthRecordId (record Java 17), BloodType (fromString por displayName)
    │   │   ├── repository/StudentHealthRecordRepository.java  # VOs en puerto (no UUID crudo)
    │   │   └── exception/HealthRecordNotFoundException
    │   ├── application/
    │   │   ├── dto/request/   UpdateHealthRecordRequest.java   # PATCH semántico
    │   │   ├── dto/response/  HealthRecordResponse.java        # incluye flags calculados del dominio
    │   │   ├── mapper/        StudentHealthRecordApplicationMapper.java
    │   │   └── usecases/      GetHealthRecordByStudentIdUseCase.java
    │   │                      UpdateHealthRecordUseCase.java
    │   └── infrastructure/
    │       ├── persistence/
    │       │   ├── entity/    StudentHealthRecordEntity.java   # emergency_contact_name concatenado
    │       │   ├── repository/ StudentHealthRecordJpaRepository.java
    │       │   ├── adapter/   StudentHealthRecordRepositoryAdapter.java
    │       │   └── mapper/    StudentHealthRecordPersistenceMapper.java  # @AfterMapping FullName+PhoneNumber
    │       └── web/
    │           ├── controller/  HealthRecordController.java  # GET + PATCH
    │           ├── dto/         HealthRecordWebDto.java       # clase contenedora
    │           ├── mapper/      HealthRecordWebMapper.java
    │           └── exception/   HealthRecordExceptionHandler.java  # ProblemDetail 404/422
    ├── enrollment/
    │   └── domain/                                  # ✅ COMPLETO
    │       ├── model/StudentEnrollment.java
    │       ├── valueobject/  EnrollmentId, EnrollmentType, EnrollmentStatus
    │       └── repository/StudentEnrollmentRepository.java
    └── records/
        └── domain/                                  # ✅ COMPLETO
            ├── model/  StudentRecord.java, RecordDocument.java
            ├── valueobject/  RecordId, RecordNumber, DocumentId, DocumentTypeId...
            └── repository/  StudentRecordRepository.java
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

### ✅ Students Personal — COMPLETO

**Infrastructure Layer:**

Persistence:
- `StudentPersonalDataEntity` — `@Id` como `UUID` con `@Convert(UuidBinaryConverter.class)`, `Gender` del Shared Kernel directo (sin `GenderEntity`), `@PrePersist`/`@PreUpdate` para timestamps
- `StudentPersonalDataJpaRepository` — `JpaRepository<Entity, UUID>`, queries para búsqueda por nombre y por lugar de residencia
- `StudentPersonalDataRepositoryAdapter` — implementa el contrato completo del puerto (sin `deleteByStudentId` — no hay delete físico)
- `StudentPersonalDataPersistenceMapper` — `@AfterMapping` para `FullName` y `Address` (VOs compuestos de múltiples columnas), sin `INSTANCE` estático

Web:
- `StudentController` — 5 endpoints REST, `@PreAuthorize` por rol, `extractUserId()` via cast a `User` con pattern matching Java 17
- `StudentWebDto` — clase contenedora `final` con todos los web DTOs del módulo
- `StudentWebMapper` — tercera capa de mappers (web ↔ application)
- `StudentExceptionHandler` — `ProblemDetail` (RFC 9457): 404/409/422/500

Shared:
- `UuidBinaryConverter` en `shared/infrastructure/persistence/converter/` — reutilizable por todos los bounded contexts

**Endpoints disponibles:**

| Método | Path | Rol | Descripción |
|--------|------|-----|-------------|
| POST | `/api/admin/students` | ADMIN | Crear estudiante (15 pasos atómicos) |
| GET | `/api/admin/students/{id}` | ADMIN, STAFF | Obtener por ID |
| GET | `/api/admin/students/dni/{dni}` | ADMIN, STAFF | Obtener por DNI |
| GET | `/api/admin/students` | ADMIN, STAFF | Buscar (dni / fullName / residencePlaceId) |
| PATCH | `/api/admin/students/{id}` | ADMIN, STAFF | Actualizar contacto y domicilio |

### ✅ Students Health — COMPLETO

**Decisión de diseño clave:** La tabla `student_health_records` (V10) tiene una sola columna `emergency_contact_name VARCHAR(200)`. Se optó por concatenar "firstName lastName" en esa columna en lugar de crear una migración adicional. La separación ocurre en el `@AfterMapping` del PersistenceMapper (split por primer espacio).

Persistence:
- `StudentHealthRecordEntity` — `emergency_contact_name` como columna única concatenada, `@PrePersist`/`@PreUpdate`
- `StudentHealthRecordPersistenceMapper` — `@AfterMapping` reconstruye `FullName` y `PhoneNumber` desde columnas simples
- `StudentHealthRecordRepositoryAdapter` — implementa el contrato completo del puerto con VOs tipados

Web:
- `HealthRecordController` — 2 endpoints REST, `@PreAuthorize` ADMIN/STAFF
- `HealthRecordWebDto` — clase contenedora `final` con todos los web DTOs del módulo
- `HealthRecordExceptionHandler` — ProblemDetail 404/422

**Endpoints disponibles:**

| Método | Path | Rol | Descripción |
|--------|------|-----|-------------|
| GET | `/api/admin/students/{studentId}/health` | ADMIN, STAFF | Obtener ficha médica |
| PATCH | `/api/admin/students/{studentId}/health` | ADMIN, STAFF | Actualizar ficha médica (campos null conservan valor) |

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
| `V11` | `document_types`, `student_records`, `record_documents` |
| `V12` | `parents`, `student_parents` |
| `V14` | `withdrawal_reasons`, `student_enrollments` |

### Convenciones de BD
- **PK**: `BINARY(16)` — `@Convert(UuidBinaryConverter.class)` en entidades, `@Id` como `UUID`
- **Timestamps**: `TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` + `@PrePersist`/`@PreUpdate`
- **Enums**: `VARCHAR` con `@Enumerated(EnumType.STRING)` — usar enums del Shared Kernel directamente
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

## 🔑 Credenciales de Prueba

| Rol | DNI | Password |
|-----|-----|----------|
| ADMIN | `00000001` | `Admin123!` |
| TEACHER | `12345678` | `Teacher123!` |
| STUDENT (con email) | `11223344` | `11223344Ipet132!` |
| STUDENT (sin email) | `87654321` | `87654321Ipet132!` |

---

## 🔒 Seguridad

- **JWT access token**: corta duración (configurable)
- **Refresh token**: larga duración, almacenado hasheado en BD, rotación en cada uso
- **Blacklist**: access tokens revocados hasta expiración
- **Roles**: `ADMIN`, `TEACHER`, `STUDENT`, `PARENT`, `STAFF`
- **Password inicial estudiante**: `{DNI}Ipet132!`
- **ProblemDetail**: respuestas de error estandarizadas (RFC 9457)

---

## 📊 Estado del Proyecto

### ✅ Implementado

- Auth, Geography, Academic completos
- **Refactor global**: todos los VOs migrados a `record` Java 17 con `of()` + `from()` estandarizados
- **`UuidBinaryConverter`** en `shared/infrastructure/` — conversión BINARY(16) ↔ UUID para todos los módulos
- **Students `personal/`** — las 3 capas completas:
    - Domain: modelo, VO (con `of(UUID)` corregido), repositorio, excepciones
    - Application: 5 use cases, 4 DTOs, application mapper
    - Infrastructure persistence: entity (UUID @Id, Gender directo, @PrePersist), JpaRepository, adapter, PersistenceMapper (@AfterMapping)
    - Infrastructure web: controller (5 endpoints, Spring Security integrado), web DTOs, web mapper, exception handler
- **Students `health/`** — las 3 capas completas:
    - Domain: `HealthRecordId` refactorizado a record Java 17, puerto corregido a VOs tipados, `HealthRecordNotFoundException`
    - Application: `HealthRecordResponse` (con flags calculados), `UpdateHealthRecordRequest` (PATCH semántico), mapper, 2 use cases
    - Infrastructure persistence: entity (emergency_contact_name concatenado, @PrePersist), JpaRepository, adapter, PersistenceMapper (@AfterMapping para FullName+PhoneNumber)
    - Infrastructure web: `HealthRecordWebDto` contenedora, mapper, controller (GET/PATCH), exception handler (ProblemDetail 404/422)
- **Students domain** de los otros 2 agregados completo (enrollment, records)
- Flyway V1–V7, V10–V14 ejecutados

### ⏳ Pendiente

- [ ] Application + Infrastructure `students/enrollment/` ← **próximo**
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
| **emergency_contact_name concatenado** | Schema V10 tiene columna única — se concatena "firstName lastName", se separa en @AfterMapping |
| **Records Java 17 para VOs** | Inmutabilidad nativa, equals/hashCode/toString sin boilerplate |
| **of() como factory principal** | Estándar del proyecto — `from()` como alias de compatibilidad |
| **UuidBinaryConverter en shared/** | Un solo converter para todos los BCs — no duplicar por módulo |
| **@Id como UUID + @Convert** | `JpaRepository<Entity, UUID>` transparente — más limpio que `byte[]` |
| **@PrePersist / @PreUpdate en entidades** | Garantizan timestamps nunca nulos independientemente del dominio |
| **Gender directo en entidades JPA** | Enum puro del Shared Kernel — elimina `GenderEntity` duplicado |
| **@AfterMapping para VOs compuestos** | `FullName` y `Address` necesitan múltiples columnas — `@Named` no funciona con múltiples parámetros en MapStruct |
| **Sin INSTANCE en mappers Spring** | `componentModel = "spring"` genera bean — `INSTANCE` es para uso sin Spring |
| **PersistenceMapper separado de ApplicationMapper** | Naming `*PersistenceMapper` distingue las 3 capas |
| **StudentWebDto clase contenedora** | Todos los web DTOs del módulo en un archivo — facilita navegación |
| **User implementa UserDetails directo** | No hay wrapper — cast via pattern matching Java 17 en `extractUserId()` |
| **extractUserId via instanceof User** | Seguro, explícito, lanza IllegalStateException con mensaje claro si falla |
| **ProblemDetail para errores** | RFC 9457, nativo en Spring 6 — estandarizado en toda la API |
| **SearchStudentsUseCase parámetros primitivos** | Testeable sin infraestructura web — `execute(dni, fullName, residencePlaceId)` |
| **DNI siempre 8 dígitos** | Consistente con `Dni.java` del Shared Kernel |
| **BINARY(16) para UUIDs** | Consistente en todo el proyecto |

---

**Última actualización**: Marzo 2026
**Versión**: 2.4.0
**Estado**: En desarrollo activo — Students personal ✅ + Students health ✅ COMPLETO