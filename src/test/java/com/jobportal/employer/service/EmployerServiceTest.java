package com.jobportal.employer.service;

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

import com.jobportal.application.repository.ApplicationRepository;
import com.jobportal.auth.entity.Role;
import com.jobportal.auth.entity.User;
import com.jobportal.auth.repository.UserRepository;
import com.jobportal.common.exception.DuplicateResourceException;
import com.jobportal.common.exception.ResourceNotFoundException;
import com.jobportal.employer.dto.EmployerDashboardDTO;
import com.jobportal.employer.dto.EmployerRequestDTO;
import com.jobportal.employer.dto.EmployerResponseDTO;
import com.jobportal.employer.entity.Employer;
import com.jobportal.employer.repository.EmployerRepository;
import com.jobportal.job.entity.JobStatus;
import com.jobportal.job.repository.JobRepository;

@ExtendWith(MockitoExtension.class)
class EmployerServiceTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 1, 1, 12, 0);
    private static final String EMAIL = "employer@test.com";

    @Mock
    private EmployerRepository employerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private EmployerService employerService;

    private User createTestUser() {
        return User.builder()
                .id(1L)
                .name("Test Employer")
                .email(EMAIL)
                .password("encoded")
                .role(Role.EMPLOYER)
                .createdAt(NOW)
                .updatedAt(NOW)
                .build();
    }

    private EmployerRequestDTO createTestRequest() {
        return new EmployerRequestDTO(
                "Tech Corp",
                "A tech company",
                "https://techcorp.com",
                "Technology",
                "New York"
        );
    }

    private Employer createTestEmployer(User user) {
        return Employer.builder()
                .id(1L)
                .user(user)
                .companyName("Tech Corp")
                .companyDescription("A tech company")
                .companyWebsite("https://techcorp.com")
                .industry("Technology")
                .location("New York")
                .createdAt(NOW)
                .updatedAt(NOW)
                .build();
    }

    // ── CREATE PROFILE ──────────────────────────────────────────────────

    @Test
    void shouldCreateProfileSuccessfully() {
        User user = createTestUser();
        EmployerRequestDTO request = createTestRequest();
        Employer saved = createTestEmployer(user);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(employerRepository.existsByUserId(1L)).thenReturn(false);
        when(employerRepository.save(any(Employer.class))).thenReturn(saved);

        EmployerResponseDTO response = employerService.createProfile(EMAIL, request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Tech Corp", response.getCompanyName());
        assertEquals("Test Employer", response.getName());
        assertEquals(EMAIL, response.getEmail());
        assertEquals("Technology", response.getIndustry());
        assertEquals("New York", response.getLocation());

        verify(employerRepository).save(any(Employer.class));
    }

    @Test
    void shouldThrowWhenProfileAlreadyExists() {
        User user = createTestUser();
        EmployerRequestDTO request = createTestRequest();

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(employerRepository.existsByUserId(1L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> employerService.createProfile(EMAIL, request));
    }

    @Test
    void shouldThrowWhenUserNotFoundOnCreate() {
        EmployerRequestDTO request = createTestRequest();

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> employerService.createProfile(EMAIL, request));
    }

    // ── GET PROFILE ─────────────────────────────────────────────────────

    @Test
    void shouldGetProfileByEmail() {
        User user = createTestUser();
        Employer employer = createTestEmployer(user);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(employerRepository.findByUserId(1L)).thenReturn(Optional.of(employer));

        EmployerResponseDTO response = employerService.getProfile(EMAIL);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Tech Corp", response.getCompanyName());
    }

    @Test
    void shouldThrowWhenUserNotFoundOnGetProfile() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> employerService.getProfile(EMAIL));
    }

    @Test
    void shouldThrowWhenProfileNotFoundOnGetProfile() {
        User user = createTestUser();

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(employerRepository.findByUserId(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> employerService.getProfile(EMAIL));
    }

    // ── GET PROFILE BY ID ───────────────────────────────────────────────

    @Test
    void shouldGetProfileById() {
        User user = createTestUser();
        Employer employer = createTestEmployer(user);

        when(employerRepository.findById(1L)).thenReturn(Optional.of(employer));

        EmployerResponseDTO response = employerService.getProfileById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Tech Corp", response.getCompanyName());
    }

    @Test
    void shouldThrowWhenEmployerNotFoundById() {
        when(employerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> employerService.getProfileById(99L));
    }

    // ── UPDATE PROFILE ──────────────────────────────────────────────────

    @Test
    void shouldUpdateProfileSuccessfully() {
        User user = createTestUser();
        Employer existing = createTestEmployer(user);

        EmployerRequestDTO updateRequest = new EmployerRequestDTO(
                "Updated Corp",
                "Updated description",
                "https://updated.com",
                "Finance",
                "London"
        );

        Employer updated = Employer.builder()
                .id(1L)
                .user(user)
                .companyName("Updated Corp")
                .companyDescription("Updated description")
                .companyWebsite("https://updated.com")
                .industry("Finance")
                .location("London")
                .createdAt(NOW)
                .updatedAt(NOW)
                .build();

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(employerRepository.findByUserId(1L)).thenReturn(Optional.of(existing));
        when(employerRepository.save(any(Employer.class))).thenReturn(updated);

        EmployerResponseDTO response = employerService.updateProfile(EMAIL, updateRequest);

        assertNotNull(response);
        assertEquals("Updated Corp", response.getCompanyName());
        assertEquals("Finance", response.getIndustry());
        assertEquals("London", response.getLocation());

        verify(employerRepository).save(any(Employer.class));
    }

    @Test
    void shouldThrowWhenUserNotFoundOnUpdate() {
        EmployerRequestDTO request = createTestRequest();

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> employerService.updateProfile(EMAIL, request));
    }

    @Test
    void shouldThrowWhenProfileNotFoundOnUpdate() {
        User user = createTestUser();
        EmployerRequestDTO request = createTestRequest();

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(employerRepository.findByUserId(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> employerService.updateProfile(EMAIL, request));
    }

    // ── GET DASHBOARD ───────────────────────────────────────────────────

    @Test
    void shouldGetDashboardSuccessfully() {
        User user = createTestUser();
        Employer employer = createTestEmployer(user);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(employerRepository.findByUserId(1L)).thenReturn(Optional.of(employer));
        when(jobRepository.countByEmployerId(1L)).thenReturn(10L);
        when(jobRepository.countByEmployerIdAndStatus(1L, JobStatus.ACTIVE)).thenReturn(5L);
        when(jobRepository.countByEmployerIdAndStatus(1L, JobStatus.DRAFT)).thenReturn(3L);
        when(jobRepository.countByEmployerIdAndStatus(1L, JobStatus.CLOSED)).thenReturn(2L);
        when(applicationRepository.countByJobEmployerId(1L)).thenReturn(25L);

        EmployerDashboardDTO dashboard = employerService.getDashboard(EMAIL);

        assertNotNull(dashboard);
        assertEquals(10L, dashboard.getTotalJobs());
        assertEquals(5L, dashboard.getActiveJobs());
        assertEquals(3L, dashboard.getDraftJobs());
        assertEquals(2L, dashboard.getClosedJobs());
        assertEquals(25L, dashboard.getTotalApplicationsReceived());
    }

    @Test
    void shouldGetDashboardWithZeroStats() {
        User user = createTestUser();
        Employer employer = createTestEmployer(user);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(employerRepository.findByUserId(1L)).thenReturn(Optional.of(employer));
        when(jobRepository.countByEmployerId(1L)).thenReturn(0L);
        when(jobRepository.countByEmployerIdAndStatus(1L, JobStatus.ACTIVE)).thenReturn(0L);
        when(jobRepository.countByEmployerIdAndStatus(1L, JobStatus.DRAFT)).thenReturn(0L);
        when(jobRepository.countByEmployerIdAndStatus(1L, JobStatus.CLOSED)).thenReturn(0L);
        when(applicationRepository.countByJobEmployerId(1L)).thenReturn(0L);

        EmployerDashboardDTO dashboard = employerService.getDashboard(EMAIL);

        assertNotNull(dashboard);
        assertEquals(0L, dashboard.getTotalJobs());
        assertEquals(0L, dashboard.getActiveJobs());
        assertEquals(0L, dashboard.getDraftJobs());
        assertEquals(0L, dashboard.getClosedJobs());
        assertEquals(0L, dashboard.getTotalApplicationsReceived());
    }

    @Test
    void shouldThrowWhenUserNotFoundOnGetDashboard() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> employerService.getDashboard(EMAIL));
    }

    @Test
    void shouldThrowWhenProfileNotFoundOnGetDashboard() {
        User user = createTestUser();

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(employerRepository.findByUserId(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> employerService.getDashboard(EMAIL));
    }
}
