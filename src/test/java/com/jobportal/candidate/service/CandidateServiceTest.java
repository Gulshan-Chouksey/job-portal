package com.jobportal.candidate.service;

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

import com.jobportal.auth.entity.Role;
import com.jobportal.auth.entity.User;
import com.jobportal.auth.repository.UserRepository;
import com.jobportal.candidate.dto.CandidateRequestDTO;
import com.jobportal.candidate.dto.CandidateResponseDTO;
import com.jobportal.candidate.entity.Candidate;
import com.jobportal.candidate.repository.CandidateRepository;
import com.jobportal.common.exception.DuplicateResourceException;
import com.jobportal.common.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class CandidateServiceTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 1, 1, 12, 0);
    private static final String EMAIL = "candidate@test.com";

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CandidateService candidateService;

    private User createTestUser() {
        return User.builder()
                .id(1L)
                .name("Test Candidate")
                .email(EMAIL)
                .password("encoded")
                .role(Role.CANDIDATE)
                .createdAt(NOW)
                .updatedAt(NOW)
                .build();
    }

    private CandidateRequestDTO createTestRequest() {
        return new CandidateRequestDTO(
                "Java Developer",
                "Experienced backend developer",
                "Java, Spring, SQL",
                5,
                "B.Tech Computer Science",
                "https://resume.com/test.pdf",
                "+1234567890",
                "New York"
        );
    }

    private Candidate createTestCandidate(User user) {
        return Candidate.builder()
                .id(1L)
                .user(user)
                .headline("Java Developer")
                .summary("Experienced backend developer")
                .skills("Java, Spring, SQL")
                .experienceYears(5)
                .education("B.Tech Computer Science")
                .resumeUrl("https://resume.com/test.pdf")
                .phone("+1234567890")
                .location("New York")
                .createdAt(NOW)
                .updatedAt(NOW)
                .build();
    }

    // ── CREATE PROFILE ──────────────────────────────────────────────────

    @Test
    void shouldCreateProfileSuccessfully() {
        User user = createTestUser();
        CandidateRequestDTO request = createTestRequest();
        Candidate saved = createTestCandidate(user);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(candidateRepository.existsByUserId(1L)).thenReturn(false);
        when(candidateRepository.save(any(Candidate.class))).thenReturn(saved);

        CandidateResponseDTO response = candidateService.createProfile(EMAIL, request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Java Developer", response.getHeadline());
        assertEquals("Java, Spring, SQL", response.getSkills());
        assertEquals(5, response.getExperienceYears());
        assertEquals("Test Candidate", response.getName());
        assertEquals(EMAIL, response.getEmail());

        verify(candidateRepository).save(any(Candidate.class));
    }

    @Test
    void shouldThrowWhenProfileAlreadyExists() {
        User user = createTestUser();
        CandidateRequestDTO request = createTestRequest();

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(candidateRepository.existsByUserId(1L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> candidateService.createProfile(EMAIL, request));
    }

    @Test
    void shouldThrowWhenUserNotFoundOnCreate() {
        CandidateRequestDTO request = createTestRequest();

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> candidateService.createProfile(EMAIL, request));
    }

    // ── GET PROFILE ─────────────────────────────────────────────────────

    @Test
    void shouldGetProfileByEmail() {
        User user = createTestUser();
        Candidate candidate = createTestCandidate(user);

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(candidateRepository.findByUserId(1L)).thenReturn(Optional.of(candidate));

        CandidateResponseDTO response = candidateService.getProfile(EMAIL);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Java Developer", response.getHeadline());
    }

    @Test
    void shouldThrowWhenUserNotFoundOnGetProfile() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> candidateService.getProfile(EMAIL));
    }

    @Test
    void shouldThrowWhenProfileNotFoundOnGetProfile() {
        User user = createTestUser();

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(candidateRepository.findByUserId(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> candidateService.getProfile(EMAIL));
    }

    // ── GET PROFILE BY ID ───────────────────────────────────────────────

    @Test
    void shouldGetProfileById() {
        User user = createTestUser();
        Candidate candidate = createTestCandidate(user);

        when(candidateRepository.findById(1L)).thenReturn(Optional.of(candidate));

        CandidateResponseDTO response = candidateService.getProfileById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Java Developer", response.getHeadline());
    }

    @Test
    void shouldThrowWhenCandidateNotFoundById() {
        when(candidateRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> candidateService.getProfileById(99L));
    }

    // ── UPDATE PROFILE ──────────────────────────────────────────────────

    @Test
    void shouldUpdateProfileSuccessfully() {
        User user = createTestUser();
        Candidate existing = createTestCandidate(user);

        CandidateRequestDTO updateRequest = new CandidateRequestDTO(
                "Senior Java Dev",
                "Updated summary",
                "Java, Spring, Kafka, Docker",
                8,
                "M.Tech Computer Science",
                "https://resume.com/updated.pdf",
                "+9876543210",
                "London"
        );

        Candidate updated = Candidate.builder()
                .id(1L)
                .user(user)
                .headline("Senior Java Dev")
                .summary("Updated summary")
                .skills("Java, Spring, Kafka, Docker")
                .experienceYears(8)
                .education("M.Tech Computer Science")
                .resumeUrl("https://resume.com/updated.pdf")
                .phone("+9876543210")
                .location("London")
                .createdAt(NOW)
                .updatedAt(NOW)
                .build();

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(candidateRepository.findByUserId(1L)).thenReturn(Optional.of(existing));
        when(candidateRepository.save(any(Candidate.class))).thenReturn(updated);

        CandidateResponseDTO response = candidateService.updateProfile(EMAIL, updateRequest);

        assertNotNull(response);
        assertEquals("Senior Java Dev", response.getHeadline());
        assertEquals(8, response.getExperienceYears());
        assertEquals("London", response.getLocation());

        verify(candidateRepository).save(any(Candidate.class));
    }

    @Test
    void shouldThrowWhenUserNotFoundOnUpdate() {
        CandidateRequestDTO request = createTestRequest();

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> candidateService.updateProfile(EMAIL, request));
    }

    @Test
    void shouldThrowWhenProfileNotFoundOnUpdate() {
        User user = createTestUser();
        CandidateRequestDTO request = createTestRequest();

        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(candidateRepository.findByUserId(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> candidateService.updateProfile(EMAIL, request));
    }
}
