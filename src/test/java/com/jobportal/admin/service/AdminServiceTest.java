package com.jobportal.admin.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.jobportal.admin.dto.AdminDashboardDTO;
import com.jobportal.admin.dto.UpdateRoleDTO;
import com.jobportal.admin.dto.UserResponseDTO;
import com.jobportal.application.repository.ApplicationRepository;
import com.jobportal.auth.entity.Role;
import com.jobportal.auth.entity.User;
import com.jobportal.auth.repository.UserRepository;
import com.jobportal.common.exception.ResourceNotFoundException;
import com.jobportal.job.repository.JobRepository;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private AdminService adminService;

    private User createTestUser(Long id, String name, String email, Role role) {
        User user = User.builder()
                .id(id)
                .name(name)
                .email(email)
                .password("encoded")
                .role(role)
                .build();
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    // ── getAllUsers ──────────────────────────────────────────────────────

    @Test
    void shouldGetAllUsersPaginated() {
        Pageable pageable = PageRequest.of(0, 10);
        User user1 = createTestUser(1L, "Alice", "alice@test.com", Role.CANDIDATE);
        User user2 = createTestUser(2L, "Bob", "bob@test.com", Role.EMPLOYER);
        Page<User> page = new PageImpl<>(List.of(user1, user2), pageable, 2);

        when(userRepository.findAll(pageable)).thenReturn(page);

        Page<UserResponseDTO> result = adminService.getAllUsers(pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals("Alice", result.getContent().get(0).getName());
        assertEquals("Bob", result.getContent().get(1).getName());
        verify(userRepository).findAll(pageable);
    }

    @Test
    void shouldReturnEmptyPageWhenNoUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(userRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<UserResponseDTO> result = adminService.getAllUsers(pageable);

        assertEquals(0, result.getTotalElements());
    }

    // ── getUserById ─────────────────────────────────────────────────────

    @Test
    void shouldGetUserById() {
        User user = createTestUser(1L, "Alice", "alice@test.com", Role.CANDIDATE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponseDTO result = adminService.getUserById(1L);

        assertNotNull(result);
        assertEquals("Alice", result.getName());
        assertEquals("alice@test.com", result.getEmail());
        assertEquals(Role.CANDIDATE, result.getRole());
    }

    @Test
    void shouldThrowWhenUserNotFoundById() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> adminService.getUserById(99L));
        assertEquals("User not found with id: 99", ex.getMessage());
    }

    // ── updateUserRole ──────────────────────────────────────────────────

    @Test
    void shouldUpdateUserRole() {
        User user = createTestUser(1L, "Alice", "alice@test.com", Role.CANDIDATE);
        User updated = createTestUser(1L, "Alice", "alice@test.com", Role.EMPLOYER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updated);

        UpdateRoleDTO dto = new UpdateRoleDTO(Role.EMPLOYER);
        UserResponseDTO result = adminService.updateUserRole(1L, dto);

        assertEquals(Role.EMPLOYER, result.getRole());
        verify(userRepository).save(user);
    }

    @Test
    void shouldThrowWhenUpdatingRoleOfNonExistentUser() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        UpdateRoleDTO dto = new UpdateRoleDTO(Role.ADMIN);
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> adminService.updateUserRole(99L, dto));
        assertEquals("User not found with id: 99", ex.getMessage());
    }

    // ── deleteUser ──────────────────────────────────────────────────────

    @Test
    void shouldDeleteUser() {
        when(userRepository.existsById(1L)).thenReturn(true);

        adminService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void shouldThrowWhenDeletingNonExistentUser() {
        when(userRepository.existsById(99L)).thenReturn(false);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> adminService.deleteUser(99L));
        assertEquals("User not found with id: 99", ex.getMessage());
        verify(userRepository, never()).deleteById(99L);
    }

    // ── getDashboard ────────────────────────────────────────────────────

    @Test
    void shouldGetDashboardStats() {
        when(userRepository.count()).thenReturn(50L);
        when(userRepository.countByRole(Role.CANDIDATE)).thenReturn(30L);
        when(userRepository.countByRole(Role.EMPLOYER)).thenReturn(18L);
        when(userRepository.countByRole(Role.ADMIN)).thenReturn(2L);
        when(jobRepository.count()).thenReturn(100L);
        when(applicationRepository.count()).thenReturn(200L);

        AdminDashboardDTO dashboard = adminService.getDashboard();

        assertEquals(50L, dashboard.getTotalUsers());
        assertEquals(30L, dashboard.getTotalCandidates());
        assertEquals(18L, dashboard.getTotalEmployers());
        assertEquals(2L, dashboard.getTotalAdmins());
        assertEquals(100L, dashboard.getTotalJobs());
        assertEquals(200L, dashboard.getTotalApplications());
    }

    @Test
    void shouldReturnZeroStatsWhenEmpty() {
        when(userRepository.count()).thenReturn(0L);
        when(userRepository.countByRole(Role.CANDIDATE)).thenReturn(0L);
        when(userRepository.countByRole(Role.EMPLOYER)).thenReturn(0L);
        when(userRepository.countByRole(Role.ADMIN)).thenReturn(0L);
        when(jobRepository.count()).thenReturn(0L);
        when(applicationRepository.count()).thenReturn(0L);

        AdminDashboardDTO dashboard = adminService.getDashboard();

        assertEquals(0L, dashboard.getTotalUsers());
        assertEquals(0L, dashboard.getTotalJobs());
    }
}
