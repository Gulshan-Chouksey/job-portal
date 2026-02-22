package com.jobportal.candidate.controller;

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
import com.jobportal.candidate.entity.Candidate;
import com.jobportal.candidate.repository.CandidateRepository;
import com.jobportal.job.repository.SavedJobRepository;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "candidate@test.com", roles = "CANDIDATE")
class CandidateControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private SavedJobRepository savedJobRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        savedJobRepository.deleteAll();
        applicationRepository.deleteAll();
        candidateRepository.deleteAll();
        userRepository.deleteAll();

        testUser = userRepository.save(User.builder()
                .name("Test Candidate")
                .email("candidate@test.com")
                .password("encoded")
                .role(Role.CANDIDATE)
                .build());
    }

    // ── POST /api/candidates/profile ────────────────────────────────────

    @Test
    void shouldCreateProfileAndReturn201() throws Exception {
        String requestBody = """
                {
                    "headline": "Java Developer",
                    "summary": "Experienced backend developer",
                    "skills": "Java, Spring, SQL",
                    "experienceYears": 5,
                    "education": "B.Tech CS",
                    "resumeUrl": "https://resume.com/test.pdf",
                    "phone": "+1234567890",
                    "location": "New York"
                }
                """;

        mockMvc.perform(post("/api/candidates/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Candidate profile created successfully")))
                .andExpect(jsonPath("$.data.headline", is("Java Developer")))
                .andExpect(jsonPath("$.data.skills", is("Java, Spring, SQL")))
                .andExpect(jsonPath("$.data.experienceYears", is(5)))
                .andExpect(jsonPath("$.data.email", is("candidate@test.com")));
    }

    @Test
    void shouldReturn400WhenExperienceYearsNegative() throws Exception {
        String requestBody = """
                {
                    "headline": "Dev",
                    "experienceYears": -1
                }
                """;

        mockMvc.perform(post("/api/candidates/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn409WhenProfileAlreadyExists() throws Exception {
        candidateRepository.save(Candidate.builder()
                .user(testUser)
                .headline("Existing Profile")
                .build());

        String requestBody = """
                {
                    "headline": "New Profile"
                }
                """;

        mockMvc.perform(post("/api/candidates/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success", is(false)));
    }

    // ── GET /api/candidates/profile ─────────────────────────────────────

    @Test
    void shouldGetOwnProfile() throws Exception {
        candidateRepository.save(Candidate.builder()
                .user(testUser)
                .headline("Java Developer")
                .skills("Java, Spring")
                .experienceYears(5)
                .location("New York")
                .build());

        mockMvc.perform(get("/api/candidates/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.headline", is("Java Developer")))
                .andExpect(jsonPath("$.data.email", is("candidate@test.com")));
    }

    @Test
    void shouldReturn404WhenProfileNotFound() throws Exception {
        mockMvc.perform(get("/api/candidates/profile"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)));
    }

    // ── GET /api/candidates/{id} ────────────────────────────────────────

    @Test
    void shouldGetCandidateById() throws Exception {
        Candidate saved = candidateRepository.save(Candidate.builder()
                .user(testUser)
                .headline("Java Developer")
                .skills("Java")
                .build());

        mockMvc.perform(get("/api/candidates/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.headline", is("Java Developer")));
    }

    @Test
    void shouldReturn404WhenCandidateIdNotFound() throws Exception {
        mockMvc.perform(get("/api/candidates/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)));
    }

    // ── PUT /api/candidates/profile ─────────────────────────────────────

    @Test
    void shouldUpdateProfileSuccessfully() throws Exception {
        candidateRepository.save(Candidate.builder()
                .user(testUser)
                .headline("Old Headline")
                .skills("Java")
                .build());

        String requestBody = """
                {
                    "headline": "Senior Java Developer",
                    "summary": "Updated summary",
                    "skills": "Java, Spring, Kafka, Docker",
                    "experienceYears": 8,
                    "education": "M.Tech CS",
                    "location": "London"
                }
                """;

        mockMvc.perform(put("/api/candidates/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Candidate profile updated successfully")))
                .andExpect(jsonPath("$.data.headline", is("Senior Java Developer")))
                .andExpect(jsonPath("$.data.experienceYears", is(8)))
                .andExpect(jsonPath("$.data.location", is("London")));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentProfile() throws Exception {
        String requestBody = """
                {
                    "headline": "Updated"
                }
                """;

        mockMvc.perform(put("/api/candidates/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)));
    }

    // ── SECURITY ────────────────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "EMPLOYER")
    void shouldReturn403ForEmployerRole() throws Exception {
        mockMvc.perform(get("/api/candidates/profile"))
                .andExpect(status().isForbidden());
    }
}
