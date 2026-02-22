package com.jobportal.auth.controller;

import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jobportal.application.repository.ApplicationRepository;
import com.jobportal.auth.repository.UserRepository;
import com.jobportal.candidate.repository.CandidateRepository;
import com.jobportal.employer.repository.EmployerRepository;
import com.jobportal.job.repository.SavedJobRepository;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

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

    @BeforeEach
    void setUp() {
        savedJobRepository.deleteAll();
        applicationRepository.deleteAll();
        candidateRepository.deleteAll();
        employerRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldRegisterUserAndReturn201() throws Exception {

        String requestBody = """
                {
                    "name": "John Doe",
                    "email": "john@example.com",
                    "password": "password123",
                    "role": "CANDIDATE"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("User registered successfully")))
                .andExpect(jsonPath("$.data.name", is("John Doe")))
                .andExpect(jsonPath("$.data.email", is("john@example.com")))
                .andExpect(jsonPath("$.data.role", is("CANDIDATE")))
                .andExpect(jsonPath("$.data.token").exists());
    }

    @Test
    void shouldReturn409WhenEmailAlreadyRegistered() throws Exception {

        String requestBody = """
                {
                    "name": "John Doe",
                    "email": "john@example.com",
                    "password": "password123",
                    "role": "CANDIDATE"
                }
                """;

        // Register first
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());

        // Try again — should conflict
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Email already registered: john@example.com")));
    }

    @Test
    void shouldReturn400WhenRegisterWithInvalidData() throws Exception {

        String requestBody = """
                {
                    "name": "",
                    "email": "invalid-email",
                    "password": "12",
                    "role": null
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    void shouldLoginAndReturnToken() throws Exception {

        // Register first
        String registerBody = """
                {
                    "name": "Jane Doe",
                    "email": "jane@example.com",
                    "password": "password123",
                    "role": "EMPLOYER"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated());

        // Login
        String loginBody = """
                {
                    "email": "jane@example.com",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Login successful")))
                .andExpect(jsonPath("$.data.email", is("jane@example.com")))
                .andExpect(jsonPath("$.data.token").exists());
    }

    @Test
    void shouldReturn401WhenLoginWithWrongPassword() throws Exception {

        // Register
        String registerBody = """
                {
                    "name": "Jane Doe",
                    "email": "jane@example.com",
                    "password": "password123",
                    "role": "EMPLOYER"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated());

        // Login with wrong password
        String loginBody = """
                {
                    "email": "jane@example.com",
                    "password": "wrongpassword"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldGetCurrentUserWithValidToken() throws Exception {

        // Register
        String registerBody = """
                {
                    "name": "Alice",
                    "email": "alice@example.com",
                    "password": "password123",
                    "role": "CANDIDATE"
                }
                """;

        String registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // Extract token (simple parsing)
        String token = registerResult.split("\"token\":\"")[1].split("\"")[0];

        // Get current user
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.name", is("Alice")))
                .andExpect(jsonPath("$.data.email", is("alice@example.com")));
    }

    @Test
    void shouldReturn403WhenAccessingMeWithoutToken() throws Exception {

        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isForbidden());
    }
}
