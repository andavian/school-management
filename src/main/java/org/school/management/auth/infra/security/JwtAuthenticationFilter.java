package org.school.management.auth.infra.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.school.management.auth.domain.repository.BlacklistedTokenRepository;
import org.school.management.auth.infra.security.exception.InvalidTokenException;
import org.school.management.auth.infra.security.exception.TokenExpiredException;
import org.school.management.auth.infra.security.util.TokenHashUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final ObjectMapper objectMapper;

    // ============================================
    // Endpoints que no requieren autenticación
    // ============================================
    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/refresh-token",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/actuator/health",
            "/actuator/info"
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // ============================================
        // Skip filtro para endpoints públicos
        // ============================================
        String requestPath = request.getRequestURI();
        if (isPublicEndpoint(requestPath)) {
            log.debug("Saltando autenticación JWT para endpoint público: {}", requestPath);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String token = getTokenFromRequest(request);

            if (token == null) {
                log.debug("No se encontró token JWT en la request a: {}", requestPath);
                filterChain.doFilter(request, response);
                return;
            }

            // ============================================
            // Verificar blacklist mejorada
            // ============================================
            if (isTokenBlacklisted(token)) {
                log.warn("Token en blacklist detectado desde IP: {}", request.getRemoteAddr());
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "BLACKLISTED_TOKEN", "Token has been revoked");
                return;
            }

            // ============================================
            // Procesamiento de token más robusto
            // ============================================
            final String username = jwtTokenProvider.getUsernameFromToken(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtTokenProvider.isTokenValid(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    log.debug("Usuario autenticado exitosamente: {}", username);
                } else {
                    log.warn("Token JWT inválido para usuario: {}", username);
                }
            }

        } catch (TokenExpiredException e) {
            log.warn("Token expirado en request a: {}", requestPath);
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED", "Token has expired");
            return;
        } catch (InvalidTokenException e) {
            log.warn("Token inválido en request a: {} - {}", requestPath, e.getMessage());
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "Invalid token");
            return;
        } catch (Exception e) {
            log.error("Error inesperado procesando JWT: {}", e.getMessage(), e);
            sendErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, "TOKEN_PROCESSING_ERROR", "Error processing token");
            return;
        }

        filterChain.doFilter(request, response);
    }

    // ============================================
    // Utilidades mejoradas
    // ============================================
    private boolean isPublicEndpoint(String requestPath) {
        return PUBLIC_ENDPOINTS.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, requestPath));
    }

    private boolean isTokenBlacklisted(String token) {
        try {
            String tokenHash = TokenHashUtil.hashToken(token);
            return blacklistedTokenRepository.existsByTokenHash(tokenHash);
        } catch (Exception e) {
            log.error("Error verificando blacklist: {}", e.getMessage());
            return false; // En caso de error, permitir continuar
        }
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        // También verificar en query parameter como fallback (útil para WebSocket)
        String tokenParam = request.getParameter("token");
        if (StringUtils.hasText(tokenParam)) {
            return tokenParam;
        }

        return null;
    }

    // ============================================
    // Respuestas de error estructuradas
    // ============================================
    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String errorCode, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", message);
        errorResponse.put("errorCode", errorCode);
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", status.value());

        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}