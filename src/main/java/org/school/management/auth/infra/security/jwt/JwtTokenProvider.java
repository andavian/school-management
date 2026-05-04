package org.school.management.auth.infra.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.domain.valueobject.UserId;
import org.school.management.auth.infra.security.UserPrincipal;
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

/**
 * Proveedor de JWT para access tokens.
 *
 * <p>Responsabilidad exclusiva: generar y validar <strong>access tokens</strong>.
 * Los refresh tokens y los confirmation tokens son ahora tokens opacos
 * persistidos hasheados en BD — no pasan por esta clase.</p>
 *
 * <p>Flujos:</p>
 * <ul>
 *   <li>Login → {@link #generateAccessToken(UserDetails)} → JWT access token</li>
 *   <li>Refresh → {@link org.school.management.auth.application.usecases.RefreshTokenUseCase}
 *       valida contra BD y genera nuevo access token via este método</li>
 *   <li>Activación → {@link org.school.management.auth.application.usecases.ActivateAccountUseCase}
 *       valida el token opaco contra {@code confirmation_codes} en BD</li>
 * </ul>
 */
@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${app.security.jwt.secret-key}")
    private String jwtSecret;

    @Value("${app.security.jwt.access-token-expiration}")
    private long accessTokenExpirationSeconds;

    @Value("${app.security.jwt.issuer}")
    private String issuer;

    // ── Clave secreta ─────────────────────────────────────────────────────

    private SecretKey getKey() {
        if (jwtSecret.length() < 32) {
            throw new IllegalArgumentException(
                    "JWT secret key must be at least 256 bits (32 characters)");
        }
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // ── Generación ────────────────────────────────────────────────────────

    /**
     * Genera un access token JWT con roles y userId embebidos.
     * Tipo de token: {@code "ACCESS"}.
     */
    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        claims.put("isCurrent", userDetails.isEnabled());

        UserId userId = (userDetails instanceof UserPrincipal principal)
                ? principal.user().getUserId()
                : null;

        if (userId == null) {
            throw new IllegalArgumentException(
                    "Cannot generate access token: UserDetails is not a UserPrincipal");
        }

        return buildToken(userId, userDetails.getUsername(), claims,
                accessTokenExpirationSeconds, "ACCESS");
    }

    // ── Construcción interna ──────────────────────────────────────────────

    private String buildToken(UserId userId, String username,
                              Map<String, Object> claims,
                              long expirationSeconds,
                              String tokenType) {
        Instant now        = Instant.now();
        Instant expiration = now.plus(expirationSeconds, ChronoUnit.SECONDS);

        claims.put("userId",    userId.value().toString());
        claims.put("tokenType", tokenType);
        claims.put("iss",       issuer);
        claims.put("iat",       now.getEpochSecond());
        claims.put("jti",       UUID.randomUUID().toString());

        log.debug("Generating {} token for user: {}, expires: {}",
                tokenType, username, expiration);

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(getKey())
                .compact();
    }

    // ── Extracción de claims ──────────────────────────────────────────────

    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public UserId getUserIdFromToken(String token) {
        String userIdStr = getClaim(token,
                claims -> claims.get("userId", String.class));
        return UserId.from(UUID.fromString(userIdStr));
    }

    public String getTokenType(String token) {
        return getClaim(token, claims -> claims.get("tokenType", String.class));
    }

    public Date getExpiration(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    public <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(getAllClaims(token));
    }

    // ── Validación ────────────────────────────────────────────────────────

    /**
     * Valida un access token contra el {@link UserDetails} del usuario.
     * Verifica: username, expiración, tipo {@code "ACCESS"} y estado activo.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username  = getUsernameFromToken(token);
            final String tokenType = getTokenType(token);

            boolean valid = username.equals(userDetails.getUsername())
                    && !isTokenExpired(token)
                    && "ACCESS".equals(tokenType)
                    && userDetails.isEnabled()
                    && userDetails.isAccountNonLocked()
                    && userDetails.isAccountNonExpired()
                    && userDetails.isCredentialsNonExpired();

            log.debug("Access token validation for {}: {}", username, valid);
            return valid;

        } catch (Exception e) {
            log.error("Error validating access token: {}", e.getMessage());
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            boolean expired = getExpiration(token).before(new Date());
            if (expired) {
                log.debug("Token expired at: {}", getExpiration(token));
            }
            return expired;
        } catch (Exception e) {
            log.error("Error checking token expiration: {}", e.getMessage());
            return true;
        }
    }

    // ── Parsing interno con manejo de errores ─────────────────────────────

    private Claims getAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getKey())
                    .requireIssuer(issuer)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (ExpiredJwtException e) {
            log.warn("JWT expired for user: {}", e.getClaims().getSubject());
            throw new TokenExpiredException("El token ha expirado", e);

        } catch (MalformedJwtException e) {
            log.error("Malformed JWT: {}", e.getMessage());
            throw new InvalidTokenException("Token malformado", e);

        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
            throw new InvalidTokenException("Firma de token inválida", e);

        } catch (Exception e) {
            log.error("Unexpected error processing JWT: {}", e.getMessage(), e);
            throw new TokenProcessingException("Error al procesar token", e);
        }
    }
}