package com.jobportal.job.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.jobportal.common.exception.ResourceNotFoundException;
import com.jobportal.employer.entity.Employer;
import com.jobportal.employer.repository.EmployerRepository;
import com.jobportal.job.dto.CategoryResponseDTO;
import com.jobportal.job.dto.JobRequestDTO;
import com.jobportal.job.dto.JobResponseDTO;
import com.jobportal.job.entity.Category;
import com.jobportal.job.entity.Job;
import com.jobportal.job.entity.JobStatus;
import com.jobportal.job.repository.CategoryRepository;
import com.jobportal.job.repository.JobRepository;
import com.jobportal.job.repository.JobSpecification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final EmployerRepository employerRepository;
    private final CategoryRepository categoryRepository;

    public JobResponseDTO createJob(JobRequestDTO request, Long employerId) {
        log.info("Creating job with title '{}' for employer id: {}", request.getTitle(), employerId);

        Employer employer = employerRepository.findById(employerId)
                .orElseThrow(() -> {
                    log.warn("Employer not found with id: {}", employerId);
                    return new ResourceNotFoundException("Employer not found with id: " + employerId);
                });

        Job job = new Job();
        job.setEmployer(employer);
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setLocation(request.getLocation());
        job.setSalaryMin(request.getSalaryMin());
        job.setSalaryMax(request.getSalaryMax());
        job.setStatus(request.getStatus() != null ? request.getStatus() : JobStatus.ACTIVE);

        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            Set<Category> categories = new HashSet<>(categoryRepository.findAllById(request.getCategoryIds()));
            job.setCategories(categories);
            log.debug("Assigned {} categories to job", categories.size());
        }

        Job saved = jobRepository.save(job);
        log.info("Job created successfully with id: {} for employer: {}", saved.getId(), employerId);

        return mapToResponse(saved);
    }

    public JobResponseDTO createJob(JobRequestDTO request) {
        log.info("Creating job with title '{}' (no employer)", request.getTitle());

        Job job = new Job();
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setLocation(request.getLocation());
        job.setSalaryMin(request.getSalaryMin());
        job.setSalaryMax(request.getSalaryMax());
        job.setStatus(request.getStatus() != null ? request.getStatus() : JobStatus.ACTIVE);

        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            Set<Category> categories = new HashSet<>(categoryRepository.findAllById(request.getCategoryIds()));
            job.setCategories(categories);
            log.debug("Assigned {} categories to job", categories.size());
        }

        Job saved = jobRepository.save(job);
        log.info("Job created successfully with id: {}", saved.getId());

        return mapToResponse(saved);
    }

    public List<JobResponseDTO> getAllJobs() {
        log.info("Fetching all jobs");
        return jobRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Page<JobResponseDTO> getAllJobs(Pageable pageable) {
        log.info("Fetching all jobs with pagination - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return jobRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    public Page<JobResponseDTO> searchJobs(String keyword, String location,
                                           Integer minSalary, Integer maxSalary,
                                           JobStatus status, Pageable pageable) {
        log.info("Searching jobs - keyword: '{}', location: '{}', salary: {}-{}, status: {}",
                keyword, location, minSalary, maxSalary, status);

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
        log.info("Fetching job with id: {}", id);

        Job job = jobRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Job not found with id: {}", id);
                    return new ResourceNotFoundException("Job not found with id: " + id);
                });

        return mapToResponse(job);
    }

    public JobResponseDTO updateJob(Long id, JobRequestDTO request) {
        log.info("Updating job with id: {}", id);

        Job job = jobRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Job not found with id: {}", id);
                    return new ResourceNotFoundException("Job not found with id: " + id);
                });

        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setLocation(request.getLocation());
        job.setSalaryMin(request.getSalaryMin());
        job.setSalaryMax(request.getSalaryMax());
        if (request.getStatus() != null) {
            job.setStatus(request.getStatus());
        }

        if (request.getCategoryIds() != null) {
            Set<Category> categories = new HashSet<>(categoryRepository.findAllById(request.getCategoryIds()));
            job.setCategories(categories);
            log.debug("Updated categories for job id: {}, count: {}", id, categories.size());
        }

        Job updated = jobRepository.save(job);
        log.info("Job updated successfully with id: {}", id);

        return mapToResponse(updated);
    }

    public void deleteJob(Long id) {
        log.info("Deleting job with id: {}", id);

        Job job = jobRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Job not found with id: {}", id);
                    return new ResourceNotFoundException("Job not found with id: " + id);
                });

        jobRepository.delete(job);
        log.info("Job deleted successfully with id: {}", id);
    }

    public Page<JobResponseDTO> getJobsByEmployer(Long employerId, Pageable pageable) {
        log.info("Fetching jobs for employer id: {}", employerId);
        return jobRepository.findByEmployerId(employerId, pageable)
                .map(this::mapToResponse);
    }

    private JobResponseDTO mapToResponse(Job job) {
        JobResponseDTO.JobResponseDTOBuilder builder = JobResponseDTO.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .location(job.getLocation())
                .salaryMin(job.getSalaryMin())
                .salaryMax(job.getSalaryMax())
                .status(job.getStatus())
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt());

        if (job.getEmployer() != null) {
            builder.employerId(job.getEmployer().getId())
                   .companyName(job.getEmployer().getCompanyName());
        }

        if (job.getCategories() != null && !job.getCategories().isEmpty()) {
            Set<CategoryResponseDTO> categoryDTOs = job.getCategories().stream()
                    .map(cat -> CategoryResponseDTO.builder()
                            .id(cat.getId())
                            .name(cat.getName())
                            .description(cat.getDescription())
                            .createdAt(cat.getCreatedAt())
                            .build())
                    .collect(Collectors.toSet());
            builder.categories(categoryDTOs);
        }

        return builder.build();
    }
}
