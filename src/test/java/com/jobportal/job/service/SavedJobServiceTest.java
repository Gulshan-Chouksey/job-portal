package com.jobportal.job.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.jobportal.auth.entity.Role;
import com.jobportal.auth.entity.User;
import com.jobportal.auth.repository.UserRepository;
import com.jobportal.candidate.entity.Candidate;
import com.jobportal.candidate.repository.CandidateRepository;
import com.jobportal.common.exception.BadRequestException;
import com.jobportal.common.exception.DuplicateResourceException;
import com.jobportal.common.exception.ResourceNotFoundException;
import com.jobportal.job.dto.SavedJobResponseDTO;
import com.jobportal.job.entity.Job;
import com.jobportal.job.entity.JobStatus;
import com.jobportal.job.entity.SavedJob;
import com.jobportal.job.repository.JobRepository;
import com.jobportal.job.repository.SavedJobRepository;

@ExtendWith(MockitoExtension.class)
class SavedJobServiceTest {

    @Mock
    private SavedJobRepository savedJobRepository;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SavedJobService savedJobService;

    private User user;
    private Candidate candidate;
    private Job job;
    private SavedJob savedJob;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Test User")
                .email("candidate@test.com")
                .password("password")
                .role(Role.CANDIDATE)
                .build();

        candidate = new Candidate();
        candidate.setId(1L);
        candidate.setUser(user);
        candidate.setHeadline("Developer");

        job = new Job();
        job.setId(1L);
        job.setTitle("Java Developer");
        job.setLocation("Bangalore");
        job.setSalaryMin(50000);
        job.setSalaryMax(100000);
        job.setStatus(JobStatus.ACTIVE);

        savedJob = SavedJob.builder()
                .id(1L)
                .candidate(candidate)
                .job(job)
                .savedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void saveJob_Success() {
        when(userRepository.findByEmail("candidate@test.com")).thenReturn(Optional.of(user));
        when(candidateRepository.findByUserId(1L)).thenReturn(Optional.of(candidate));
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(savedJobRepository.existsByCandidateIdAndJobId(1L, 1L)).thenReturn(false);
        when(savedJobRepository.save(any(SavedJob.class))).thenReturn(savedJob);

        SavedJobResponseDTO response = savedJobService.saveJob("candidate@test.com", 1L);

        assertNotNull(response);
        assertEquals(1L, response.getJobId());
        assertEquals("Java Developer", response.getJobTitle());
        verify(savedJobRepository).save(any(SavedJob.class));
    }

    @Test
    void saveJob_JobNotFound_ThrowsException() {
        when(userRepository.findByEmail("candidate@test.com")).thenReturn(Optional.of(user));
        when(candidateRepository.findByUserId(1L)).thenReturn(Optional.of(candidate));
        when(jobRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> savedJobService.saveJob("candidate@test.com", 99L));
    }

    @Test
    void saveJob_AlreadySaved_ThrowsException() {
        when(userRepository.findByEmail("candidate@test.com")).thenReturn(Optional.of(user));
        when(candidateRepository.findByUserId(1L)).thenReturn(Optional.of(candidate));
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(savedJobRepository.existsByCandidateIdAndJobId(1L, 1L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> savedJobService.saveJob("candidate@test.com", 1L));
    }

    @Test
    void saveJob_NoCandidateProfile_ThrowsException() {
        when(userRepository.findByEmail("candidate@test.com")).thenReturn(Optional.of(user));
        when(candidateRepository.findByUserId(1L)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class,
                () -> savedJobService.saveJob("candidate@test.com", 1L));
    }

    @Test
    void getMySavedJobs_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<SavedJob> page = new PageImpl<>(List.of(savedJob));

        when(userRepository.findByEmail("candidate@test.com")).thenReturn(Optional.of(user));
        when(candidateRepository.findByUserId(1L)).thenReturn(Optional.of(candidate));
        when(savedJobRepository.findByCandidateId(1L, pageable)).thenReturn(page);

        Page<SavedJobResponseDTO> result = savedJobService.getMySavedJobs("candidate@test.com", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Java Developer", result.getContent().get(0).getJobTitle());
    }

    @Test
    void unsaveJob_Success() {
        when(userRepository.findByEmail("candidate@test.com")).thenReturn(Optional.of(user));
        when(candidateRepository.findByUserId(1L)).thenReturn(Optional.of(candidate));
        when(savedJobRepository.existsByCandidateIdAndJobId(1L, 1L)).thenReturn(true);

        assertDoesNotThrow(() -> savedJobService.unsaveJob("candidate@test.com", 1L));
        verify(savedJobRepository).deleteByCandidateIdAndJobId(1L, 1L);
    }

    @Test
    void unsaveJob_NotFound_ThrowsException() {
        when(userRepository.findByEmail("candidate@test.com")).thenReturn(Optional.of(user));
        when(candidateRepository.findByUserId(1L)).thenReturn(Optional.of(candidate));
        when(savedJobRepository.existsByCandidateIdAndJobId(1L, 99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> savedJobService.unsaveJob("candidate@test.com", 99L));
    }

    @Test
    void isJobSaved_ReturnsTrue() {
        when(userRepository.findByEmail("candidate@test.com")).thenReturn(Optional.of(user));
        when(candidateRepository.findByUserId(1L)).thenReturn(Optional.of(candidate));
        when(savedJobRepository.existsByCandidateIdAndJobId(1L, 1L)).thenReturn(true);

        assertTrue(savedJobService.isJobSaved("candidate@test.com", 1L));
    }

    @Test
    void isJobSaved_ReturnsFalse() {
        when(userRepository.findByEmail("candidate@test.com")).thenReturn(Optional.of(user));
        when(candidateRepository.findByUserId(1L)).thenReturn(Optional.of(candidate));
        when(savedJobRepository.existsByCandidateIdAndJobId(1L, 99L)).thenReturn(false);

        assertFalse(savedJobService.isJobSaved("candidate@test.com", 99L));
    }

    @Test
    void saveJob_UserNotFound_ThrowsException() {
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> savedJobService.saveJob("unknown@test.com", 1L));
    }
}
