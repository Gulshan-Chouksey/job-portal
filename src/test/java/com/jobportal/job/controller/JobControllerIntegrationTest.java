package com.jobportal.job.controller;

import static org.hamcrest.Matchers.hasSize;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jobportal.application.repository.ApplicationRepository;
import com.jobportal.job.entity.Job;
import com.jobportal.job.entity.JobStatus;
import com.jobportal.job.repository.JobRepository;
import com.jobportal.job.repository.SavedJobRepository;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "EMPLOYER")
class JobControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private SavedJobRepository savedJobRepository;

    @BeforeEach
    void setUp() {
        savedJobRepository.deleteAll();
        applicationRepository.deleteAll();
        jobRepository.deleteAll();
    }

    // ---- POST /api/jobs ----

    @Test
    void shouldCreateJobAndReturn201() throws Exception {

        String requestBody = """
                {
                    "title": "Java Developer",
                    "description": "Backend role",
                    "location": "Remote",
                    "salaryMin": 50000,
                    "salaryMax": 80000
                }
                """;

        mockMvc.perform(post("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Job created successfully")))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.title", is("Java Developer")))
                .andExpect(jsonPath("$.data.location", is("Remote")))
                .andExpect(jsonPath("$.data.createdAt").exists())
                .andExpect(jsonPath("$.data.updatedAt").exists());
    }

    @Test
    void shouldReturn400WhenCreatingJobWithInvalidData() throws Exception {

        String requestBody = """
                {
                    "title": "",
                    "description": "Backend role",
                    "location": "",
                    "salaryMin": null,
                    "salaryMax": null
                }
                """;

        mockMvc.perform(post("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Validation failed")))
                .andExpect(jsonPath("$.data.title").exists());
    }

    // ---- GET /api/jobs ----

    @Test
    void shouldReturnPaginatedJobsAndReturn200() throws Exception {

        jobRepository.save(new Job(null, null, "Java Dev", "Backend", "Remote", 50000, 80000, JobStatus.ACTIVE, null, null));
        jobRepository.save(new Job(null, null, "Python Dev", "ML role", "Hybrid", 60000, 90000, JobStatus.ACTIVE, null, null));

        mockMvc.perform(get("/api/jobs")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Jobs retrieved successfully")))
                .andExpect(jsonPath("$.data.content", hasSize(2)))
                .andExpect(jsonPath("$.data.totalElements", is(2)));
    }

    @Test
    void shouldReturnEmptyPageWhenNoJobs() throws Exception {

        mockMvc.perform(get("/api/jobs")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content", hasSize(0)))
                .andExpect(jsonPath("$.data.totalElements", is(0)));
    }

    // ---- GET /api/jobs/{id} ----

    @Test
    void shouldReturnJobByIdAndReturn200() throws Exception {

        Job saved = jobRepository.save(new Job(null, null, "Java Dev", "Backend", "Remote", 50000, 80000, JobStatus.ACTIVE, null, null));

        mockMvc.perform(get("/api/jobs/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Job retrieved successfully")))
                .andExpect(jsonPath("$.data.id", is(saved.getId().intValue())))
                .andExpect(jsonPath("$.data.title", is("Java Dev")))
                .andExpect(jsonPath("$.data.location", is("Remote")));
    }

    @Test
    void shouldReturn404WhenGettingNonExistentJob() throws Exception {

        mockMvc.perform(get("/api/jobs/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Job not found with id: 999")));
    }

    // ---- PUT /api/jobs/{id} ----

    @Test
    void shouldUpdateJobAndReturn200() throws Exception {

        Job saved = jobRepository.save(new Job(null, null, "Old Title", "Old Desc", "Old Loc", 40000, 60000, JobStatus.ACTIVE, null, null));

        String updateBody = """
                {
                    "title": "Updated Title",
                    "description": "Updated Desc",
                    "location": "Updated Location",
                    "salaryMin": 70000,
                    "salaryMax": 100000
                }
                """;

        mockMvc.perform(put("/api/jobs/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Job updated successfully")))
                .andExpect(jsonPath("$.data.title", is("Updated Title")))
                .andExpect(jsonPath("$.data.salaryMin", is(70000)));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentJob() throws Exception {

        String updateBody = """
                {
                    "title": "Title",
                    "description": "Desc",
                    "location": "Location",
                    "salaryMin": 50000,
                    "salaryMax": 80000
                }
                """;

        mockMvc.perform(put("/api/jobs/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Job not found with id: 999")));
    }

    // ---- DELETE /api/jobs/{id} ----

    @Test
    void shouldDeleteJobAndReturn200() throws Exception {

        Job saved = jobRepository.save(new Job(null, null, "To Delete", "Desc", "Location", 50000, 80000, JobStatus.ACTIVE, null, null));

        mockMvc.perform(delete("/api/jobs/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Job deleted successfully")));
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentJob() throws Exception {

        mockMvc.perform(delete("/api/jobs/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Job not found with id: 999")));
    }

    // ---- GET /api/jobs/search ----

    @Test
    void shouldSearchJobsByKeyword() throws Exception {

        jobRepository.save(new Job(null, null, "Java Developer", "Backend", "Remote", 50000, 80000, JobStatus.ACTIVE, null, null));
        jobRepository.save(new Job(null, null, "Python Developer", "ML role", "Hybrid", 60000, 90000, JobStatus.ACTIVE, null, null));
        jobRepository.save(new Job(null, null, "DevOps Engineer", "Infra role", "Onsite", 70000, 100000, JobStatus.ACTIVE, null, null));

        mockMvc.perform(get("/api/jobs/search")
                        .param("keyword", "Developer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("Jobs search results")))
                .andExpect(jsonPath("$.data.content", hasSize(2)))
                .andExpect(jsonPath("$.data.totalElements", is(2)));
    }

    @Test
    void shouldSearchJobsByLocation() throws Exception {

        jobRepository.save(new Job(null, null, "Java Dev", "Backend", "Remote", 50000, 80000, JobStatus.ACTIVE, null, null));
        jobRepository.save(new Job(null, null, "Python Dev", "ML role", "Bangalore", 60000, 90000, JobStatus.ACTIVE, null, null));

        mockMvc.perform(get("/api/jobs/search")
                        .param("location", "Remote"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].title", is("Java Dev")));
    }

    @Test
    void shouldSearchJobsBySalaryRange() throws Exception {

        jobRepository.save(new Job(null, null, "Junior Dev", "Entry level", "Remote", 30000, 50000, JobStatus.ACTIVE, null, null));
        jobRepository.save(new Job(null, null, "Senior Dev", "Senior role", "Hybrid", 80000, 120000, JobStatus.ACTIVE, null, null));
        jobRepository.save(new Job(null, null, "Mid Dev", "Mid level", "Onsite", 50000, 80000, JobStatus.ACTIVE, null, null));

        mockMvc.perform(get("/api/jobs/search")
                        .param("minSalary", "50000")
                        .param("maxSalary", "100000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].title", is("Mid Dev")));
    }

    @Test
    void shouldReturnAllJobsWhenNoFiltersProvided() throws Exception {

        jobRepository.save(new Job(null, null, "Java Dev", "Backend", "Remote", 50000, 80000, JobStatus.ACTIVE, null, null));
        jobRepository.save(new Job(null, null, "Python Dev", "ML role", "Hybrid", 60000, 90000, JobStatus.ACTIVE, null, null));

        mockMvc.perform(get("/api/jobs/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content", hasSize(2)))
                .andExpect(jsonPath("$.data.totalElements", is(2)));
    }

    @Test
    void shouldReturnEmptyResultsWhenNoJobsMatchSearch() throws Exception {

        jobRepository.save(new Job(null, null, "Java Dev", "Backend", "Remote", 50000, 80000, JobStatus.ACTIVE, null, null));

        mockMvc.perform(get("/api/jobs/search")
                        .param("keyword", "Nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(0)))
                .andExpect(jsonPath("$.data.totalElements", is(0)));
    }

    // ---- Job Status Tests ----

    @Test
    void shouldCreateJobWithSpecificStatus() throws Exception {

        String requestBody = """
                {
                    "title": "Draft Job",
                    "description": "A draft position",
                    "location": "Remote",
                    "salaryMin": 50000,
                    "salaryMax": 80000,
                    "status": "DRAFT"
                }
                """;

        mockMvc.perform(post("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.status", is("DRAFT")));
    }

    @Test
    void shouldCreateJobWithDefaultActiveStatus() throws Exception {

        String requestBody = """
                {
                    "title": "Active Job",
                    "description": "No status specified",
                    "location": "Onsite",
                    "salaryMin": 40000,
                    "salaryMax": 70000
                }
                """;

        mockMvc.perform(post("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status", is("ACTIVE")));
    }

    @Test
    void shouldSearchJobsByStatus() throws Exception {

        jobRepository.save(new Job(null, null, "Active Job", "Desc", "Remote", 50000, 80000, JobStatus.ACTIVE, null, null));
        jobRepository.save(new Job(null, null, "Draft Job", "Desc", "Remote", 50000, 80000, JobStatus.DRAFT, null, null));
        jobRepository.save(new Job(null, null, "Closed Job", "Desc", "Remote", 50000, 80000, JobStatus.CLOSED, null, null));

        mockMvc.perform(get("/api/jobs/search")
                        .param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].title", is("Active Job")));
    }

    // ---- Salary Validation Tests ----

    @Test
    void shouldReturn400WhenSalaryMinGreaterThanSalaryMax() throws Exception {

        String requestBody = """
                {
                    "title": "Invalid Salary Job",
                    "description": "Salary range invalid",
                    "location": "Remote",
                    "salaryMin": 100000,
                    "salaryMax": 50000
                }
                """;

        mockMvc.perform(post("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Validation failed")));
    }
}
