package org.school.management.auth.application.usecases;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.school.management.auth.application.dto.requests.BlacklistTokenRequest;
import org.school.management.auth.application.dto.responses.BlacklistedTokenResponse;
import org.school.management.auth.application.mappers.BlacklistedTokenApplicationMapper;
import org.school.management.auth.domain.model.BlacklistedToken;
import org.school.management.auth.domain.repository.BlacklistedTokenRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("BlacklistTokenUseCase")
class BlacklistTokenUseCaseTest {

    @Mock private BlacklistedTokenRepository repository;
    @Mock private BlacklistedTokenApplicationMapper mapper;

    @InjectMocks private BlacklistTokenUseCase useCase;

    // ── constantes ────────────────────────────────────────────────────────

    private static final String RAW_TOKEN = "eyJhbG.ciOiJ.accesstoken";
    private static final String DNI = "20345676";

    // ── helpers ───────────────────────────────────────────────────────────

    private BlacklistTokenRequest buildRequest() {
        return new BlacklistTokenRequest(
                RAW_TOKEN,
                "ACCESS",
                LocalDateTime.now().plusHours(1),
                "LOGOUT",
                DNI
        );
    }

    private BlacklistedTokenResponse buildResponse() {
        return new BlacklistedTokenResponse(
                "token-id-123",
                "ACCESS",
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                "LOGOUT",
                DNI,
                false,
                true
        );
    }

    // ── tests — flujo feliz ───────────────────────────────────────────────

    @Test
    @DisplayName("execute — flujo feliz — blacklistea el token y retorna respuesta")
    void execute_happyPath_blacklistsTokenAndReturnsResponse() {
        // given
        BlacklistTokenRequest request = buildRequest();
        BlacklistedTokenResponse expectedResponse = buildResponse();

        when(repository.existsByTokenHash(anyString())).thenReturn(false);
        when(repository.save(any(BlacklistedToken.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toResponse(any(BlacklistedToken.class))).thenReturn(expectedResponse);

        // when
        BlacklistedTokenResponse result = useCase.execute(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.reason()).isEqualTo("LOGOUT");
        assertThat(result.tokenType()).isEqualTo("ACCESS");
        assertThat(result.userEmail()).isEqualTo(DNI);

        verify(repository).existsByTokenHash(anyString());
        verify(repository).save(any(BlacklistedToken.class));
        verify(mapper).toResponse(any(BlacklistedToken.class));
    }

    @Test
    @DisplayName("execute — flujo feliz — hashea el token antes de verificar existencia")
    void execute_happyPath_hashesTokenBeforeChecking() {
        // given
        BlacklistTokenRequest request = buildRequest();
        BlacklistedTokenResponse expectedResponse = buildResponse();

        when(repository.existsByTokenHash(anyString())).thenReturn(false);
        when(repository.save(any(BlacklistedToken.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toResponse(any(BlacklistedToken.class))).thenReturn(expectedResponse);

        // when
        useCase.execute(request);

        // then — el hash se calcula via TokenHashUtil.hashToken()
        verify(repository).existsByTokenHash(anyString());
    }

    // ── tests — token ya blacklisteado ────────────────────────────────────

    @Test
    @DisplayName("execute — token ya blacklisteado — lanza TokenAlreadyBlacklistedException")
    void execute_tokenAlreadyBlacklisted_throwsTokenAlreadyBlacklistedException() {
        // given
        BlacklistTokenRequest request = buildRequest();

        when(repository.existsByTokenHash(anyString())).thenReturn(true);

        // when / then
        assertThatThrownBy(() -> useCase.execute(request))
                .isInstanceOf(BlacklistTokenUseCase.TokenAlreadyBlacklistedException.class)
                .hasMessageContaining("already blacklisted");

        verify(repository, never()).save(any());
        verifyNoInteractions(mapper);
    }

    // ── tests — verifica que el token guardado contiene los datos correctos ──

    @Test
    @DisplayName("execute — el token guardado tiene reason y userDni correctos")
    void execute_savedToken_hasCorrectReasonAndUserDni() {
        // given
        BlacklistTokenRequest request = new BlacklistTokenRequest(
                RAW_TOKEN, "REFRESH",
                LocalDateTime.now().plusHours(2),
                "PASSWORD_CHANGED",
                DNI
        );
        BlacklistedTokenResponse expectedResponse = buildResponse();

        when(repository.existsByTokenHash(anyString())).thenReturn(false);
        when(repository.save(any(BlacklistedToken.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toResponse(any(BlacklistedToken.class))).thenReturn(expectedResponse);

        // when
        useCase.execute(request);

        // then
        verify(repository).save(argThat(token ->
                "PASSWORD_CHANGED".equals(token.getReason())
                        && DNI.equals(token.getUserDni())
                        && "REFRESH".equals(token.getTokenType())
        ));
    }
}
