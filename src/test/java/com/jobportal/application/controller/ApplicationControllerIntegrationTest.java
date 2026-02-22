package com.jobportal.application.controller;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jobportal.application.entity.ApplicationStatus;
import com.jobportal.application.entity.JobApplication;
import com.jobportal.application.repository.ApplicationRepository;
import com.jobportal.auth.entity.Role;
import com.jobportal.auth.entity.User;
import com.jobportal.auth.repository.UserRepository;
import com.jobportal.candidate.entity.Candidate;
import com.jobportal.candidate.repository.CandidateRepository;
import com.jobportal.job.entity.Job;
import com.jobportal.job.entity.JobStatus;
import com.jobportal.job.repository.JobRepository;
import com.jobportal.job.repository.SavedJobRepository;

@SpringBootTest
@AutoConfigureMockMvc
class ApplicationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SavedJobRepository savedJobRepository;

    private User candidateUser;
    private Candidate candidate;
    private Job job;

    @BeforeEach
    void setUp() {
        savedJobRepository.deleteAll();
        applicationRepository.deleteAll();
        jobRepository.deleteAll();
        candidateRepository.deleteAll();
        userRepository.deleteAll();

        candidateUser = userRepository.save(User.builder()
                .name("Test Candidate")
                .email("candidate@test.com")
                .password("encoded")
                .role(Role.CANDIDATE)
                .build());

        candidate = candidateRepository.save(Candidate.builder()
                .user(candidateUser)
                .headline("Java Developer")
                .skills("Java, Spring")
                .build());

        job = jobRepository.save(new Job(null, null, "Java Developer", "Backend",
                "Remote", 50000, 80000, JobStatus.ACTIVE, null, null));
    }

    // ── POST /api/applications ──────────────────────────────────────────

    @Test
    @WithMockUser(username = "candidate@test.com", roles = "CANDIDATE")
    void shouldApplySuccessfully() throws Exception {
        String requestBody = """
                {
                    "jobId": %d,
                    "coverLetter": "I am very interested in this role"
                }
                """.formatted(job.getId());

        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Application submitted successfully")))
                .andExpect(jsonPath("$.data.status", is("PENDING")))
                .andExpect(jsonPath("$.data.jobTitle", is("Java Developer")))
                .andExpect(jsonPath("$.data.candidateName", is("Test Candidate")));
    }

    @Test
    @WithMockUser(username = "candidate@test.com", roles = "CANDIDATE")
    void shouldReturn400WhenJobIdMissing() throws Exception {
        String requestBody = """
                {
                    "coverLetter": "cover"
                }
                """;

        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "candidate@test.com", roles = "CANDIDATE")
    void shouldReturn404WhenJobNotFound() throws Exception {
        String requestBody = """
                {
                    "jobId": 99999
                }
                """;

        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "candidate@test.com", roles = "CANDIDATE")
    void shouldReturn409WhenAlreadyApplied() throws Exception {
        applicationRepository.save(JobApplication.builder()
                .candidate(candidate)
                .job(job)
                .status(ApplicationStatus.PENDING)
                .build());

        String requestBody = """
                {
                    "jobId": %d
                }
                """.formatted(job.getId());

        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    @WithMockUser(username = "candidate@test.com", roles = "CANDIDATE")
    void shouldReturn400WhenJobNotActive() throws Exception {
        Job closedJob = jobRepository.save(new Job(null, null, "Closed Job", "Desc",
                "NYC", 40000, 60000, JobStatus.CLOSED, null, null));

        String requestBody = """
                {
                    "jobId": %d
                }
                """.formatted(closedJob.getId());

        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    // ── GET /api/applications/my ────────────────────────────────────────

    @Test
    @WithMockUser(username = "candidate@test.com", roles = "CANDIDATE")
    void shouldGetMyApplications() throws Exception {
        applicationRepository.save(JobApplication.builder()
                .candidate(candidate)
                .job(job)
                .status(ApplicationStatus.PENDING)
                .build());

        mockMvc.perform(get("/api/applications/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.totalElements", is(1)));
    }

    // ── GET /api/applications/{id} ──────────────────────────────────────

    @Test
    @WithMockUser(username = "candidate@test.com", roles = "CANDIDATE")
    void shouldGetApplicationById() throws Exception {
        JobApplication saved = applicationRepository.save(JobApplication.builder()
                .candidate(candidate)
                .job(job)
                .coverLetter("My cover letter")
                .status(ApplicationStatus.PENDING)
                .build());

        mockMvc.perform(get("/api/applications/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.coverLetter", is("My cover letter")))
                .andExpect(jsonPath("$.data.status", is("PENDING")));
    }

    @Test
    @WithMockUser(username = "candidate@test.com", roles = "CANDIDATE")
    void shouldReturn404WhenApplicationNotFound() throws Exception {
        mockMvc.perform(get("/api/applications/99999"))
                .andExpect(status().isNotFound());
    }

    // ── PATCH /api/applications/{id}/status ─────────────────────────────

    @Test
    @WithMockUser(username = "employer@test.com", roles = "EMPLOYER")
    void shouldUpdateApplicationStatus() throws Exception {
        JobApplication saved = applicationRepository.save(JobApplication.builder()
                .candidate(candidate)
                .job(job)
                .status(ApplicationStatus.PENDING)
                .build());

        String requestBody = """
                {
                    "status": "SHORTLISTED"
                }
                """;

        mockMvc.perform(patch("/api/applications/" + saved.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.status", is("SHORTLISTED")));
    }

    // ── PATCH /api/applications/{id}/withdraw ───────────────────────────

    @Test
    @WithMockUser(username = "candidate@test.com", roles = "CANDIDATE")
    void shouldWithdrawApplication() throws Exception {
        JobApplication saved = applicationRepository.save(JobApplication.builder()
                .candidate(candidate)
                .job(job)
                .status(ApplicationStatus.PENDING)
                .build());

        mockMvc.perform(patch("/api/applications/" + saved.getId() + "/withdraw"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Application withdrawn successfully")))
                .andExpect(jsonPath("$.data.status", is("WITHDRAWN")));
    }

    // ── SECURITY ────────────────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "EMPLOYER")
    void shouldReturn403WhenEmployerTriesToApply() throws Exception {
        String requestBody = """
                {
                    "jobId": %d
                }
                """.formatted(job.getId());

        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CANDIDATE")
    void shouldReturn403WhenCandidateTriesToUpdateStatus() throws Exception {
        JobApplication saved = applicationRepository.save(JobApplication.builder()
                .candidate(candidate)
                .job(job)
                .status(ApplicationStatus.PENDING)
                .build());

        String requestBody = """
                {
                    "status": "SHORTLISTED"
                }
                """;

        mockMvc.perform(patch("/api/applications/" + saved.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isForbidden());
    }
}
