package com.jobportal.application.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.jobportal.application.dto.ApplicationRequestDTO;
import com.jobportal.application.dto.ApplicationResponseDTO;
import com.jobportal.application.dto.StatusUpdateDTO;
import com.jobportal.application.entity.ApplicationStatus;
import com.jobportal.application.entity.JobApplication;
import com.jobportal.application.repository.ApplicationRepository;
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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final CandidateRepository candidateRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public ApplicationResponseDTO apply(String email, ApplicationRequestDTO request) {
        log.info("User '{}' applying for job id: {}", email, request.getJobId());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", email);
                    return new ResourceNotFoundException("User not found with email: " + email);
                });

        Candidate candidate = candidateRepository.findByUserId(user.getId())
                .orElseThrow(() -> {
                    log.warn("Candidate profile not found for user: {}", email);
                    return new BadRequestException(
                        "Please create a candidate profile before applying");
                });

        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> {
                    log.warn("Job not found with id: {}", request.getJobId());
                    return new ResourceNotFoundException(
                        "Job not found with id: " + request.getJobId());
                });

        if (job.getStatus() != JobStatus.ACTIVE) {
            log.warn("Cannot apply - job id: {} is not active (status: {})", job.getId(), job.getStatus());
            throw new BadRequestException("Cannot apply to a job that is not active");
        }

        if (applicationRepository.existsByCandidateIdAndJobId(candidate.getId(), job.getId())) {
            log.warn("Duplicate application - user '{}' already applied for job id: {}", email, job.getId());
            throw new DuplicateResourceException("You have already applied for this job");
        }

        JobApplication application = JobApplication.builder()
                .candidate(candidate)
                .job(job)
                .coverLetter(request.getCoverLetter())
                .status(ApplicationStatus.PENDING)
                .build();

        JobApplication saved = applicationRepository.save(application);
        log.info("Application created successfully with id: {} for job id: {}", saved.getId(), request.getJobId());
        return mapToResponse(saved);
    }

    public Page<ApplicationResponseDTO> getMyApplications(String email, Pageable pageable) {
        log.info("Fetching applications for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", email);
                    return new ResourceNotFoundException("User not found with email: " + email);
                });

        Candidate candidate = candidateRepository.findByUserId(user.getId())
                .orElseThrow(() -> {
                    log.warn("Candidate profile not found for user: {}", email);
                    return new ResourceNotFoundException(
                        "Candidate profile not found for user: " + email);
                });

        return applicationRepository.findByCandidateId(candidate.getId(), pageable)
                .map(this::mapToResponse);
    }

    public Page<ApplicationResponseDTO> getApplicationsForJob(Long jobId, Pageable pageable) {
        log.info("Fetching applications for job id: {}", jobId);
        if (!jobRepository.existsById(jobId)) {
            log.warn("Job not found with id: {}", jobId);
            throw new ResourceNotFoundException("Job not found with id: " + jobId);
        }
        return applicationRepository.findByJobId(jobId, pageable)
                .map(this::mapToResponse);
    }

    public Page<ApplicationResponseDTO> getApplicationsForEmployer(String email, Pageable pageable) {
        log.info("Fetching applications for employer user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", email);
                    return new ResourceNotFoundException("User not found with email: " + email);
                });

        var employer = user.getId();
        // find employer by user id
        return applicationRepository.findByJobEmployerId(employer, pageable)
                .map(this::mapToResponse);
    }

    public ApplicationResponseDTO getApplicationById(Long id) {
        log.info("Fetching application by id: {}", id);
        JobApplication application = applicationRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Application not found with id: {}", id);
                    return new ResourceNotFoundException(
                        "Application not found with id: " + id);
                });
        return mapToResponse(application);
    }

    public ApplicationResponseDTO updateStatus(Long id, StatusUpdateDTO request) {
        log.info("Updating application status - id: {}, new status: {}", id, request.getStatus());

        JobApplication application = applicationRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Application not found with id: {}", id);
                    return new ResourceNotFoundException(
                        "Application not found with id: " + id);
                });

        application.setStatus(request.getStatus());
        JobApplication updated = applicationRepository.save(application);
        log.info("Application status updated successfully - id: {}, status: {}", id, request.getStatus());

        return mapToResponse(updated);
    }

    public ApplicationResponseDTO withdraw(Long id, String email) {
        log.info("Withdrawing application id: {} by user: {}", id, email);

        JobApplication application = applicationRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Application not found with id: {}", id);
                    return new ResourceNotFoundException(
                        "Application not found with id: " + id);
                });

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", email);
                    return new ResourceNotFoundException("User not found with email: " + email);
                });

        Candidate candidate = candidateRepository.findByUserId(user.getId())
                .orElseThrow(() -> {
                    log.warn("Candidate profile not found for user: {}", email);
                    return new ResourceNotFoundException(
                        "Candidate profile not found for user: " + email);
                });

        if (!application.getCandidate().getId().equals(candidate.getId())) {
            log.warn("Unauthorized withdrawal attempt - user '{}' tried to withdraw application id: {}", email, id);
            throw new BadRequestException("You can only withdraw your own applications");
        }

        if (application.getStatus() == ApplicationStatus.WITHDRAWN) {
            log.warn("Application id: {} is already withdrawn", id);
            throw new BadRequestException("Application is already withdrawn");
        }

        application.setStatus(ApplicationStatus.WITHDRAWN);
        JobApplication updated = applicationRepository.save(application);
        log.info("Application withdrawn successfully - id: {}", id);

        return mapToResponse(updated);
    }

    private ApplicationResponseDTO mapToResponse(JobApplication app) {
        ApplicationResponseDTO.ApplicationResponseDTOBuilder builder = ApplicationResponseDTO.builder()
                .id(app.getId())
                .candidateId(app.getCandidate().getId())
                .candidateName(app.getCandidate().getUser().getName())
                .candidateEmail(app.getCandidate().getUser().getEmail())
                .jobId(app.getJob().getId())
                .jobTitle(app.getJob().getTitle())
                .coverLetter(app.getCoverLetter())
                .status(app.getStatus())
                .appliedAt(app.getAppliedAt())
                .updatedAt(app.getUpdatedAt());

        if (app.getCandidate().getHeadline() != null) {
            builder.candidateHeadline(app.getCandidate().getHeadline());
        }

        if (app.getJob().getEmployer() != null) {
            builder.companyName(app.getJob().getEmployer().getCompanyName());
        }

        return builder.build();
    }
}
