package com.jobportal.admin.controller;

import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jobportal.application.repository.ApplicationRepository;
import com.jobportal.auth.entity.Role;
import com.jobportal.auth.entity.User;
import com.jobportal.auth.repository.RefreshTokenRepository;
import com.jobportal.auth.repository.UserRepository;
import com.jobportal.candidate.repository.CandidateRepository;
import com.jobportal.employer.repository.EmployerRepository;
import com.jobportal.job.repository.SavedJobRepository;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private EmployerRepository employerRepository;

    @Autowired
    private SavedJobRepository savedJobRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    private User adminUser;
    private User candidateUser;

    @BeforeEach
    void setUp() {
        savedJobRepository.deleteAll();
        applicationRepository.deleteAll();
        candidateRepository.deleteAll();
        employerRepository.deleteAll();
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();

        adminUser = userRepository.save(User.builder()
                .name("Admin User")
                .email("admin@test.com")
                .password("encoded")
                .role(Role.ADMIN)
                .build());

        candidateUser = userRepository.save(User.builder()
                .name("Test Candidate")
                .email("candidate@test.com")
                .password("encoded")
                .role(Role.CANDIDATE)
                .build());
    }

    // ── GET /api/admin/users ────────────────────────────────────────────

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void shouldGetAllUsers() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.totalElements", is(2)));
    }

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void shouldGetAllUsersWithPagination() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                        .param("page", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size", is(1)))
                .andExpect(jsonPath("$.data.totalElements", is(2)))
                .andExpect(jsonPath("$.data.totalPages", is(2)));
    }

    // ── GET /api/admin/users/{id} ───────────────────────────────────────

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void shouldGetUserById() throws Exception {
        mockMvc.perform(get("/api/admin/users/" + candidateUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.name", is("Test Candidate")))
                .andExpect(jsonPath("$.data.email", is("candidate@test.com")))
                .andExpect(jsonPath("$.data.role", is("CANDIDATE")));
    }

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void shouldReturn404WhenUserNotFound() throws Exception {
        mockMvc.perform(get("/api/admin/users/99999"))
                .andExpect(status().isNotFound());
    }

    // ── PUT /api/admin/users/{id}/role ───────────────────────────────────

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void shouldUpdateUserRole() throws Exception {
        String requestBody = """
                {
                    "role": "EMPLOYER"
                }
                """;

        mockMvc.perform(put("/api/admin/users/" + candidateUser.getId() + "/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.role", is("EMPLOYER")));
    }

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void shouldReturn400WhenRoleIsNull() throws Exception {
        String requestBody = """
                {
                    "role": null
                }
                """;

        mockMvc.perform(put("/api/admin/users/" + candidateUser.getId() + "/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    // ── DELETE /api/admin/users/{id} ────────────────────────────────────

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void shouldDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/admin/users/" + candidateUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("User deleted successfully")));
    }

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void shouldReturn404WhenDeletingNonExistentUser() throws Exception {
        mockMvc.perform(delete("/api/admin/users/99999"))
                .andExpect(status().isNotFound());
    }

    // ── GET /api/admin/dashboard ────────────────────────────────────────

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    void shouldGetDashboardStats() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.totalUsers", is(2)))
                .andExpect(jsonPath("$.data.totalCandidates", is(1)))
                .andExpect(jsonPath("$.data.totalAdmins", is(1)));
    }

    // ── Access control ──────────────────────────────────────────────────

    @Test
    @WithMockUser(username = "candidate@test.com", roles = "CANDIDATE")
    void shouldReturn403ForCandidateAccessingAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "employer@test.com", roles = "EMPLOYER")
    void shouldReturn403ForEmployerAccessingAdmin() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard"))
                .andExpect(status().isForbidden());
    }
}
