package org.school.management.auth.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.auth.domain.model.RefreshToken;
import org.school.management.auth.domain.repository.RefreshTokenRepository;
import org.school.management.auth.infra.security.token.SecureTokenGenerator;
import org.school.management.auth.infra.security.token.TokenHasher;
import org.school.management.shared.person.domain.valueobject.Dni;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("GenerateRefreshTokenUseCase")
class GenerateRefreshTokenUseCaseTest {

    @Mock private RefreshTokenRepository repository;
    @Mock private SecureTokenGenerator tokenGenerator;
    @Mock private TokenHasher tokenHasher;

    @InjectMocks private GenerateRefreshTokenUseCase useCase;

    // ── constantes ────────────────────────────────────────────────────────

    private static final String DNI = "20345676";
    private static final String RAW_TOKEN = "raw-refresh-token-opaque";
    private static final String TOKEN_HASH = "sha256-hash-of-refresh-token";
    private static final String DEVICE_INFO = "browser";
    private static final String IP = "192.168.1.1";
    private static final String USER_AGENT = "Mozilla/5.0";

    // ── tests — flujo feliz ───────────────────────────────────────────────

    @Test
    @DisplayName("execute — flujo feliz — genera token, lo hashea, persiste y devuelve raw")
    void execute_happyPath_generatesAndReturnsRawToken() {
        // given
        when(tokenGenerator.generate()).thenReturn(RAW_TOKEN);
        when(tokenHasher.hash(RAW_TOKEN)).thenReturn(TOKEN_HASH);

        // when
        String result = useCase.execute(Dni.of(DNI), DEVICE_INFO, IP, USER_AGENT);

        // then
        assertThat(result).isEqualTo(RAW_TOKEN);

        verify(tokenGenerator).generate();
        verify(tokenHasher).hash(RAW_TOKEN);
        verify(repository).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("execute — flujo feliz — persiste refresh token con metadata del dispositivo")
    void execute_happyPath_persistsTokenWithDeviceMetadata() {
        // given
        when(tokenGenerator.generate()).thenReturn(RAW_TOKEN);
        when(tokenHasher.hash(RAW_TOKEN)).thenReturn(TOKEN_HASH);

        // when
        useCase.execute(Dni.of(DNI), DEVICE_INFO, IP, USER_AGENT);

        // then
        verify(repository).save(argThat(token ->
                DNI.equals(token.getUserDni().value())
                        && TOKEN_HASH.equals(token.getTokenHash())
                        && DEVICE_INFO.equals(token.getDeviceInfo())
                        && IP.equals(token.getIpAddress())
                        && USER_AGENT.equals(token.getUserAgent())
                        && token.getExpiresAt().isAfter(token.getIssuedAt())
        ));
    }
}
