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

### 🎯 Características Principales

- **DNI como username**: Sistema adaptado a la realidad argentina
- **Email opcional**: Para estudiantes menores sin email propio
- **Token Rotation**: Máxima seguridad en refresh tokens
- **Multi-dispositivo**: Control de sesiones activas
- **Roles específicos**: ADMIN, TEACHER, STUDENT, PARENT, STAFF
- **Arquitectura escalable**: Preparada para migrar a microservicios
- **Folios automáticos**: Asignación automática desde el Registro de Calificaciones
- **Shared Kernel**: Value Objects reutilizados entre bounded contexts (DNI, Email, PhoneNumber)

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

Cada bounded context es un slice vertical completo:

```
shared/         → Shared Kernel (DNI, Email, PhoneNumber, IDs geográficos)
auth/           → Autenticación y autorización ✅
geography/      → Lugares geográficos (País, Provincia, Localidad) ✅
academic/       → Estructura académica (Años, Cursos, Materias) ✅
students/       → Gestión de estudiantes ⏳
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
- **MapStruct 1.5.5** - Mapeo de objetos entre capas
- **Lombok** - Reducción de boilerplate
- **SpringDoc OpenAPI 3** - Documentación Swagger/OpenAPI

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
│   │   └── Email.java
│   ├── geography/domain/valueobject/
│   │   ├── CountryId.java
│   │   ├── ProvinceId.java
│   │   └── PlaceId.java
│   └── domain/exception/
│       └── DomainException.java             # Base abstracta de excepciones
│
├── auth/                                    # BOUNDED CONTEXT: Autenticación ✅
│   ├── domain/
│   │   ├── model/
│   │   │   ├── User.java
│   │   │   ├── RefreshToken.java
│   │   │   └── BlacklistedToken.java
│   │   ├── valueobject/
│   │   │   └── UserId.java, HashedPassword.java, PlainPassword.java, RoleName.java...
│   │   ├── repository/
│   │   │   └── UserRepository.java, RefreshTokenRepository.java, BlacklistedTokenRepository.java
│   │   └── exception/
│   │       └── InvalidPasswordException.java, UserNotActiveException.java
│   ├── application/
│   │   ├── usecases/
│   │   │   ├── LoginUseCase.java
│   │   │   ├── RefreshTokenUseCase.java
│   │   │   ├── LogoutUseCase.java
│   │   │   ├── ChangePasswordUseCase.java
│   │   │   ├── GetUserProfileUseCase.java
│   │   │   ├── GetActiveSessionsUseCase.java
│   │   │   ├── RevokeSessionUseCase.java
│   │   │   └── admin/
│   │   │       ├── CreateStudentUseCase.java
│   │   │       └── CreateTeacherUseCase.java
│   │   ├── dto/
│   │   └── mappers/
│   │       └── AuthApplicationMapper.java
│   └── infrastructure/
│       ├── web/controller/
│       │   ├── AuthController.java
│       │   ├── AdminController.java
│       │   └── UsersController.java
│       ├── persistence/
│       │   ├── entity/
│       │   ├── repository/
│       │   └── mappers/
│       └── security/
│           ├── SecurityConfig.java
│           ├── JwtTokenProvider.java
│           ├── JwtAuthenticationFilter.java
│           └── CustomUserDetailsService.java
│
├── geography/                               # BOUNDED CONTEXT: Geografía ✅
│   ├── domain/
│   │   ├── model/
│   │   │   ├── Country.java
│   │   │   ├── Province.java
│   │   │   ├── Place.java
│   │   │   └── PlaceWithHierarchy.java
│   │   ├── valueobject/
│   │   │   ├── IsoCode.java, PhoneCode.java, ProvinceCode.java
│   │   │   ├── PostalCode.java, GeographicName.java, PlaceType.java
│   │   │   └── ids/ (CountryId, ProvinceId, PlaceId)
│   │   └── repository/
│   │       ├── CountryRepository.java
│   │       ├── ProvinceRepository.java
│   │       ├── PlaceRepository.java
│   │       └── GeographyQueryRepository.java
│   ├── application/
│   │   ├── usecases/                        # 10 use cases
│   │   ├── dto/
│   │   └── mappers/
│   └── infrastructure/
│       ├── persistence/ (entity, repository, adapter, mapper)
│       ├── web/controller/
│       │   ├── GeographyController.java
│       │   └── GeographyAdminController.java
│       └── seeder/
│           └── GeographyDataSeeder.java
│
├── academic/                                # BOUNDED CONTEXT: Académico ✅
│   ├── domain/
│   │   ├── model/
│   │   │   ├── AcademicYear.java
│   │   │   ├── Orientation.java
│   │   │   ├── GradeLevel.java
│   │   │   ├── Subject.java
│   │   │   ├── StudyPlan.java
│   │   │   ├── EvaluationPeriod.java
│   │   │   └── QualificationRegistry.java
│   │   ├── valueobject/
│   │   │   ├── Year.java, YearLevel.java, Division.java
│   │   │   ├── enums/ (AcademicYearStatus, Shift, RegistryStatus)
│   │   │   └── ids/ (AcademicYearId, OrientationId, GradeLevelId, SubjectId...)
│   │   ├── repository/                      # 7 interfaces (puertos)
│   │   ├── service/
│   │   │   ├── FolioAssignmentService.java  # ← CRÍTICO para Students
│   │   │   ├── RegistryNumberGenerator.java
│   │   │   ├── AcademicYearActivationService.java
│   │   │   ├── GradeLevelValidationService.java
│   │   │   └── StudyPlanManagementService.java
│   │   └── exception/                       # 20+ excepciones de dominio
│   ├── application/
│   │   ├── usecases/                        # 22 use cases
│   │   ├── dto/ (request/ + response/)
│   │   └── mapper/
│   │       └── AcademicApplicationMapper.java
│   └── infrastructure/
│       ├── persistence/ (entity, repository, adapter, mapper)
│       ├── web/
│       │   ├── controller/
│       │   │   ├── AcademicYearController.java
│       │   │   ├── OrientationController.java
│       │   │   ├── GradeLevelController.java
│       │   │   └── SubjectController.java
│       │   └── exception/
│       │       └── AcademicExceptionHandler.java
│       └── seeder/
│           └── AcademicDataSeeder.java
│
├── students/                                # BOUNDED CONTEXT: Estudiantes ⏳
│   ├── personal/                            # Agregado: StudentPersonalData
│   ├── health/                              # Agregado: StudentHealthRecord
│   ├── enrollment/                          # Agregado: StudentEnrollment
│   └── records/                             # Agregado: StudentRecord + RecordDocuments
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
    └── V7__extend_academic_module.sql
```

---

## 🗂️ Módulos Implementados

### ✅ Auth — Autenticación y Autorización

- Login con DNI + password
- Logout con blacklist de access token
- Refresh token con rotación automática
- Cambio de contraseña
- Gestión de sesiones activas por dispositivo
- Creación de estudiantes y profesores (Admin)
- Activación de cuenta de profesores

### ✅ Geography — Geografía Argentina

- Países, provincias y localidades con jerarquía completa
- Búsqueda con autocompletado
- Datos precargados: Argentina completa (1 país, 24 provincias, ~45 localidades)
- Endpoints públicos para autocompletado en formularios (sin auth requerida)

### ✅ Academic — Estructura Académica

**Gestión de Años Académicos**
- Crear, activar y cerrar años académicos
- Solo un año puede estar activo a la vez
- Al activar uno nuevo, el anterior se cierra automáticamente

**Gestión de Orientaciones**
- Técnico Electricista (ELEC) y Técnico Electromecánico (ELMEC)
- Solo disponibles desde 4° año (ciclo superior)
- Obligatorias para años 4°–7°

**Gestión de Cursos (GradeLevel)**
- Ciclo básico (1°–3°): sin orientación
- Ciclo superior (4°–7°): con orientación obligatoria
- 37 cursos activos precargados para 2024

**Gestión de Materias (Subject)**
- Materias comunes (todos los cursos)
- Materias específicas por orientación
- ~60 materias precargadas

**Registro de Calificaciones (QualificationRegistry)**
- Instrumento legal obligatorio por año académico
- Asignación automática de folios via `FolioAssignmentService`
- REG-2024-001 activo con 500 folios disponibles
- Limpieza automática de tokens vencidos (Schedulers)

---

## 🗄️ Base de Datos

### Migraciones Flyway

| Migración | Descripción |
|-----------|-------------|
| `V1` | Tabla `users` con UUID PK, DNI unique |
| `V2` | Tabla `blacklisted_tokens` |
| `V3` | Admin por defecto (solo dev) |
| `V4` | Tabla `refresh_tokens` |
| `V5` | Tablas geography (`countries`, `provinces`, `places`) |
| `V6` | Tablas academic core (`academic_years`, `orientations`, `grade_levels`, `subjects`, `qualification_registries`) |
| `V7` | Tablas academic extendidas (`study_plans`, `evaluation_periods`, `grades`) |

### Convenciones de BD
- **PK**: UUID (VARCHAR 36)
- **Timestamps**: `created_at`, `updated_at`, `expires_at` tipo DATETIME
- **Flags**: `is_active`, `is_current`, `is_revoked` tipo BOOLEAN
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
| GET | `/api/admin/academic-years/{id}` | ✅ | Año por ID |
| PUT | `/api/admin/academic-years/{id}/activate` | ✅ ADMIN | Activar año |
| PUT | `/api/admin/academic-years/{id}/close` | ✅ ADMIN | Cerrar año |
| POST | `/api/admin/orientations` | ✅ ADMIN | Crear orientación |
| GET | `/api/admin/orientations` | ✅ | Listar orientaciones |
| PUT | `/api/admin/orientations/{id}` | ✅ ADMIN | Actualizar orientación |
| PUT | `/api/admin/orientations/{id}/toggle-status` | ✅ ADMIN | Activar/Desactivar |
| POST | `/api/admin/grade-levels` | ✅ ADMIN | Crear curso |
| GET | `/api/admin/grade-levels` | ✅ | Listar cursos |
| PUT | `/api/admin/grade-levels/{id}/homeroom-teacher` | ✅ ADMIN | Asignar tutor |
| DELETE | `/api/admin/grade-levels/{id}` | ✅ ADMIN | Desactivar curso |
| POST | `/api/admin/subjects` | ✅ ADMIN | Crear materia |
| GET | `/api/admin/subjects` | ✅ | Listar materias |
| PUT | `/api/admin/subjects/{id}` | ✅ ADMIN | Actualizar materia |

**Total actual: ~35 endpoints REST documentados en Swagger**

---

## 🔒 Seguridad

- **JWT access token**: corta duración (configurable en `application.yml`)
- **Refresh token**: larga duración, almacenado en BD con hash, rotación en cada uso
- **Blacklist**: access tokens revocados almacenados hasta expiración
- **Spring Security 6**: `SecurityFilterChain` bean (no `WebSecurityConfigurerAdapter`)
- **Roles disponibles**: `ADMIN`, `TEACHER`, `STUDENT`, `PARENT`, `STAFF`
- **Schedulers**: limpieza periódica de refresh tokens y blacklist expirados

---

## 🧪 Testing

```bash
# Todos los tests
mvn clean verify

# Solo unit tests
mvn test -Dgroups="unit"

# Solo integration tests
mvn test -Dgroups="integration"

# Con coverage (JaCoCo)
mvn test jacoco:report
```

### Testing del API

```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"dni":"00000001","password":"Admin123!"}'

export TOKEN="<access_token>"

# Crear año académico
curl -X POST http://localhost:8080/api/admin/academic-years \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"year":2025,"startDate":"2025-03-01","endDate":"2025-12-20","setAsCurrent":true}'

# Buscar localidades
curl "http://localhost:8080/api/geography/places/search?q=Alta"
```

O bien usar **Swagger UI**: `http://localhost:8080/swagger-ui.html`

---

## 🔑 Credenciales de Prueba

Generadas automáticamente por `DataSeederConfig` en perfil `dev`:

| Rol | DNI | Password |
|-----|-----|----------|
| ADMIN | `00000001` | `Admin123!` |
| TEACHER | `12345678` | `Teacher123!` |
| STUDENT (con email) | `11223344` | `11223344Ipet132!` |
| STUDENT (sin email) | `87654321` | `87654321Ipet132!` |

> **Nota**: La contraseña inicial de estudiantes sigue el patrón `{DNI}Ipet132!`

---

## 📊 Estado del Proyecto

### ✅ Implementado (MVP + Fase 2)

**Auth Module**
- [x] Arquitectura hexagonal completa
- [x] Autenticación con DNI — JWT + Refresh Token con rotación
- [x] Gestión de sesiones activas por dispositivo
- [x] Blacklist de tokens — Schedulers de limpieza
- [x] Creación de estudiantes y profesores (Admin)
- [x] Activación de cuenta de profesores
- [x] Cambio de contraseña
- [x] GlobalExceptionHandler, MapStruct mappers, Flyway migrations

**Geography Module**
- [x] Países, provincias y localidades (jerarquía completa)
- [x] Búsqueda con autocompletado
- [x] Seeder con Argentina completa (24 provincias, ~45 localidades)
- [x] Endpoints públicos para formularios de alta

**Academic Module**
- [x] Gestión de años académicos con activación controlada
- [x] Orientaciones técnicas (Electricista, Electromecánico)
- [x] Cursos con validación de orientación por ciclo (1°–3° sin, 4°–7° con)
- [x] Materias comunes y específicas por orientación
- [x] Registro de Calificaciones con asignación automática de folios
- [x] 5 Domain Services (FolioAssignment, RegistryNumber, AcademicYearActivation, etc.)
- [x] 22 Use Cases — 22 endpoints REST
- [x] Seeder completo (2 años, 2 orientaciones, 37 cursos, ~60 materias)
- [x] Swagger/OpenAPI completo
- [x] Validaciones Jakarta en todos los DTOs

### ⏳ Pendiente — Próximos Módulos

**Students Module** (próximo)
- [ ] Agregado: `StudentPersonalData` (identidad civil, domicilio, fotos de DNI)
- [ ] Agregado: `StudentHealthRecord` (ficha médica, obra social, alergias)
- [ ] Agregado: `StudentEnrollment` (matrícula por ciclo lectivo, baja)
- [ ] Agregado: `StudentRecord + RecordDocuments` (legajo digital con documentación)
- [ ] Gestión de padres/tutores con su propio `User`
- [ ] `RecordNumberGenerator` — formato `LEG-{año}-{secuencia}`
- [ ] Flujo transaccional completo de alta de estudiante (integra Auth + Academic + Geography)

**Teachers Module**
- [ ] Asignación de profesores a cursos
- [ ] Homeroom teachers (tutores de curso)

**Calificaciones**
- [ ] Notas por período y materia
- [ ] Integración con Qualification Registry
- [ ] Promedio final

**Infraestructura**
- [ ] Rate limiting
- [ ] Auditoría completa
- [ ] Métricas y monitoring (Actuator)
- [ ] Email Service (envío real)
- [ ] Búsqueda y paginación avanzada

---

## 📝 Decisiones Arquitectónicas

| Decisión | Razón |
|----------|-------|
| **DNI como username** | Identificador universal en el sistema escolar argentino |
| **Email opcional** | Estudiantes menores no tienen email propio |
| **UUID como PK** | Preparado para microservicios, evita IDs predecibles |
| **Roles como String** | Simplicidad MVP — roles fijos |
| **Token Rotation** | Seguridad OWASP en refresh tokens |
| **Records para DTOs** | Inmutabilidad + menos boilerplate (Java 17) |
| **MapStruct 3 capas** | Type-safe en compile-time — persistence, application y web mapper separados |
| **Flyway migraciones** | Control de versión de esquema — nunca `ddl-auto: create` |
| **Shared Kernel** | DNI, Email, PhoneNumber, IDs geográficos reutilizados entre bounded contexts |
| **Students en 4 agregados** | Evitar God Table — separación por responsabilidad (personal, salud, matrícula, legajo) |
| **Folio automático** | `FolioAssignmentService` transaccional garantiza unicidad del folio |
| **Password inicial estudiante** | Patrón `{DNI}Ipet132!` — simple y conocido por el admin |
| **Endpoints Geography públicos** | Los formularios de alta necesitan autocompletado sin estar autenticado |
| **Ciclo básico sin orientación** | 1°–3° son comunes; orientación obligatoria solo en ciclo superior (4°–7°) |

### Patrones en uso

- Repository Pattern — Puerto (interface en domain) + Adaptador (impl en infrastructure)
- Factory Pattern — Creación de entidades con validación en el constructor
- Value Object Pattern — Tipos primitivos encapsulados con validación
- Port & Adapters — Arquitectura hexagonal completa
- Domain Services — Lógica que no pertenece a un solo agregado
- Domain Events — Preparado en estructura (no implementado aún)
- CQRS — Preparado (use cases separados por lectura/escritura)

---

## 📦 Dependencias Principales

```xml
<!-- Spring Boot -->
<dependency>spring-boot-starter-web</dependency>
<dependency>spring-boot-starter-security</dependency>
<dependency>spring-boot-starter-data-jpa</dependency>
<dependency>spring-boot-starter-validation</dependency>

<!-- JWT -->
<dependency>io.jsonwebtoken:jjwt-api:0.12.3</dependency>

<!-- MapStruct -->
<dependency>org.mapstruct:mapstruct:1.5.5.Final</dependency>

<!-- Lombok -->
<dependency>org.projectlombok:lombok</dependency>

<!-- MySQL -->
<dependency>com.mysql:mysql-connector-j</dependency>

<!-- Flyway -->
<dependency>org.flywaydb:flyway-mysql</dependency>

<!-- OpenAPI / Swagger -->
<dependency>org.springdoc:springdoc-openapi-starter-webmvc-ui</dependency>
```

---

**Última actualización**: Marzo 2025
**Versión**: 2.0.0
**Estado**: En desarrollo activo — MVP Auth + Geography + Academic completados
