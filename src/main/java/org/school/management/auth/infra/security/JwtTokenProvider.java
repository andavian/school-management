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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration.access}")
    private long accessTokenExpiration;

    @Value("${jwt.expiration.refresh}")
    private long refreshTokenExpiration;

    @Value("${jwt.expiration.confirmation}")
    private long confirmationTokenExpiration;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    private String buildToken(UserId userId, String username, Map<String, Object> claims, long expirationMillis) {
        claims.put("userId", userId.toString());

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(getKey())
                .compact();
    }

    // ---------------- ACCESS TOKEN ----------------
    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return buildToken(user.getUserId(), user.getUsername(), claims, accessTokenExpiration);
    }

    // ---------------- REFRESH TOKEN ----------------
    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return buildToken(user.getUserId(), user.getUsername(), claims, refreshTokenExpiration);
    }

    // ---------------- CONFIRMATION / ACTIVATION ----------------
    public String generateConfirmationToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("purpose", "account_confirmation");

        return buildToken(user.getUserId(), user.getUsername(), claims, confirmationTokenExpiration);
    }

    // ---------------- CLAIMS ----------------
    private Claims getAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.error("JWT expirado: {}", e.getMessage());
            throw new TokenExpiredException("El token ha expirado", e);
        } catch (MalformedJwtException | SignatureException e) {
            log.error("JWT inválido: {}", e.getMessage());
            throw new InvalidTokenException("Token inválido", e);
        } catch (Exception e) {
            log.error("Error al procesar JWT: {}", e.getMessage());
            throw new TokenProcessingException("Error al procesar token", e);
        }
    }

    public <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public UUID getUserIdFromToken(String token) {
        return UUID.fromString(getClaim(token, claims -> claims.get("userId", String.class)));
    }

    // ---------------- VALIDATION ----------------
    public boolean isTokenExpired(String token) {
        return getExpiration(token).before(new Date());
    }

    public Date getExpiration(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean isConfirmationTokenValid(String token) {
        try {
            String purpose = getClaim(token, claims -> (String) claims.get("purpose"));
            return "account_confirmation".equals(purpose) && !isTokenExpired(token);
        } catch (Exception e) {
            log.error("Error validando token de confirmación: {}", e.getMessage());
            return false;
        }
    }
}

