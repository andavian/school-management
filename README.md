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

### 🎯 Características Principales

- **DNI como username**: Sistema adaptado a la realidad argentina
- **Email opcional**: Para estudiantes menores sin email propio
- **Token Rotation**: Máxima seguridad en refresh tokens
- **Multi-dispositivo**: Control de sesiones activas
- **Roles específicos**: ADMIN, TEACHER, STUDENT, PARENT, STAFF

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
│         DOMAIN LAYER (Core Business)        │          │
│  ┌──────────────────────┴──────────────────────────┐   │
│  │  Entities: User, RefreshToken, BlacklistedToken │   │
│  │  Value Objects: DNI, Email, Password, UserId    │   │
│  │  Domain Services, Domain Events                 │   │
│  │  Repository Interfaces (Ports)                  │   │
│  └─────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
```

### Vertical Slicing

Cada bounded context es un slice vertical completo:

```
auth/           → Autenticación y autorización
├── domain/
├── application/
└── infrastructure/

students/       → Gestión de estudiantes (futuro)
teachers/       → Gestión de profesores (futuro)
courses/        → Gestión de cursos (futuro)
grades/         → Gestión de calificaciones (futuro)
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
- **MapStruct 1.5.5** - Mapeo de objetos
- **Lombok** - Reducción de boilerplate

### Testing
- **JUnit 5**
- **Mockito**
- **Spring Boot Test**
- **H2 Database** (para tests)

### Tools
- **Maven**
- **Flyway** (migraciones de BD)
- **Postman** (testing de API)

---

## 📁 Estructura del Proyecto

```
src/main/java/org/school/management/
│
├── auth/                                    # BOUNDED CONTEXT: Autenticación
│   ├── domain/                             # Capa de Dominio (Core)
│   │   ├── model/
│   │   │   ├── User.java                   # Entidad principal (implements UserDetails)
│   │   │   ├── RefreshToken.java           # Entidad para refresh tokens
│   │   │   └── BlacklistedToken.java       # Tokens revocados
│   │   ├── valueobject/
│   │   │   ├── UserId.java                 # Value Object
│   │   │   ├── HashedPassword.java
│   │   │   ├── PlainPassword.java
│   │   │   ├── RoleName.java
│   │   │   ├── RefreshTokenId.java
│   │   │   └── BlacklistedTokenId.java
│   │   ├── repository/                     # Puertos (Interfaces)
│   │   │   ├── UserRepository.java
│   │   │   ├── RefreshTokenRepository.java
│   │   │   └── BlacklistedTokenRepository.java
│   │   └── exception/
│   │       ├── InvalidPasswordException.java
│   │       └── UserNotActiveException.java
│   │
│   ├── application/                        # Casos de Uso
│   │   ├── usecases/
│   │   │   ├── LoginUseCase.java           # ✅ Implementado
│   │   │   ├── RefreshTokenUseCase.java    # ✅ Implementado
│   │   │   ├── LogoutUseCase.java          # ✅ Implementado
│   │   │   ├── ChangePasswordUseCase.java  # ✅ Implementado
│   │   │   ├── GetUserProfileUseCase.java  # ✅ Implementado
│   │   │   ├── ActivateTeacherAccountUseCase.java
│   │   │   ├── GetActiveSessionsUseCase.java
│   │   │   ├── RevokeSessionUseCase.java
│   │   │   ├── RevokeAllUserTokensUseCase.java
│   │   │   ├── StoreRefreshTokenUseCase.java
│   │   │   ├── BlacklistTokenUseCase.java
│   │   │   └── admin/
│   │   │       ├── CreateStudentUseCase.java   # ✅ Implementado
│   │   │       └── CreateTeacherUseCase.java   # ✅ Implementado
│   │   ├── dto/                            # Application DTOs (Records)
│   │   │   ├── LoginRequest.java
│   │   │   ├── LoginResponse.java
│   │   │   ├── UserResponse.java
│   │   │   ├── CreateStudentRequest.java
│   │   │   ├── CreateTeacherRequest.java
│   │   │   ├── RefreshTokenRequest.java
│   │   │   ├── RefreshTokenResponse.java
│   │   │   └── ...
│   │   └── mappers/
│   │       └── AuthApplicationMapper.java  # MapStruct mapper
│   │
│   └── infrastructure/                     # Adaptadores
│       ├── web/                           # Adaptador REST
│       │   ├── controller/
│       │   │   ├── AuthController.java     # ✅ Implementado
│       │   │   ├── AdminController.java    # ✅ Implementado
│       │   │   └── UsersController.java
│       │   ├── dto/                       # API DTOs (con validaciones)
│       │   │   ├── LoginApiRequest.java
│       │   │   ├── LoginApiResponse.java
│       │   │   ├── CreateStudentApiRequest.java
│       │   │   └── ...
│       │   ├── mappers/
│       │   │   └── AuthWebMapper.java     # MapStruct mapper
│       │   └── exception/
│       │       └── GlobalExceptionHandler.java  # ✅ Implementado
│       │
│       ├── persistence/                   # Adaptador de Persistencia
│       │   ├── entity/
│       │   │   ├── UserEntity.java
│       │   │   ├── RefreshTokenEntity.java
│       │   │   └── BlacklistedTokenEntity.java
│       │   ├── repository/
│       │   │   ├── UserJpaRepository.java
│       │   │   ├── UserRepositoryImpl.java     # ✅ Implementado
│       │   │   ├── RefreshTokenJpaRepository.java
│       │   │   ├── RefreshTokenRepositoryImpl.java
│       │   │   └── ...
│       │   └── mappers/
│       │       └── AuthPersistenceMapper.java  # MapStruct mapper
│       │
│       ├── security/                      # Configuración de Seguridad
│       │   ├── SecurityConfig.java        # ✅ Configurado
│       │   ├── JwtTokenProvider.java      # ✅ Implementado
│       │   ├── JwtAuthenticationFilter.java  # ✅ Implementado
│       │   ├── CustomUserDetailsService.java # ✅ Implementado
│       │   └── config/
│       │       ├── AuthenticationConfig.java
│       │       └── PasswordEncoderConfig.java
│       │
│       ├── scheduling/                    # Tareas programadas
│       │   ├── RefreshTokenCleanupScheduler.java
│       │   └── TokenBlacklistCleanupScheduler.java
│       │
│       └── config/
│           └── DataSeederConfig.java      # Seed de datos para DEV
│
├── shared/                                # Shared Kernel
│   └── domain/
│       └── valueobjects/
│           ├── Email.java                 # ✅ Implementado
│           ├── DNI.java                   # ✅ Implementado
│           └── PhoneNumber.java           # ✅ Implementado
│
└── SchoolManagementApplication.java       # Main class

src/main/resources/
├── application.yml                        # ✅ Configurado
├── application-dev.yml
├── application-prod.yml
└── db/migration/                          # Flyway migrations
    ├── V1__Create_users_table.sql
    ├── V2__Create_blacklisted_tokens_table.sql
    ├── V3__Insert_default_admin.sql
    └── V4__Create_refresh_tokens_table.sql
```

---

## 🗄️ Base de Datos

### Diagrama ER (Implementado)

```
┌─────────────────────────────┐
│          users              │
├─────────────────────────────┤
│ PK user_id (UUID)           │
│ UK dni (VARCHAR)            │◄────┐
│    email (VARCHAR) NULL     │     │
│    password (VARCHAR)       │     │
│    roles (VARCHAR)          │     │
│    is_active (BOOLEAN)      │     │
│    created_at (DATETIME)    │     │
│    last_login_at (DATETIME) │     │
│    updated_at (DATETIME)    │     │
└─────────────────────────────┘     │
                                    │ FK
┌─────────────────────────────┐     │
│    refresh_tokens           │     │
├─────────────────────────────┤     │
│ PK id (UUID)                │     │
│ FK user_id (UUID)           │─────┘
│ UK token_hash (VARCHAR)     │
│    created_at (DATETIME)    │
│    expires_at (DATETIME)    │
│    is_revoked (BOOLEAN)     │
│    revoked_at (DATETIME)    │
│    user_agent (VARCHAR)     │
│    ip_address (VARCHAR)     │
└─────────────────────────────┘

┌─────────────────────────────┐
│   blacklisted_tokens        │
├─────────────────────────────┤
│ PK id (UUID)                │
│ UK token_hash (VARCHAR)     │
│    token_type (VARCHAR)     │
│    blacklisted_at (DATETIME)│
│    expires_at (DATETIME)    │
│    reason (VARCHAR)         │
│    user_email (VARCHAR)     │
└─────────────────────────────┘
```

### Scripts SQL Disponibles

```sql
-- V1: Tabla de usuarios
CREATE TABLE users (
    user_id VARCHAR(36) PRIMARY KEY,
    dni VARCHAR(8) UNIQUE NOT NULL,
    email VARCHAR(254) NULL,
    password VARCHAR(255) NOT NULL,
    roles VARCHAR(500) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME NOT NULL,
    last_login_at DATETIME,
    updated_at DATETIME NOT NULL
);

-- V2: Tabla de tokens en blacklist
-- V3: Usuario admin por defecto
-- V4: Tabla de refresh tokens
```

---

## ⚙️ Configuración

### application.yml

```yaml
spring:
  application:
    name: school-management
  profiles:
    active: dev

app:
  cors:
    allowed-origins:
      - "http://localhost:3000"
      - "http://localhost:5173"
    allowed-methods:
      - GET
      - POST
      - PUT
      - PATCH
      - DELETE
      - OPTIONS
    allow-credentials: true

  security:
    jwt:
      secret-key: "my-super-secret-key-must-be-256-bits"
      access-token-expiration: 3600      # 1 hora
      refresh-token-expiration: 604800   # 7 días
      issuer: "ipet132-school-system"
    refresh-token:
      max-active-per-user: 5  # Máximo 5 dispositivos

  school:
    name: "IPET 132"
    institutional-email-domain: "ipet132.edu.ar"

logging:
  level:
    org.school.management: DEBUG
```

### Variables de Entorno (Producción)

```bash
JWT_SECRET_KEY=<secret-256-bits>
DB_HOST=<database-host>
DB_USER=<database-user>
DB_PASSWORD=<database-password>
FRONTEND_DOMAIN=<frontend-domain>
```

---

## 🔌 Endpoints API

### Auth Endpoints (Públicos)

```http
POST   /api/auth/login              # Login con DNI
POST   /api/auth/refresh-token      # Renovar tokens
POST   /api/auth/activate-account   # Activar cuenta de profesor
```

### Auth Endpoints (Autenticados)

```http
GET    /api/auth/profile            # Obtener mi perfil
PUT    /api/auth/change-password    # Cambiar contraseña
POST   /api/auth/logout             # Cerrar sesión
GET    /api/auth/sessions           # Ver sesiones activas
DELETE /api/auth/sessions/{id}      # Cerrar sesión específica
DELETE /api/auth/sessions           # Cerrar todas las sesiones
```

### Admin Endpoints (Solo ADMIN)

```http
POST   /api/admin/students          # Crear estudiante
POST   /api/admin/teachers          # Crear profesor
GET    /api/admin/students          # Listar estudiantes (TODO)
GET    /api/admin/teachers          # Listar profesores (TODO)
PUT    /api/admin/users/{id}/activate    # Activar usuario (TODO)
PUT    /api/admin/users/{id}/deactivate  # Desactivar usuario (TODO)
```

### Ejemplos de Requests

#### Login
```bash
POST /api/auth/login
Content-Type: application/json

{
  "dni": "12345678",
  "password": "12345678Ipet132!",
  "rememberMe": false
}

# Response 200 OK
{
  "accessToken": "eyJhbGc...",
  "refreshToken": "eyJhbGc...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "user": {
    "userId": "uuid-123",
    "dni": "12345678",
    "email": "student@mail.com",
    "roles": ["STUDENT"],
    "isActive": true
  }
}
```

#### Crear Estudiante (Admin)
```bash
POST /api/admin/students
Authorization: Bearer <admin-token>
Content-Type: application/json

{
  "dni": "87654321",
  "firstName": "Juan",
  "lastName": "Pérez",
  "email": null,
  "phoneNumber": "+5492612345678",
  "parentEmail": "padre@mail.com",
  "grade": "3",
  "division": "A"
}

# Response 201 Created
{
  "userId": "uuid-456",
  "dni": "87654321",
  "email": null,
  "initialPassword": "87654321Ipet132!"
}
```

#### Refresh Token
```bash
POST /api/auth/refresh-token
Content-Type: application/json

{
  "refreshToken": "eyJhbGc..."
}

# Response 200 OK
{
  "accessToken": "eyJhbGc... (NUEVO)",
  "refreshToken": "eyJhbGc... (NUEVO)",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

---

## 🔐 Seguridad

### Estrategias Implementadas

#### 1. **JWT con Token Rotation**
- Access Token: 1 hora de validez
- Refresh Token: 7 días de validez
- **Rotation**: Cada refresh genera tokens nuevos y revoca los anteriores
- Refresh tokens almacenados con hash SHA-256

#### 2. **Blacklist de Tokens**
- Tokens revocados al hacer logout
- Cleanup automático cada 6 horas
- Validación en cada request

#### 3. **Control de Sesiones**
- Máximo 5 dispositivos activos por usuario
- Metadata: UserAgent, IP Address
- Revocación individual o masiva de sesiones

#### 4. **Validaciones**
- Bean Validation en DTOs de API
- Domain validation en Value Objects
- Password strength: min 8 chars, mayúscula, minúscula, número, especial

#### 5. **DNI como Username**
- Identificador único e inmutable
- Validación: 7-8 dígitos numéricos
- Email opcional (para menores)

---

## 🧪 Testing

### Tests Implementados

```bash
# Unit Tests
src/test/java/
├── auth/application/usecases/
│   ├── LoginUseCaseTest.java
│   ├── RefreshTokenUseCaseTest.java
│   └── CreateStudentUseCaseTest.java
├── auth/infrastructure/persistence/
│   └── UserRepositoryImplTest.java
└── auth/infrastructure/web/
    └── AuthControllerTest.java

# Integration Tests
src/test/java/
└── auth/infrastructure/
    ├── AuthControllerIntegrationTest.java
    └── UserRepositoryImplIntegrationTest.java
```

### Ejecutar Tests

```bash
# Todos los tests
mvn test

# Solo unit tests
mvn test -Dgroups="unit"

# Solo integration tests
mvn test -Dgroups="integration"

# Con coverage
mvn test jacoco:report
```

---

## 🔑 Credenciales de Prueba

El sistema incluye datos de prueba en modo **dev** (auto-generados en startup):

### Admin
```
DNI: 00000001
Password: Admin123!
Roles: ADMIN
```

### Profesor
```
DNI: 12345678
Password: Teacher123!
Roles: TEACHER
Email: juan.perez@ipet132.edu.ar
```

### Estudiante (con email)
```
DNI: 11223344
Password: 11223344Ipet132!
Roles: STUDENT
Email: pedro.rodriguez@student.com
```

### Estudiante (sin email)
```
DNI: 87654321
Password: 87654321Ipet132!
Roles: STUDENT
Email: null
```

---

## 🚀 Inicio Rápido

### Prerequisitos

- Java 17+
- Maven 3.8+
- MySQL 8+
- IDE (IntelliJ IDEA recomendado)

### Instalación

```bash
# 1. Clonar repositorio
git clone <repository-url>
cd school-management

# 2. Crear base de datos
mysql -u root -p
CREATE DATABASE ipet132_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 3. Configurar application-dev.yml
# Actualizar credenciales de BD

# 4. Instalar dependencias
mvn clean install

# 5. Ejecutar aplicación
mvn spring-boot:run

# La aplicación estará disponible en:
# http://localhost:8080
```

### Testing con cURL

```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"dni":"00000001","password":"Admin123!","rememberMe":false}'

# Guardar token
export TOKEN="<access_token_from_response>"

# Obtener perfil
curl -X GET http://localhost:8080/api/auth/profile \
  -H "Authorization: Bearer $TOKEN"

# Crear estudiante
curl -X POST http://localhost:8080/api/admin/students \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "dni":"99887766",
    "firstName":"Carlos",
    "lastName":"González",
    "grade":"4",
    "division":"B",
    "parentEmail":"padre@mail.com"
  }'
```

---

## 📦 Dependencias Principales

```xml
<dependencies>
    <!-- Spring Boot -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.12.3</version>
    </dependency>

    <!-- MapStruct -->
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
        <version>1.5.5.Final</version>
    </dependency>

    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

    <!-- MySQL -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
    </dependency>

    <!-- Flyway -->
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-mysql</artifactId>
    </dependency>
</dependencies>
```

---

## 📊 Estado del Proyecto

### ✅ Implementado (MVP - Fase 1)

- [x] Arquitectura hexagonal completa
- [x] Domain models y Value Objects
- [x] Autenticación con DNI
- [x] Login y Logout
- [x] Refresh Token con rotation
- [x] Gestión de sesiones activas
- [x] Creación de estudiantes (Admin)
- [x] Creación de profesores (Admin)
- [x] Activación de cuenta de profesores
- [x] Cambio de contraseña
- [x] Blacklist de tokens
- [x] Global Exception Handler
- [x] Mappers con MapStruct
- [x] Repositories completos
- [x] Unit y Integration Tests
- [x] Data Seeder para testing
- [x] Documentación completa

### ⏳ Pendiente (Post-MVP)

- [ ] Bounded Context: Students (separado)
- [ ] Bounded Context: Teachers (separado)
- [ ] Bounded Context: Courses
- [ ] Bounded Context: Grades
- [ ] Email Service (envío real de emails)
- [ ] Búsqueda y paginación avanzada
- [ ] Bulk operations
- [ ] Rate limiting
- [ ] Auditoría completa
- [ ] Métricas y monitoring
- [ ] Documentación API con Swagger/OpenAPI

---

## 🤝 Contribución

Este proyecto sigue principios de **Clean Architecture** y **Domain-Driven Design**. Para contribuir:

1. Mantener la separación de capas estricta
2. Seguir los patrones establecidos
3. Escribir tests para nuevo código
4. Documentar decisiones arquitectónicas importantes
5. Usar commits descriptivos

---

## 📝 Notas Técnicas

### Decisiones Arquitectónicas

1. **DNI como Username**: Decisión específica para sistema escolar argentino
2. **Email Opcional**: Permite estudiantes menores sin email
3. **Token Rotation**: Máxima seguridad, siguiendo OWASP recommendations
4. **Roles como String**: Decisión de simplicidad para MVP, roles son fijos
5. **Records para DTOs**: Inmutabilidad y menos boilerplate
6. **MapStruct**: Type-safe mapping en compile-time
7. **Vertical Slicing**: Preparado para migrar a microservicios

### Patrones Utilizados

- Repository Pattern
- Factory Pattern (User creation)
- Value Object Pattern
- Domain Events (preparado)
- CQRS (preparado en estructura)
- Port & Adapters (Hexagonal)

---

## 📞 Contacto y Soporte

Para consultas sobre el proyecto, arquitectura o implementación, revisar:
- Documentación en código (JavaDoc)
- Tests como ejemplos de uso
- Este README

---

## 📄 Licencia

[Definir licencia del proyecto]

---

**Última actualización**: Noviembre 2024
**Versión**: 1.0.0-MVP
**Estado**: En desarrollo activo