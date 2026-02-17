package com.jobportal.job.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.jobportal.common.exception.ResourceNotFoundException;
import com.jobportal.job.dto.JobRequestDTO;
import com.jobportal.job.dto.JobResponseDTO;
import com.jobportal.job.entity.Job;
import com.jobportal.job.entity.JobStatus;
import com.jobportal.job.repository.JobRepository;
import com.jobportal.job.repository.JobSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;

    public JobResponseDTO createJob(JobRequestDTO request) {

        Job job = new Job();
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setLocation(request.getLocation());
        job.setSalaryMin(request.getSalaryMin());
        job.setSalaryMax(request.getSalaryMax());
        job.setStatus(request.getStatus() != null ? request.getStatus() : JobStatus.ACTIVE);

        Job saved = jobRepository.save(job);

        return mapToResponse(saved);
    }

    public List<JobResponseDTO> getAllJobs() {
        return jobRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Page<JobResponseDTO> getAllJobs(Pageable pageable) {
        return jobRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    public Page<JobResponseDTO> searchJobs(String keyword, String location,
                                           Integer minSalary, Integer maxSalary,
                                           JobStatus status, Pageable pageable) {

        Specification<Job> spec = (root, query, cb) -> cb.conjunction();

        if (keyword != null && !keyword.isBlank()) {
            spec = spec.and(JobSpecification.titleContains(keyword));
        }
        if (location != null && !location.isBlank()) {
            spec = spec.and(JobSpecification.locationContains(location));
        }
        if (minSalary != null) {
            spec = spec.and(JobSpecification.salaryMinGreaterThanOrEqual(minSalary));
        }
        if (maxSalary != null) {
            spec = spec.and(JobSpecification.salaryMaxLessThanOrEqual(maxSalary));
        }
        if (status != null) {
            spec = spec.and(JobSpecification.statusEquals(status));
        }

        return jobRepository.findAll(spec, pageable)
                .map(this::mapToResponse);
    }

    public JobResponseDTO getJobById(Long id) {

        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));

        return mapToResponse(job);
    }

    public JobResponseDTO updateJob(Long id, JobRequestDTO request) {

        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));

        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setLocation(request.getLocation());
        job.setSalaryMin(request.getSalaryMin());
        job.setSalaryMax(request.getSalaryMax());
        if (request.getStatus() != null) {
            job.setStatus(request.getStatus());
        }

        Job updated = jobRepository.save(job);

        return mapToResponse(updated);
    }

    public void deleteJob(Long id) {

        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));

        jobRepository.delete(job);
    }

    private JobResponseDTO mapToResponse(Job job) {
        return JobResponseDTO.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .location(job.getLocation())
                .salaryMin(job.getSalaryMin())
                .salaryMax(job.getSalaryMax())
                .status(job.getStatus())
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }
}
