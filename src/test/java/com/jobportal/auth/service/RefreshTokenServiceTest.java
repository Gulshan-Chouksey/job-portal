package com.jobportal.auth.service;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.jobportal.auth.entity.RefreshToken;
import com.jobportal.auth.entity.Role;
import com.jobportal.auth.entity.User;
import com.jobportal.auth.repository.RefreshTokenRepository;
import com.jobportal.common.exception.BadRequestException;
import com.jobportal.common.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private User createTestUser() {
        return User.builder()
                .id(1L)
                .name("John")
                .email("john@example.com")
                .password("encoded")
                .role(Role.CANDIDATE)
                .build();
    }

    @Test
    void shouldCreateRefreshTokenSuccessfully() {
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenExpiration", 604800000L);
        User user = createTestUser();

        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> {
            RefreshToken token = invocation.getArgument(0);
            token.setId(1L);
            return token;
        });

        RefreshToken result = refreshTokenService.createRefreshToken(user);

        assertNotNull(result);
        assertNotNull(result.getToken());
        assertEquals(user, result.getUser());
        assertTrue(result.getExpiryDate().isAfter(Instant.now()));
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void shouldVerifyValidRefreshToken() {
        RefreshToken refreshToken = RefreshToken.builder()
                .id(1L)
                .token("valid-token")
                .user(createTestUser())
                .expiryDate(Instant.now().plusMillis(604800000))
                .revoked(false)
                .build();

        when(refreshTokenRepository.findByToken("valid-token")).thenReturn(Optional.of(refreshToken));

        RefreshToken result = refreshTokenService.verifyRefreshToken("valid-token");

        assertNotNull(result);
        assertEquals("valid-token", result.getToken());
    }

    @Test
    void shouldThrowWhenRefreshTokenNotFound() {
        when(refreshTokenRepository.findByToken("invalid-token")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> refreshTokenService.verifyRefreshToken("invalid-token"));
    }

    @Test
    void shouldThrowWhenRefreshTokenIsRevoked() {
        RefreshToken refreshToken = RefreshToken.builder()
                .id(1L)
                .token("revoked-token")
                .user(createTestUser())
                .expiryDate(Instant.now().plusMillis(604800000))
                .revoked(true)
                .build();

        when(refreshTokenRepository.findByToken("revoked-token")).thenReturn(Optional.of(refreshToken));

        assertThrows(BadRequestException.class,
                () -> refreshTokenService.verifyRefreshToken("revoked-token"));
    }

    @Test
    void shouldThrowWhenRefreshTokenExpired() {
        RefreshToken refreshToken = RefreshToken.builder()
                .id(1L)
                .token("expired-token")
                .user(createTestUser())
                .expiryDate(Instant.now().minusMillis(1000))
                .revoked(false)
                .build();

        when(refreshTokenRepository.findByToken("expired-token")).thenReturn(Optional.of(refreshToken));

        assertThrows(BadRequestException.class,
                () -> refreshTokenService.verifyRefreshToken("expired-token"));
        verify(refreshTokenRepository).delete(refreshToken);
    }

    @Test
    void shouldRevokeRefreshToken() {
        RefreshToken refreshToken = RefreshToken.builder()
                .id(1L)
                .token("token-to-revoke")
                .user(createTestUser())
                .expiryDate(Instant.now().plusMillis(604800000))
                .revoked(false)
                .build();

        when(refreshTokenRepository.findByToken("token-to-revoke")).thenReturn(Optional.of(refreshToken));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        refreshTokenService.revokeRefreshToken("token-to-revoke");

        assertTrue(refreshToken.isRevoked());
        verify(refreshTokenRepository).save(refreshToken);
    }

    @Test
    void shouldThrowWhenRevokingNonExistentToken() {
        when(refreshTokenRepository.findByToken("non-existent")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> refreshTokenService.revokeRefreshToken("non-existent"));
    }

    @Test
    void shouldRevokeAllUserTokens() {
        refreshTokenService.revokeAllUserTokens(1L);

        verify(refreshTokenRepository).deleteByUserId(1L);
    }
}