# 🎓 Sistema de Gestión Escolar IPET 132

Sistema integral de gestión escolar desarrollado con **Spring Boot**, siguiendo principios de **Arquitectura Hexagonal**, **Vertical Slicing** y **Screaming Architecture**.

## 📋 Tabla de Contenidos

- [Descripción del Proyecto](#descripción-del-proyecto)
- [Arquitectura](#arquitectura)
- [Tecnologías](#tecnologías)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Módulos Implementados](#módulos-implementados)
- [Base de Datos](#base-de-datos)
- [Configuración](#configuración)
- [Endpoints API](#endpoints-api)
- [Seguridad](#seguridad)
- [Testing](#testing)
- [Credenciales de Prueba](#credenciales-de-prueba)

---

## 📖 Descripción del Proyecto

Sistema de gestión escolar para el **IPET 132** (Argentina) que permite:

- ✅ Autenticación y autorización con JWT
- ✅ Gestión de estudiantes, profesores y administradores
- ✅ Login con **DNI** como identificador principal
- ✅ Gestión de sesiones y tokens de refresco
- ✅ Sistema de roles y permisos
- ✅ Control de sesiones activas por dispositivo
- ✅ Gestión de geografía argentina (países, provincias, localidades)
- ✅ Gestión académica completa (años, orientaciones, cursos, materias)
- ✅ Registro de calificaciones con asignación automática de folios
- ✅ Documentación interactiva con Swagger/OpenAPI
- ⏳ Módulo de estudiantes (en construcción — domain layer personal completado)

### 🎯 Características Principales

- **DNI como username**: Sistema adaptado a la realidad argentina
- **CUIL validado**: Dígito verificador ANSES/AFIP con compatibilidad con DNI embebido
- **Email opcional**: Para estudiantes menores sin email propio
- **Token Rotation**: Máxima seguridad en refresh tokens
- **Multi-dispositivo**: Control de sesiones activas
- **Roles específicos**: ADMIN, TEACHER, STUDENT, PARENT, STAFF
- **Arquitectura escalable**: Preparada para migrar a microservicios
- **Folios automáticos**: Asignación automática desde el Registro de Calificaciones
- **Shared Kernel**: Value Objects reutilizados entre bounded contexts (DNI, CUIL, Email, PhoneNumber, Address, IDs geográficos)

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
│         │                 │                  │          │
├─────────┼─────────────────┼──────────────────┼──────────┤
│         │   APPLICATION LAYER                │          │
│         │  ┌────────────────────────────────┐│          │
│         └──│  Use Cases (Business Logic)    ││          │
│            │  - CreateStudent               ││          │
│            │  - Login                       ││          │
│            │  - RefreshToken                ││          │
│            └────────────┬───────────────────┘│          │
│                         │                               │
├─────────────────────────┼───────────────────────────────┤
│         DOMAIN LAYER (Core Business)                    │
│  ┌──────────────────────┴──────────────────────────┐   │
│  │  Entities, Value Objects, Domain Services       │   │
│  │  Repository Interfaces (Ports)                  │   │
│  └─────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
```

### Vertical Slicing (Bounded Contexts)

```
shared/         → Shared Kernel (DNI, CUIL, Email, PhoneNumber, Address, IDs geográficos)
auth/           → Autenticación y autorización ✅
geography/      → Lugares geográficos (País, Provincia, Localidad) ✅
academic/       → Estructura académica (Años, Cursos, Materias) ✅
students/       → Gestión de estudiantes ⏳ (domain/personal ✅)
teachers/       → Gestión de profesores ⏳
```

---

## 💻 Tecnologías

### Backend
- **Java 17**
- **Spring Boot 3.2.x**
- **Spring Security 6**
- **Spring Data JPA**
- **MySQL 8**
- **JWT (jjwt 0.12.x)**
- **MapStruct 1.5.5** — Mapeo de objetos entre capas
- **Lombok** — Reducción de boilerplate
- **SpringDoc OpenAPI 3** — Documentación Swagger/OpenAPI

### Testing
- **JUnit 5**
- **Mockito**
- **Spring Boot Test**
- **H2 Database** (para tests)
- **AssertJ**

### Tools
- **Maven**
- **Flyway** (migraciones de BD)
- **Postman / Swagger UI** (testing de API)

---

## 📁 Estructura del Proyecto

```
src/main/java/org/school/management/
│
├── shared/                                  # Shared Kernel
│   ├── person/domain/valueobject/
│   │   ├── Dni.java                         # ✅ DNI argentino (8 dígitos)
│   │   ├── FullName.java
│   │   ├── Gender.java
│   │   ├── Nationality.java
│   │   ├── PhoneNumber.java
│   │   ├── Email.java
│   │   ├── Cuil.java                        # ✅ CUIL con validación dígito verificador ANSES/AFIP
│   │   ├── CuilType.java                    # ✅ Enum: MALE_ARGENTINEAN, FEMALE_ARGENTINEAN...
│   │   └── Address.java                     # ✅ Domicilio postal (street, number, PlaceId, CP...)
│   ├── geography/domain/valueobject/
│   │   ├── CountryId.java
│   │   ├── ProvinceId.java
│   │   └── PlaceId.java
│   └── domain/exception/
│       └── DomainException.java
│
├── auth/                                    # BOUNDED CONTEXT: Autenticación ✅
│   └── ...                                  # (sin cambios)
│
├── geography/                               # BOUNDED CONTEXT: Geografía ✅
│   └── ...                                  # (sin cambios)
│
├── academic/                                # BOUNDED CONTEXT: Académico ✅
│   └── ...                                  # (sin cambios)
│
├── students/                                # BOUNDED CONTEXT: Estudiantes ⏳
│   ├── personal/                            # Agregado: StudentPersonalData
│   │   └── domain/                          # ✅ COMPLETO
│   │       ├── model/
│   │       │   └── StudentPersonalData.java # @Builder+@Getter, factory method create()
│   │       ├── valueobject/
│   │       │   └── StudentPersonalDataId.java # record UUID
│   │       ├── repository/
│   │       │   └── StudentPersonalDataRepository.java  # Puerto
│   │       └── exception/
│   │           ├── StudentNotFoundException.java        # + factory methods byId, byDni
│   │           ├── StudentAlreadyExistsException.java   # + factory methods withDni, withCuil
│   │           └── InvalidStudentDataException.java
│   ├── health/                              # Agregado: StudentHealthRecord ⏳
│   ├── enrollment/                          # Agregado: StudentEnrollment ⏳
│   └── records/                             # Agregado: StudentRecord + RecordDocuments ⏳
│
└── SchoolManagementApplication.java

src/main/resources/
├── application.yml
├── application-dev.yml
├── application-prod.yml
└── db/migration/
    ├── V1__Create_users_table.sql
    ├── V2__Create_blacklisted_tokens_table.sql
    ├── V3__Insert_default_admin.sql
    ├── V4__Create_refresh_tokens_table.sql
    ├── V5__create_geography_tables.sql
    ├── V6__create_academic_tables.sql
    ├── V7__extend_academic_module.sql
    ├── V10__create_students_tables.sql      # student_personal_data, student_health_records
    ├── V11__create_records_tables.sql       # document_types, student_records, record_documents
    ├── V12__create_parents_tables.sql       # parents, student_parents
    └── V14__create_enrollments_table.sql    # withdrawal_reasons, student_enrollments
```

---

## 🗂️ Módulos Implementados

### ✅ Auth — Autenticación y Autorización
*(sin cambios)*

### ✅ Geography — Geografía Argentina
*(sin cambios)*

### ✅ Academic — Estructura Académica
*(sin cambios)*

### ⏳ Students — Gestión de Estudiantes

**Domain Layer — `personal/` ✅ Completo**

- `StudentPersonalData` — agregado principal con validaciones de dominio, métodos de negocio (`calculateAge`, `isAdult`, `isEligibleForSecondarySchool`, `updateContactInfo`, `updateAddress`)
- `StudentPersonalDataId` — Value Object UUID como `record`
- `StudentPersonalDataRepository` — puerto con `save`, `findByStudentId`, `findByDni`, `findByFullNameContaining`, `findByResidencePlaceId`, `existsByDni`, `existsByCuil`, `count`
- Excepciones: `StudentNotFoundException` (+ `byId`, `byDni`), `StudentAlreadyExistsException` (+ `withDni`, `withCuil`), `InvalidStudentDataException`

**Shared Kernel ampliado (para Students y futuros módulos)**

- `Cuil` — validación completa ANSES/AFIP con dígito verificador, `extractDni()`, `formatted()`
- `CuilType` — enum con display names en español
- `Address` — domicilio encapsulado con `PlaceId`, normalización de calle, `toStringFormatted(localityName)`

**Próximo: Application Layer `personal/`**
- `CreateStudentRequest.java` — DTO con Jakarta
- `StudentPersonalDataResponse.java` — DTO response
- `StudentPersonalDataApplicationMapper.java` — MapStruct

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
- **PK**: `BINARY(16)` (UUID binario) — los mappers de persistencia incluyen `bytesToUuid` / `uuidToBytes`
- **Timestamps**: `TIMESTAMP` con `DEFAULT CURRENT_TIMESTAMP` y `ON UPDATE CURRENT_TIMESTAMP`
- **Flags**: `is_active`, `is_current`, `is_mandatory`, `requires_documentation` tipo `BOOLEAN`
- **Nunca** modificar migraciones ya ejecutadas — siempre crear `V{n+1}`

---

## ⚙️ Configuración

### Prerequisitos

- Java 17+
- Maven 3.8+
- MySQL 8+

### Instalación

```bash
# 1. Clonar repositorio
git clone <repository-url>
cd school-management

# 2. Crear base de datos
mysql -u root -p
CREATE DATABASE ipet132_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 3. Configurar application-dev.yml con tus credenciales de BD

# 4. Instalar dependencias y compilar
mvn clean install

# 5. Ejecutar en perfil dev (carga seeders automáticamente)
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Disponible en: http://localhost:8080
# Swagger UI:    http://localhost:8080/swagger-ui.html
# OpenAPI spec:  http://localhost:8080/api-docs
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
| POST | `/api/admin/students` | ✅ ADMIN | Crear estudiante |
| POST | `/api/admin/teachers` | ✅ ADMIN | Crear profesor |

### Geography
| Método | Endpoint | Auth | Descripción |
|--------|----------|------|-------------|
| GET | `/api/geography/countries` | ❌ | Listar países |
| GET | `/api/geography/countries/{iso}` | ❌ | País por ISO (ej: ARG) |
| GET | `/api/geography/countries/{id}/provinces` | ❌ | Provincias del país |
| GET | `/api/geography/provinces/search?q=` | ❌ | Buscar provincias |
| GET | `/api/geography/provinces/{id}/places` | ❌ | Lugares de la provincia |
| GET | `/api/geography/places/{id}` | ❌ | Lugar por ID |
| GET | `/api/geography/places/search?q=` | ❌ | Buscar lugares |
| GET | `/api/geography/search?q=` | ❌ | Búsqueda global |
| POST | `/api/admin/geography/places` | ✅ ADMIN | Crear localidad |
| GET | `/api/geography/statistics` | ❌ | Estadísticas |

### Academic
| Método | Endpoint | Auth | Descripción |
|--------|----------|------|-------------|
| POST | `/api/admin/academic-years` | ✅ ADMIN | Crear año académico |
| GET | `/api/admin/academic-years` | ✅ | Listar años |
| GET | `/api/admin/academic-years/current` | ✅ | Año actual |
| PUT | `/api/admin/academic-years/{id}/activate` | ✅ ADMIN | Activar año |
| PUT | `/api/admin/academic-years/{id}/close` | ✅ ADMIN | Cerrar año |
| POST | `/api/admin/orientations` | ✅ ADMIN | Crear orientación |
| GET | `/api/admin/orientations` | ✅ | Listar orientaciones |
| POST | `/api/admin/grade-levels` | ✅ ADMIN | Crear curso |
| GET | `/api/admin/grade-levels` | ✅ | Listar cursos |
| POST | `/api/admin/subjects` | ✅ ADMIN | Crear materia |
| GET | `/api/admin/subjects` | ✅ | Listar materias |

**Total actual: ~35 endpoints REST documentados en Swagger**

---

## 🔒 Seguridad

- **JWT access token**: corta duración (configurable en `application.yml`)
- **Refresh token**: larga duración, almacenado en BD con hash, rotación en cada uso
- **Blacklist**: access tokens revocados almacenados hasta expiración
- **Spring Security 6**: `SecurityFilterChain` bean
- **Roles disponibles**: `ADMIN`, `TEACHER`, `STUDENT`, `PARENT`, `STAFF`
- **Schedulers**: limpieza periódica de refresh tokens y blacklist expirados

---

## 🧪 Testing

```bash
mvn clean verify
mvn test -Dgroups="unit"
mvn test -Dgroups="integration"
mvn test jacoco:report
```

---

## 🔑 Credenciales de Prueba

| Rol | DNI | Password |
|-----|-----|----------|
| ADMIN | `00000001` | `Admin123!` |
| TEACHER | `12345678` | `Teacher123!` |
| STUDENT (con email) | `11223344` | `11223344Ipet132!` |
| STUDENT (sin email) | `87654321` | `87654321Ipet132!` |

> La contraseña inicial de estudiantes sigue el patrón `{DNI}Ipet132!`

---

## 📊 Estado del Proyecto

### ✅ Implementado

- Auth, Geography, Academic completos (ver secciones anteriores)
- **Shared Kernel ampliado**: `Cuil` + `CuilType` + `Address`
- **Students `personal/` domain layer**: `StudentPersonalData`, `StudentPersonalDataId`, `StudentPersonalDataRepository`, excepciones con factory methods
- Flyway V1–V7, V10–V14 ejecutados

### ⏳ En construcción — Students Module

- [ ] Application layer `personal/` — DTOs, mapper, use cases ← **próximo**
- [ ] Infrastructure layer `personal/` — JPA entity, adapter, controller
- [ ] Agregado `health/` — StudentHealthRecord
- [ ] Agregado `records/` — StudentRecord + RecordDocuments
- [ ] Agregado `enrollment/` — StudentEnrollment
- [ ] Parents + StudentParents
- [ ] Flujo transaccional completo (15 pasos)

### ⏳ Pendiente — Otros Módulos

- Teachers — asignación a cursos
- Calificaciones por período y promedio final
- Rate limiting, auditoría, métricas, email service

---

## 📝 Decisiones Arquitectónicas Clave

| Decisión | Razón |
|----------|-------|
| **DNI como username** | Identificador universal en el sistema escolar argentino |
| **CUIL con validación completa** | Algoritmo ANSES/AFIP — dígito verificador + compatibilidad DNI |
| **BINARY(16) para UUIDs** | Consistente en todo el proyecto — mappers incluyen conversión bytes↔UUID |
| **Address en Shared Kernel con PlaceId** | Domicilio completo reutilizable — students, parents, teachers |
| **@Builder para modelos de dominio complejos** | Ergonomía con +15 campos — `StudentPersonalData.create(builder)` agrega validaciones |
| **Excepciones con factory methods** | Constructor genérico + `byId()`, `byDni()` — flexibilidad y consistencia |
| **Sin delete físico de estudiantes** | Integridad referencial — baja lógica via StudentEnrollment |
| **Students en 4 agregados** | Evitar God Table — separación por responsabilidad |
| **Folio automático** | `FolioAssignmentService` transaccional garantiza unicidad |
| **UUID como PK** | Preparado para microservicios, evita IDs predecibles |
| **MapStruct 3 capas** | Type-safe en compile-time — persistence, application y web mapper separados |
| **Flyway migraciones** | Control de versión de esquema — nunca `ddl-auto: create` |

---

**Última actualización**: Marzo 2026
**Versión**: 2.1.0
**Estado**: En desarrollo activo — MVP Auth + Geography + Academic completados | Students domain/personal en progreso
