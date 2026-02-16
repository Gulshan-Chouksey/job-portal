package com.jobportal.job.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.jobportal.job.dto.JobRequestDTO;
import com.jobportal.job.dto.JobResponseDTO;
import com.jobportal.job.entity.Job;
import com.jobportal.job.repository.JobRepository;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

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
                80000
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
                80000
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
                90000
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
}
