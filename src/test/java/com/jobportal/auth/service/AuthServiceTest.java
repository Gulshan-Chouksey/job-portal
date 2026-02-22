package com.jobportal.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.jobportal.auth.dto.AuthResponseDTO;
import com.jobportal.auth.dto.ChangePasswordDTO;
import com.jobportal.auth.dto.LoginRequestDTO;
import com.jobportal.auth.dto.RegisterRequestDTO;
import com.jobportal.auth.entity.Role;
import com.jobportal.auth.entity.User;
import com.jobportal.auth.repository.UserRepository;
import com.jobportal.common.exception.BadRequestException;
import com.jobportal.common.exception.DuplicateResourceException;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldRegisterUserSuccessfully() {

        RegisterRequestDTO request = new RegisterRequestDTO("John", "john@example.com", "password123", Role.CANDIDATE);

        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        User savedUser = User.builder()
                .id(1L)
                .name("John")
                .email("john@example.com")
                .password("encodedPassword")
                .role(Role.CANDIDATE)
                .build();

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDetails mockUserDetails = org.springframework.security.core.userdetails.User
                .withUsername("john@example.com")
                .password("encodedPassword")
                .roles("CANDIDATE")
                .build();

        when(userDetailsService.loadUserByUsername("john@example.com")).thenReturn(mockUserDetails);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwt-token");

        AuthResponseDTO response = authService.register(request);

        assertNotNull(response);
        assertEquals("John", response.getName());
        assertEquals("john@example.com", response.getEmail());
        assertEquals(Role.CANDIDATE, response.getRole());
        assertEquals("jwt-token", response.getToken());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {

        RegisterRequestDTO request = new RegisterRequestDTO("John", "john@example.com", "password123", Role.CANDIDATE);

        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> authService.register(request)
        );

        assertEquals("Email already registered: john@example.com", exception.getMessage());
    }

    @Test
    void shouldLoginSuccessfully() {

        LoginRequestDTO request = new LoginRequestDTO("john@example.com", "password123");

        User user = User.builder()
                .id(1L)
                .name("John")
                .email("john@example.com")
                .password("encodedPassword")
                .role(Role.CANDIDATE)
                .build();

        when(userRepository.findByEmail("john@example.com")).thenReturn(java.util.Optional.of(user));

        UserDetails mockUserDetails = org.springframework.security.core.userdetails.User
                .withUsername("john@example.com")
                .password("encodedPassword")
                .roles("CANDIDATE")
                .build();

        when(userDetailsService.loadUserByUsername("john@example.com")).thenReturn(mockUserDetails);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwt-token");

        AuthResponseDTO response = authService.login(request);

        assertNotNull(response);
        assertEquals("John", response.getName());
        assertEquals("jwt-token", response.getToken());

        verify(authenticationManager, times(1)).authenticate(any());
    }

    @Test
    void shouldGetCurrentUserSuccessfully() {

        User user = User.builder()
                .id(1L)
                .name("John")
                .email("john@example.com")
                .role(Role.CANDIDATE)
                .build();

        when(userRepository.findByEmail("john@example.com")).thenReturn(java.util.Optional.of(user));

        AuthResponseDTO response = authService.getCurrentUser("john@example.com");

        assertNotNull(response);
        assertEquals("John", response.getName());
        assertEquals(Role.CANDIDATE, response.getRole());
    }

    // ── CHANGE PASSWORD ─────────────────────────────────────────────────

    @Test
    void shouldChangePasswordSuccessfully() {
        ChangePasswordDTO request = new ChangePasswordDTO("oldPass123", "newPass456", "newPass456");

        User user = User.builder()
                .id(1L)
                .name("John")
                .email("john@example.com")
                .password("encodedOldPass")
                .role(Role.CANDIDATE)
                .build();

        when(userRepository.findByEmail("john@example.com")).thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.matches("oldPass123", "encodedOldPass")).thenReturn(true);
        when(passwordEncoder.encode("newPass456")).thenReturn("encodedNewPass");

        authService.changePassword("john@example.com", request);

        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("newPass456");
    }

    @Test
    void shouldThrowWhenPasswordsDoNotMatch() {
        ChangePasswordDTO request = new ChangePasswordDTO("oldPass123", "newPass456", "differentPass");

        assertThrows(BadRequestException.class,
                () -> authService.changePassword("john@example.com", request));
    }

    @Test
    void shouldThrowWhenCurrentPasswordIsIncorrect() {
        ChangePasswordDTO request = new ChangePasswordDTO("wrongPass", "newPass456", "newPass456");

        User user = User.builder()
                .id(1L)
                .name("John")
                .email("john@example.com")
                .password("encodedOldPass")
                .role(Role.CANDIDATE)
                .build();

        when(userRepository.findByEmail("john@example.com")).thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.matches("wrongPass", "encodedOldPass")).thenReturn(false);

        assertThrows(BadRequestException.class,
                () -> authService.changePassword("john@example.com", request));
    }

    @Test
    void shouldThrowWhenUserNotFoundOnChangePassword() {
        ChangePasswordDTO request = new ChangePasswordDTO("oldPass123", "newPass456", "newPass456");

        when(userRepository.findByEmail("john@example.com")).thenReturn(java.util.Optional.empty());

        assertThrows(com.jobportal.common.exception.ResourceNotFoundException.class,
                () -> authService.changePassword("john@example.com", request));
    }
}
