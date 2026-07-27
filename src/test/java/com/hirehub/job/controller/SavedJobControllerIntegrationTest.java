package com.hirehub.job.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hirehub.application.repository.ApplicationRepository;
import com.hirehub.auth.entity.Role;
import com.hirehub.auth.entity.User;
import com.hirehub.auth.repository.RefreshTokenRepository;
import com.hirehub.auth.repository.UserRepository;
import com.hirehub.candidate.entity.Candidate;
import com.hirehub.candidate.repository.CandidateRepository;
import com.hirehub.job.entity.Job;
import com.hirehub.job.entity.JobStatus;
import com.hirehub.job.repository.JobRepository;
import com.hirehub.job.repository.SavedJobRepository;

@SpringBootTest
@AutoConfigureMockMvc
class SavedJobControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private SavedJobRepository savedJobRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    private Job testJob;

    @BeforeEach
    void setUp() {
        savedJobRepository.deleteAll();
        applicationRepository.deleteAll();
        candidateRepository.deleteAll();
        jobRepository.deleteAll();
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();

        // Create candidate user
        User candidateUser = User.builder()
                .name("Test Candidate")
                .email("candidate@test.com")
                .password("$2a$10$dummyhashpassword1234567890123456789012")
                .role(Role.CANDIDATE)
                .build();
        candidateUser = userRepository.save(candidateUser);

        Candidate candidate = new Candidate();
        candidate.setUser(candidateUser);
        candidate.setHeadline("Developer");
        candidateRepository.save(candidate);

        // Create a test job
        testJob = new Job();
        testJob.setTitle("Java Developer");
        testJob.setDescription("Java dev role");
        testJob.setLocation("Bangalore");
        testJob.setSalaryMin(50000);
        testJob.setSalaryMax(100000);
        testJob.setStatus(JobStatus.ACTIVE);
        testJob = jobRepository.save(testJob);
    }

    @Test
    @WithMockUser(username = "candidate@test.com", roles = "CANDIDATE")
    void saveJob_Success() throws Exception {
        mockMvc.perform(post("/api/saved-jobs/" + testJob.getId()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.jobTitle").value("Java Developer"));
    }

    @Test
    @WithMockUser(username = "candidate@test.com", roles = "CANDIDATE")
    void saveJob_Duplicate_Returns409() throws Exception {
        // Save first time
        mockMvc.perform(post("/api/saved-jobs/" + testJob.getId()))
                .andExpect(status().isCreated());

        // Save again - should fail
        mockMvc.perform(post("/api/saved-jobs/" + testJob.getId()))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "candidate@test.com", roles = "CANDIDATE")
    void saveJob_JobNotFound_Returns404() throws Exception {
        mockMvc.perform(post("/api/saved-jobs/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "candidate@test.com", roles = "CANDIDATE")
    void getMySavedJobs_Success() throws Exception {
        // Save a job first
        mockMvc.perform(post("/api/saved-jobs/" + testJob.getId()))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/saved-jobs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].jobTitle").value("Java Developer"));
    }

    @Test
    @WithMockUser(username = "candidate@test.com", roles = "CANDIDATE")
    void unsaveJob_Success() throws Exception {
        // Save first
        mockMvc.perform(post("/api/saved-jobs/" + testJob.getId()))
                .andExpect(status().isCreated());

        // Unsave
        mockMvc.perform(delete("/api/saved-jobs/" + testJob.getId()))
                .andExpect(status().isOk());

        // Verify unsaved
        mockMvc.perform(get("/api/saved-jobs/" + testJob.getId() + "/check"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(false));
    }

    @Test
    @WithMockUser(username = "candidate@test.com", roles = "CANDIDATE")
    void unsaveJob_NotFound_Returns404() throws Exception {
        mockMvc.perform(delete("/api/saved-jobs/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "candidate@test.com", roles = "CANDIDATE")
    void isJobSaved_ReturnsTrue() throws Exception {
        mockMvc.perform(post("/api/saved-jobs/" + testJob.getId()))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/saved-jobs/" + testJob.getId() + "/check"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    @WithMockUser(username = "candidate@test.com", roles = "CANDIDATE")
    void isJobSaved_ReturnsFalse() throws Exception {
        mockMvc.perform(get("/api/saved-jobs/" + testJob.getId() + "/check"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(false));
    }

    @Test
    @WithMockUser(username = "employer@test.com", roles = "EMPLOYER")
    void saveJob_AsEmployer_Returns403() throws Exception {
        mockMvc.perform(post("/api/saved-jobs/" + testJob.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    void saveJob_Unauthenticated_Returns403() throws Exception {
        mockMvc.perform(post("/api/saved-jobs/" + testJob.getId()))
                .andExpect(status().isForbidden());
    }
}
