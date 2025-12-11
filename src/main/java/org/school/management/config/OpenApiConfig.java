package org.school.management.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "IPET 132 - School Management System API",
                version = "1.0.0",
                description = """
                        Sistema de Gestión Escolar para el IPET N° 132 “Presbítero José María Broggi”.
                        
                        ---
                        ## 📚 Módulos
                        - **Academic**: Años académicos, orientaciones, cursos y materias  
                        - **Auth**: Autenticación y autorización  
                        - **Geography**: Países, provincias y localidades  
                        
                        ---
                        ## 👥 Roles del Sistema
                        | Rol        | Permisos principales |
                        |------------|-----------------------|
                        | **ADMIN**  | Acceso completo       |
                        | **TEACHER**| Gestión académica     |
                        | **STUDENT**| Información propia     |
                        | **PARENT** | Información de hijos  |
                        | **STAFF**  | Gestión administrativa |
                        
                        ---
                        ## 🔐 Autenticación (JWT)
                        Todas las rutas —excepto `/api/auth/login` y `/api/auth/activate-account`— requieren autenticación mediante **Bearer JWT**.
                        
                        **Pasos para autenticarse:**
                        1. Hacer POST a `/api/auth/login` con DNI y contraseña.  
                        2. Copiar el `accessToken` devuelto.  
                        3. Click en **Authorize** y pegar el token (sin el prefijo `Bearer`).  
                        4. Swagger enviará el token automáticamente en todas las requests protegidas.  
                        """,
                contact = @Contact(
                        name = "Sistema de Gestión IPET 132",
                        email = "soporte@ipet132.edu.ar",
                        url = "https://ipet132.edu.ar"
                ),
                license = @License(
                        name = "Proprietary",
                        url = "https://ipet132.edu.ar/licencia"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local Development"),
                @Server(url = "https://api.ipet132.edu.ar", description = "Production")
        },
        security = @SecurityRequirement(name = "bearer-jwt") // 🔥 Aplica JWT a todos los endpoints por defecto
)
@SecurityScheme(
        name = "bearer-jwt",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER,
        description = """
                Autenticación JWT mediante Bearer Token.
                
                Para obtener un token:
                1. POST `/api/auth/login` con DNI y contraseña
                2. Copiar `accessToken` del JSON
                3. Pegar aquí el token (sin “Bearer ”)
                
                ---
                ### Ejemplo de credenciales (entorno de desarrollo)
                - **Admin** → DNI: `00000001`, Password: `Admin123!`
                """
)
public class OpenApiConfig {
    // No se necesita lógica adicional: toda la configuración está declarada por anotaciones.
}
