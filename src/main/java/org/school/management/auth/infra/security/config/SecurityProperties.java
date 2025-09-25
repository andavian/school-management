package org.school.management.auth.infra.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {

    private Jwt jwt = new Jwt();
    private Cors cors = new Cors();

    @Data
    public static class Jwt {
        private String secretKey;
        private long accessTokenExpiration = 3600; // 1 hora
        private long refreshTokenExpiration = 86400; // 24 horas
        private String issuer = "school-management";
    }

    @Data
    public static class Cors {
        private List<String> allowedOrigins;
        private List<String> allowedMethods;
        private List<String> allowedHeaders;
        private List<String> exposedHeaders;
        private Long maxAge = 3600L;
        private boolean allowCredentials = true;
    }
}
