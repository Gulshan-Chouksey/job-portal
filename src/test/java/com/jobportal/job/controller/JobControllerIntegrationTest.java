package com.jobportal.job.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jobportal.job.entity.Job;
import com.jobportal.job.repository.JobRepository;

@SpringBootTest
@AutoConfigureMockMvc
class JobControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JobRepository jobRepository;

    @BeforeEach
    void setUp() {
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
                .andExpect(jsonPath("$.data.location", is("Remote")));
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

        jobRepository.save(new Job(null, "Java Dev", "Backend", "Remote", 50000, 80000));
        jobRepository.save(new Job(null, "Python Dev", "ML role", "Hybrid", 60000, 90000));

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

    // ---- PUT /api/jobs/{id} ----

    @Test
    void shouldUpdateJobAndReturn200() throws Exception {

        Job saved = jobRepository.save(new Job(null, "Old Title", "Old Desc", "Old Loc", 40000, 60000));

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

        Job saved = jobRepository.save(new Job(null, "To Delete", "Desc", "Location", 50000, 80000));

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
}
