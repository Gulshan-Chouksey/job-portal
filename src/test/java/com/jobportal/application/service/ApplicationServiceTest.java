package com.jobportal.application.service;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jobportal.application.dto.ApplicationRequestDTO;
import com.jobportal.application.dto.ApplicationResponseDTO;
import com.jobportal.application.dto.StatusUpdateDTO;
import com.jobportal.application.entity.ApplicationStatus;
import com.jobportal.application.entity.JobApplication;
import com.jobportal.application.repository.ApplicationRepository;
import com.jobportal.auth.entity.Role;
import com.jobportal.auth.entity.User;
import com.jobportal.auth.repository.UserRepository;
import com.jobportal.candidate.entity.Candidate;
import com.jobportal.candidate.repository.CandidateRepository;
import com.jobportal.common.exception.BadRequestException;
import com.jobportal.common.exception.DuplicateResourceException;
import com.jobportal.common.exception.ResourceNotFoundException;
import com.jobportal.job.entity.Job;
import com.jobportal.job.entity.JobStatus;
import com.jobportal.job.repository.JobRepository;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 1, 1, 12, 0);
    private static final String EMAIL = "candidate@test.com";

    @Mock
    private ApplicationRepository applicationRepository;
    @Mock
    private CandidateRepository candidateRepository;
    @Mock
    private JobRepository jobRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ApplicationService applicationService;

    private User createUser() {
        return User.builder().id(1L).name("Test Candidate").email(EMAIL)
                .password("enc").role(Role.CANDIDATE).createdAt(NOW).updatedAt(NOW).build();
    }

    private Candidate createCandidate(User user) {
        return Candidate.builder().id(1L).user(user).headline("Dev")
                .skills("Java").createdAt(NOW).updatedAt(NOW).build();
    }

    private Job createActiveJob() {
        Job job = new Job();
        job.setId(10L);
        job.setTitle("Java Developer");
        job.setDescription("Backend");
        job.setLocation("Remote");
        job.setSalaryMin(50000);
        job.setSalaryMax(80000);
        job.setStatus(JobStatus.ACTIVE);
        job.setCreatedAt(NOW);
        job.setUpdatedAt(NOW);
        return job;
    }

    private JobApplication createApplication(Candidate candidate, Job job) {
        return JobApplication.builder()
                .id(100L).candidate(candidate).job(job)
                .coverLetter("I am interested").status(ApplicationStatus.PENDING)
                .appliedAt(NOW).updatedAt(NOW).build();
    }

    // ── APPLY ───────────────────────────────────────────────────────────

    @Test
    void shouldApplySuccessfully() {
        User user = createUser();
        Candidate candidate = createCandidate(user);
        Job job = createActiveJob();
        ApplicationRequestDTO request = new ApplicationRequestDTO(10L, "I am interested");
        JobApplication saved = createApplication(candidate, job);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(candidateRepository.findByUserId(1L)).thenReturn(Optional.of(candidate));
        when(jobRepository.findById(10L)).thenReturn(Optional.of(job));
        when(applicationRepository.existsByCandidateIdAndJobId(1L, 10L)).thenReturn(false);
        when(applicationRepository.save(any(JobApplication.class))).thenReturn(saved);

        ApplicationResponseDTO response = applicationService.apply(EMAIL, request);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(ApplicationStatus.PENDING, response.getStatus());
        assertEquals("Java Developer", response.getJobTitle());
        verify(applicationRepository).save(any(JobApplication.class));
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        ApplicationRequestDTO request = new ApplicationRequestDTO(10L, "cover");
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> applicationService.apply(EMAIL, request));
    }

    @Test
    void shouldThrowWhenCandidateProfileMissing() {
        User user = createUser();
        ApplicationRequestDTO request = new ApplicationRequestDTO(10L, "cover");

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(candidateRepository.findByUserId(1L)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class,
                () -> applicationService.apply(EMAIL, request));
    }

    @Test
    void shouldThrowWhenJobNotFound() {
        User user = createUser();
        Candidate candidate = createCandidate(user);
        ApplicationRequestDTO request = new ApplicationRequestDTO(99L, "cover");

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(candidateRepository.findByUserId(1L)).thenReturn(Optional.of(candidate));
        when(jobRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> applicationService.apply(EMAIL, request));
    }

    @Test
    void shouldThrowWhenJobNotActive() {
        User user = createUser();
        Candidate candidate = createCandidate(user);
        Job job = createActiveJob();
        job.setStatus(JobStatus.CLOSED);
        ApplicationRequestDTO request = new ApplicationRequestDTO(10L, "cover");

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(candidateRepository.findByUserId(1L)).thenReturn(Optional.of(candidate));
        when(jobRepository.findById(10L)).thenReturn(Optional.of(job));

        assertThrows(BadRequestException.class,
                () -> applicationService.apply(EMAIL, request));
    }

    @Test
    void shouldThrowWhenAlreadyApplied() {
        User user = createUser();
        Candidate candidate = createCandidate(user);
        Job job = createActiveJob();
        ApplicationRequestDTO request = new ApplicationRequestDTO(10L, "cover");

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(candidateRepository.findByUserId(1L)).thenReturn(Optional.of(candidate));
        when(jobRepository.findById(10L)).thenReturn(Optional.of(job));
        when(applicationRepository.existsByCandidateIdAndJobId(1L, 10L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> applicationService.apply(EMAIL, request));
    }

    // ── GET BY ID ───────────────────────────────────────────────────────

    @Test
    void shouldGetApplicationById() {
        User user = createUser();
        Candidate candidate = createCandidate(user);
        Job job = createActiveJob();
        JobApplication app = createApplication(candidate, job);

        when(applicationRepository.findById(100L)).thenReturn(Optional.of(app));

        ApplicationResponseDTO response = applicationService.getApplicationById(100L);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals("Java Developer", response.getJobTitle());
    }

    @Test
    void shouldThrowWhenApplicationNotFoundById() {
        when(applicationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> applicationService.getApplicationById(999L));
    }

    // ── UPDATE STATUS ───────────────────────────────────────────────────

    @Test
    void shouldUpdateStatus() {
        User user = createUser();
        Candidate candidate = createCandidate(user);
        Job job = createActiveJob();
        JobApplication app = createApplication(candidate, job);

        JobApplication updated = createApplication(candidate, job);
        updated.setStatus(ApplicationStatus.SHORTLISTED);

        when(applicationRepository.findById(100L)).thenReturn(Optional.of(app));
        when(applicationRepository.save(any(JobApplication.class))).thenReturn(updated);

        ApplicationResponseDTO response = applicationService.updateStatus(
                100L, new StatusUpdateDTO(ApplicationStatus.SHORTLISTED));

        assertEquals(ApplicationStatus.SHORTLISTED, response.getStatus());
        verify(applicationRepository).save(any(JobApplication.class));
    }

    @Test
    void shouldThrowWhenUpdatingStatusOfNonExistentApp() {
        when(applicationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> applicationService.updateStatus(999L,
                        new StatusUpdateDTO(ApplicationStatus.REVIEWED)));
    }

    // ── WITHDRAW ────────────────────────────────────────────────────────

    @Test
    void shouldWithdrawSuccessfully() {
        User user = createUser();
        Candidate candidate = createCandidate(user);
        Job job = createActiveJob();
        JobApplication app = createApplication(candidate, job);

        JobApplication withdrawn = createApplication(candidate, job);
        withdrawn.setStatus(ApplicationStatus.WITHDRAWN);

        when(applicationRepository.findById(100L)).thenReturn(Optional.of(app));
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(candidateRepository.findByUserId(1L)).thenReturn(Optional.of(candidate));
        when(applicationRepository.save(any(JobApplication.class))).thenReturn(withdrawn);

        ApplicationResponseDTO response = applicationService.withdraw(100L, EMAIL);

        assertEquals(ApplicationStatus.WITHDRAWN, response.getStatus());
        verify(applicationRepository).save(any(JobApplication.class));
    }

    @Test
    void shouldThrowWhenWithdrawingOtherUsersApplication() {
        User user = createUser();
        Candidate candidate = createCandidate(user);
        Job job = createActiveJob();
        JobApplication app = createApplication(candidate, job);

        User otherUser = User.builder().id(2L).name("Other").email("other@test.com")
                .password("enc").role(Role.CANDIDATE).createdAt(NOW).updatedAt(NOW).build();
        Candidate otherCandidate = Candidate.builder().id(2L).user(otherUser).headline("Other")
                .createdAt(NOW).updatedAt(NOW).build();

        when(applicationRepository.findById(100L)).thenReturn(Optional.of(app));
        when(userRepository.findByEmail("other@test.com")).thenReturn(Optional.of(otherUser));
        when(candidateRepository.findByUserId(2L)).thenReturn(Optional.of(otherCandidate));

        assertThrows(BadRequestException.class,
                () -> applicationService.withdraw(100L, "other@test.com"));
    }

    @Test
    void shouldThrowWhenAlreadyWithdrawn() {
        User user = createUser();
        Candidate candidate = createCandidate(user);
        Job job = createActiveJob();
        JobApplication app = createApplication(candidate, job);
        app.setStatus(ApplicationStatus.WITHDRAWN);

        when(applicationRepository.findById(100L)).thenReturn(Optional.of(app));
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(candidateRepository.findByUserId(1L)).thenReturn(Optional.of(candidate));

        assertThrows(BadRequestException.class,
                () -> applicationService.withdraw(100L, EMAIL));
    }
}
