package org.school.management.auth.infra.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.domain.model.User;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.auth.infra.security.exception.InvalidTokenException;
import org.school.management.auth.infra.security.exception.TokenExpiredException;
import org.school.management.auth.infra.security.exception.TokenProcessingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtTokenProvider {

    // ============================================
    // Usar configuración del nuevo application.yml
    // ============================================
    @Value("${app.security.jwt.secret-key}")
    private String jwtSecret;

    @Value("${app.security.jwt.access-token-expiration}")
    private long accessTokenExpirationSeconds;

    @Value("${app.security.jwt.refresh-token-expiration}")
    private long refreshTokenExpirationSeconds;

    @Value("${app.security.jwt.issuer}")
    private String issuer;

    // ============================================
    // Validación de clave secreta
    // ============================================
    private SecretKey getKey() {
        if (jwtSecret.length() < 32) { // Mínimo 256 bits
            throw new IllegalArgumentException("JWT secret key must be at least 256 bits (32 characters)");
        }
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // ============================================
    // Builder de token robusto
    // ============================================
    private String buildToken(UserId userId, String username, Map<String, Object> claims, long expirationSeconds, String tokenType) {
        Instant now = Instant.now();
        Instant expiration = now.plus(expirationSeconds, ChronoUnit.SECONDS);

        // Claims estándar mejorados
        claims.put("userId", userId.getValue().toString());
        claims.put("tokenType", tokenType);
        claims.put("iss", issuer);
        claims.put("iat", now.getEpochSecond());
        claims.put("jti", UUID.randomUUID().toString());


        log.debug("Generando token {} para usuario: {}, expira en: {}", tokenType, username, expiration);

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(getKey())
                .compact();
    }

    // ============================================
    // Tokens tipados
    // ============================================
    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        claims.put("isActive", user.getActive());

        return buildToken(user.getUserId(), user.getUsername(), claims, accessTokenExpirationSeconds, "ACCESS");
    }

    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        // 👇 Limitar claims: solo lo básico
        claims.put("userId", user.getUserId().getValue().toString());
        claims.put("jti", UUID.randomUUID().toString());

        return buildToken(user.getUserId(), user.getUsername(), claims, refreshTokenExpirationSeconds, "REFRESH");
    }

    public String generateConfirmationToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("purpose", "account_confirmation");
        claims.put("userId", user.getUserId().getValue().toString());

        // Token de confirmación válido por 15 minutos
        return buildToken(user.getUserId(), user.getUsername(), claims, 900, "CONFIRMATION");
    }

    // ============================================
    // Manejo de errores específico
    // ============================================
    private Claims getAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getKey())
                    .requireIssuer(issuer) // Validar issuer
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("JWT expirado para usuario: {}", e.getClaims().getSubject());
            throw new TokenExpiredException("El token ha expirado", e);
        } catch (MalformedJwtException e) {
            log.error("JWT malformado: {}", e.getMessage());
            throw new InvalidTokenException("Token malformado", e);
        } catch (SignatureException e) {
            log.error("Firma JWT inválida: {}", e.getMessage());
            throw new InvalidTokenException("Firma de token inválida", e);
        } catch (Exception e) {
            log.error("Error inesperado procesando JWT: {}", e.getMessage(), e);
            throw new TokenProcessingException("Error al procesar token", e);
        }
    }

    // ============================================
    // Métodos de extracción
    // ============================================
    public <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public UserId getUserIdFromToken(String token) {
        String userIdStr = getClaim(token, claims -> claims.get("userId", String.class));
        return UserId.from(UUID.fromString(userIdStr));
    }

    public String getTokenType(String token) {
        return getClaim(token, claims -> claims.get("tokenType", String.class));
    }

    // ============================================
    // MEJORA 7: Validaciones  robustas
    // ============================================
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpiration(token);
            boolean expired = expiration.before(new Date());
            if (expired) {
                log.debug("Token expirado. Expiración: {}, Ahora: {}", expiration, new Date());
            }
            return expired;
        } catch (Exception e) {
            log.error("Error verificando expiración del token: {}", e.getMessage());
            return true; // Si hay error, considerar como expirado
        }
    }

    public Date getExpiration(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = getUsernameFromToken(token);
            final String tokenType = getTokenType(token);

            boolean isValid = username.equals(userDetails.getUsername())
                    && !isTokenExpired(token)
                    && "ACCESS".equals(tokenType)
                    && userDetails.isEnabled()
                    && userDetails.isAccountNonLocked()
                    && userDetails.isAccountNonExpired()
                    && userDetails.isCredentialsNonExpired();

            log.debug("Validación de token para {}: {}", username, isValid);
            return isValid;

        } catch (Exception e) {
            log.error("Error validando token: {}", e.getMessage());
            return false;
        }
    }

    public boolean isRefreshTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = getUsernameFromToken(token);
            final String tokenType = getTokenType(token);

            return username.equals(userDetails.getUsername())
                    && !isTokenExpired(token)
                    && "REFRESH".equals(tokenType);

        } catch (Exception e) {
            log.error("Error validando refresh token: {}", e.getMessage());
            return false;
        }
    }

    public boolean isConfirmationTokenValid(String token) {
        try {
            String purpose = getClaim(token, claims -> claims.get("purpose", String.class));
            String tokenType = getTokenType(token);

            return "account_confirmation".equals(purpose)
                    && "CONFIRMATION".equals(tokenType)
                    && !isTokenExpired(token);

        } catch (Exception e) {
            log.error("Error validando token de confirmación: {}", e.getMessage());
            return false;
        }
    }

    // ============================================
    // MEJORA 8: Utilidades adicionales
    // ============================================
    public long getTokenExpirationTime(String tokenType) {
        return switch (tokenType.toUpperCase()) {
            case "ACCESS" -> accessTokenExpirationSeconds;
            case "REFRESH" -> refreshTokenExpirationSeconds;
            case "CONFIRMATION" -> 900; // 15 minutos
            default -> accessTokenExpirationSeconds;
        };
    }

    public boolean canTokenBeRefreshed(String token) {
        try {
            return !isTokenExpired(token) && "REFRESH".equals(getTokenType(token));
        } catch (Exception e) {
            return false;
        }
    }
}