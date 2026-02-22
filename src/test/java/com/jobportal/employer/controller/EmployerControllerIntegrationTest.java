package com.jobportal.employer.controller;

import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jobportal.application.repository.ApplicationRepository;
import com.jobportal.auth.entity.Role;
import com.jobportal.auth.entity.User;
import com.jobportal.auth.repository.UserRepository;
import com.jobportal.employer.entity.Employer;
import com.jobportal.employer.repository.EmployerRepository;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "employer@test.com", roles = "EMPLOYER")
class EmployerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployerRepository employerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        applicationRepository.deleteAll();
        employerRepository.deleteAll();
        userRepository.deleteAll();

        testUser = userRepository.save(User.builder()
                .name("Test Employer")
                .email("employer@test.com")
                .password("encoded")
                .role(Role.EMPLOYER)
                .build());
    }

    // ── POST /api/employers/profile ─────────────────────────────────────

    @Test
    void shouldCreateProfileAndReturn201() throws Exception {
        String requestBody = """
                {
                    "companyName": "Tech Corp",
                    "companyDescription": "A technology company",
                    "companyWebsite": "https://techcorp.com",
                    "industry": "Technology",
                    "location": "New York"
                }
                """;

        mockMvc.perform(post("/api/employers/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Employer profile created successfully")))
                .andExpect(jsonPath("$.data.companyName", is("Tech Corp")))
                .andExpect(jsonPath("$.data.industry", is("Technology")))
                .andExpect(jsonPath("$.data.location", is("New York")))
                .andExpect(jsonPath("$.data.email", is("employer@test.com")));
    }

    @Test
    void shouldReturn400WhenCompanyNameMissing() throws Exception {
        String requestBody = """
                {
                    "companyDescription": "A technology company"
                }
                """;

        mockMvc.perform(post("/api/employers/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn409WhenProfileAlreadyExists() throws Exception {
        employerRepository.save(Employer.builder()
                .user(testUser)
                .companyName("Existing Corp")
                .build());

        String requestBody = """
                {
                    "companyName": "Another Corp"
                }
                """;

        mockMvc.perform(post("/api/employers/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success", is(false)));
    }

    // ── GET /api/employers/profile ──────────────────────────────────────

    @Test
    void shouldGetOwnProfile() throws Exception {
        employerRepository.save(Employer.builder()
                .user(testUser)
                .companyName("Tech Corp")
                .companyDescription("A tech company")
                .industry("Technology")
                .location("New York")
                .build());

        mockMvc.perform(get("/api/employers/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.companyName", is("Tech Corp")))
                .andExpect(jsonPath("$.data.email", is("employer@test.com")));
    }

    @Test
    void shouldReturn404WhenProfileNotFound() throws Exception {
        mockMvc.perform(get("/api/employers/profile"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)));
    }

    // ── GET /api/employers/{id} ─────────────────────────────────────────

    @Test
    void shouldGetEmployerById() throws Exception {
        Employer saved = employerRepository.save(Employer.builder()
                .user(testUser)
                .companyName("Tech Corp")
                .industry("Technology")
                .build());

        mockMvc.perform(get("/api/employers/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.companyName", is("Tech Corp")));
    }

    @Test
    void shouldReturn404WhenEmployerIdNotFound() throws Exception {
        mockMvc.perform(get("/api/employers/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)));
    }

    // ── PUT /api/employers/profile ──────────────────────────────────────

    @Test
    void shouldUpdateProfileSuccessfully() throws Exception {
        employerRepository.save(Employer.builder()
                .user(testUser)
                .companyName("Old Corp")
                .industry("Technology")
                .build());

        String requestBody = """
                {
                    "companyName": "Updated Corp",
                    "companyDescription": "Updated description",
                    "companyWebsite": "https://updated.com",
                    "industry": "Finance",
                    "location": "London"
                }
                """;

        mockMvc.perform(put("/api/employers/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Employer profile updated successfully")))
                .andExpect(jsonPath("$.data.companyName", is("Updated Corp")))
                .andExpect(jsonPath("$.data.industry", is("Finance")))
                .andExpect(jsonPath("$.data.location", is("London")));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentProfile() throws Exception {
        String requestBody = """
                {
                    "companyName": "Updated Corp"
                }
                """;

        mockMvc.perform(put("/api/employers/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)));
    }

    // ── SECURITY ────────────────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "CANDIDATE")
    void shouldReturn403ForCandidateRole() throws Exception {
        mockMvc.perform(get("/api/employers/profile"))
                .andExpect(status().isForbidden());
    }
}
