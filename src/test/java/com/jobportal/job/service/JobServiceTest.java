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
        Job job = new Job(null, "Java Developer", "Backend role", "Remote", 50000, 80000);

        when(jobRepository.save(any(Job.class))).thenReturn(job);

        Job savedJob = jobService.createJob(job);

        assertNotNull(savedJob);
        assertEquals("Java Developer", savedJob.getTitle());
        verify(jobRepository, times(1)).save(job);
    }

    @Test
    void shouldReturnAllJobs() {
        Job job = new Job(null, "Java Dev", "Backend", "Remote", 50000, 80000);

        when(jobRepository.findAll()).thenReturn(List.of(job));

        List<Job> jobs = jobService.getAllJobs();

        assertEquals(1, jobs.size());
        verify(jobRepository, times(1)).findAll();
    }
}

