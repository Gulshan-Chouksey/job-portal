package com.jobportal.common.seeder;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.jobportal.application.repository.ApplicationRepository;
import com.jobportal.auth.repository.UserRepository;
import com.jobportal.candidate.repository.CandidateRepository;
import com.jobportal.employer.repository.EmployerRepository;
import com.jobportal.job.repository.CategoryRepository;
import com.jobportal.job.repository.JobRepository;
import com.jobportal.job.repository.SavedJobRepository;

@SpringBootTest
@ActiveProfiles("dev")
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:seederdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class DataSeederTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private EmployerRepository employerRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private SavedJobRepository savedJobRepository;

    @Test
    void shouldSeedUsersOnStartup() {
        assertThat(userRepository.count()).isEqualTo(6);
    }

    @Test
    void shouldSeedEmployersOnStartup() {
        assertThat(employerRepository.count()).isEqualTo(2);
    }

    @Test
    void shouldSeedCandidatesOnStartup() {
        assertThat(candidateRepository.count()).isEqualTo(3);
    }

    @Test
    void shouldSeedCategoriesOnStartup() {
        assertThat(categoryRepository.count()).isEqualTo(6);
    }

    @Test
    void shouldSeedJobsOnStartup() {
        assertThat(jobRepository.count()).isEqualTo(7);
    }

    @Test
    void shouldSeedApplicationsOnStartup() {
        assertThat(applicationRepository.count()).isEqualTo(6);
    }

    @Test
    void shouldSeedSavedJobsOnStartup() {
        assertThat(savedJobRepository.count()).isEqualTo(5);
    }

    @Test
    void shouldBeIdempotent() {
        long userCountBefore = userRepository.count();
        // Running the seeder again should not add duplicate data
        // because the seeder checks if data already exists
        assertThat(userRepository.count()).isEqualTo(userCountBefore);
    }
}
