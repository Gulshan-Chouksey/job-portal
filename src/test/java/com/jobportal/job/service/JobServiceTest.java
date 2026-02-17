package com.jobportal.job.service;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.jobportal.common.exception.ResourceNotFoundException;
import com.jobportal.job.dto.JobRequestDTO;
import com.jobportal.job.dto.JobResponseDTO;
import com.jobportal.job.entity.Job;
import com.jobportal.job.repository.JobRepository;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 1, 1, 12, 0);

    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private JobService jobService;

    @Test
    void shouldCreateJobSuccessfully() {

        JobRequestDTO request = new JobRequestDTO(
                "Java Developer",
                "Backend role",
                "Remote",
                50000,
                80000
        );

        Job savedJob = new Job(
                1L,
                "Java Developer",
                "Backend role",
                "Remote",
                50000,
                80000,
                NOW,
                NOW
        );

        when(jobRepository.save(any(Job.class))).thenReturn(savedJob);

        JobResponseDTO response = jobService.createJob(request);

        assertNotNull(response);
        assertEquals("Java Developer", response.getTitle());
        assertEquals(1L, response.getId());

        verify(jobRepository, times(1)).save(any(Job.class));
    }

    @Test
    void shouldReturnAllJobs() {

        Job job = new Job(
                1L,
                "Java Dev",
                "Backend",
                "Remote",
                50000,
                80000,
                NOW,
                NOW
        );

        when(jobRepository.findAll()).thenReturn(List.of(job));

        List<JobResponseDTO> responses = jobService.getAllJobs();

        assertEquals(1, responses.size());
        assertEquals("Java Dev", responses.get(0).getTitle());

        verify(jobRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnPaginatedJobs() {

        Job job = new Job(
                1L,
                "Spring Dev",
                "Backend role",
                "Hybrid",
                60000,
                90000,
                NOW,
                NOW
        );

        Pageable pageable = PageRequest.of(0, 10);
        Page<Job> jobPage = new PageImpl<>(List.of(job), pageable, 1);

        when(jobRepository.findAll(pageable)).thenReturn(jobPage);

        Page<JobResponseDTO> result = jobService.getAllJobs(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("Spring Dev", result.getContent().get(0).getTitle());

        verify(jobRepository, times(1)).findAll(pageable);
    }

    @Test
    void shouldGetJobByIdSuccessfully() {

        Long jobId = 1L;

        Job job = new Job(
                jobId,
                "Java Dev",
                "Backend role",
                "Remote",
                50000,
                80000,
                NOW,
                NOW
        );

        when(jobRepository.findById(jobId)).thenReturn(java.util.Optional.of(job));

        JobResponseDTO response = jobService.getJobById(jobId);

        assertNotNull(response);
        assertEquals(jobId, response.getId());
        assertEquals("Java Dev", response.getTitle());
        assertEquals("Remote", response.getLocation());

        verify(jobRepository, times(1)).findById(jobId);
    }

    @Test
    void shouldThrowExceptionWhenGettingNonExistentJob() {

        Long jobId = 99L;

        when(jobRepository.findById(jobId)).thenReturn(java.util.Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> jobService.getJobById(jobId)
        );

        assertEquals("Job not found with id: 99", exception.getMessage());

        verify(jobRepository, times(1)).findById(jobId);
    }

    @Test
    void shouldUpdateJobSuccessfully() {

        Long jobId = 1L;

        Job existingJob = new Job(
                jobId,
                "Old Title",
                "Old Description",
                "Old Location",
                40000,
                60000,
                NOW,
                NOW
        );

        JobRequestDTO updateRequest = new JobRequestDTO(
                "Updated Title",
                "Updated Description",
                "Updated Location",
                70000,
                100000
        );

        Job updatedJob = new Job(
                jobId,
                "Updated Title",
                "Updated Description",
                "Updated Location",
                70000,
                100000,
                NOW,
                NOW
        );

        when(jobRepository.findById(jobId)).thenReturn(java.util.Optional.of(existingJob));
        when(jobRepository.save(any(Job.class))).thenReturn(updatedJob);

        JobResponseDTO response = jobService.updateJob(jobId, updateRequest);

        assertNotNull(response);
        assertEquals("Updated Title", response.getTitle());
        assertEquals("Updated Location", response.getLocation());
        assertEquals(70000, response.getSalaryMin());

        verify(jobRepository, times(1)).findById(jobId);
        verify(jobRepository, times(1)).save(any(Job.class));
    }

    @Test
    void shouldThrowExceptionWhenJobNotFound() {

        Long jobId = 99L;

        JobRequestDTO request = new JobRequestDTO(
                "Title",
                "Desc",
                "Location",
                50000,
                80000
        );

        when(jobRepository.findById(jobId)).thenReturn(java.util.Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> jobService.updateJob(jobId, request)
        );

        assertEquals("Job not found with id: 99", exception.getMessage());

        verify(jobRepository, times(1)).findById(jobId);
        verify(jobRepository, times(0)).save(any(Job.class));
    }

    @Test
    void shouldDeleteJobSuccessfully() {

        Long jobId = 1L;

        Job existingJob = new Job(
                jobId,
                "Java Dev",
                "Backend",
                "Remote",
                50000,
                80000,
                NOW,
                NOW
        );

        when(jobRepository.findById(jobId)).thenReturn(java.util.Optional.of(existingJob));

        jobService.deleteJob(jobId);

        verify(jobRepository, times(1)).findById(jobId);
        verify(jobRepository, times(1)).delete(existingJob);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentJob() {

        Long jobId = 99L;

        when(jobRepository.findById(jobId)).thenReturn(java.util.Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> jobService.deleteJob(jobId)
        );

        assertEquals("Job not found with id: 99", exception.getMessage());

        verify(jobRepository, times(1)).findById(jobId);
        verify(jobRepository, never()).delete(any(Job.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    void shouldSearchJobsByKeyword() {

        Job job = new Job(1L, "Java Developer", "Backend role", "Remote", 50000, 80000, NOW, NOW);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Job> jobPage = new PageImpl<>(List.of(job), pageable, 1);

        when(jobRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(jobPage);

        Page<JobResponseDTO> result = jobService.searchJobs("Java", null, null, null, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Java Developer", result.getContent().get(0).getTitle());

        verify(jobRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @SuppressWarnings("unchecked")
    @Test
    void shouldSearchJobsByLocationAndSalaryRange() {

        Job job = new Job(1L, "Python Dev", "ML role", "Bangalore", 70000, 100000, NOW, NOW);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Job> jobPage = new PageImpl<>(List.of(job), pageable, 1);

        when(jobRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(jobPage);

        Page<JobResponseDTO> result = jobService.searchJobs(null, "Bangalore", 60000, 110000, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Bangalore", result.getContent().get(0).getLocation());

        verify(jobRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @SuppressWarnings("unchecked")
    @Test
    void shouldReturnEmptyPageWhenNoJobsMatchSearch() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<Job> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(jobRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(emptyPage);

        Page<JobResponseDTO> result = jobService.searchJobs("Nonexistent", null, null, null, pageable);

        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getContent().size());

        verify(jobRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }
}
