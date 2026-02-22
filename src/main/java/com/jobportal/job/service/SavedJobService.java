package com.jobportal.job.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobportal.auth.entity.User;
import com.jobportal.auth.repository.UserRepository;
import com.jobportal.candidate.entity.Candidate;
import com.jobportal.candidate.repository.CandidateRepository;
import com.jobportal.common.exception.BadRequestException;
import com.jobportal.common.exception.DuplicateResourceException;
import com.jobportal.common.exception.ResourceNotFoundException;
import com.jobportal.job.dto.SavedJobResponseDTO;
import com.jobportal.job.entity.Job;
import com.jobportal.job.entity.SavedJob;
import com.jobportal.job.repository.JobRepository;
import com.jobportal.job.repository.SavedJobRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SavedJobService {

    private final SavedJobRepository savedJobRepository;
    private final JobRepository jobRepository;
    private final CandidateRepository candidateRepository;
    private final UserRepository userRepository;

    public SavedJobResponseDTO saveJob(String email, Long jobId) {
        log.info("User '{}' saving job id: {}", email, jobId);

        Candidate candidate = getCandidateByEmail(email);

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> {
                    log.warn("Job not found with id: {}", jobId);
                    return new ResourceNotFoundException("Job not found with id: " + jobId);
                });

        if (savedJobRepository.existsByCandidateIdAndJobId(candidate.getId(), jobId)) {
            log.warn("Job id: {} already saved by user '{}'", jobId, email);
            throw new DuplicateResourceException("Job is already saved");
        }

        SavedJob savedJob = SavedJob.builder()
                .candidate(candidate)
                .job(job)
                .build();

        SavedJob saved = savedJobRepository.save(savedJob);
        log.info("Job saved successfully - savedJob id: {}, job id: {}", saved.getId(), jobId);

        return mapToResponse(saved);
    }

    public Page<SavedJobResponseDTO> getMySavedJobs(String email, Pageable pageable) {
        log.info("Fetching saved jobs for user: {}", email);

        Candidate candidate = getCandidateByEmail(email);

        return savedJobRepository.findByCandidateId(candidate.getId(), pageable)
                .map(this::mapToResponse);
    }

    @Transactional
    public void unsaveJob(String email, Long jobId) {
        log.info("User '{}' unsaving job id: {}", email, jobId);

        Candidate candidate = getCandidateByEmail(email);

        if (!savedJobRepository.existsByCandidateIdAndJobId(candidate.getId(), jobId)) {
            log.warn("Saved job not found - candidate id: {}, job id: {}", candidate.getId(), jobId);
            throw new ResourceNotFoundException("Saved job not found");
        }

        savedJobRepository.deleteByCandidateIdAndJobId(candidate.getId(), jobId);
        log.info("Job unsaved successfully - job id: {} by user '{}'", jobId, email);
    }

    public boolean isJobSaved(String email, Long jobId) {
        Candidate candidate = getCandidateByEmail(email);
        return savedJobRepository.existsByCandidateIdAndJobId(candidate.getId(), jobId);
    }

    private Candidate getCandidateByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", email);
                    return new ResourceNotFoundException("User not found with email: " + email);
                });

        return candidateRepository.findByUserId(user.getId())
                .orElseThrow(() -> {
                    log.warn("Candidate profile not found for user: {}", email);
                    return new BadRequestException("Please create a candidate profile first");
                });
    }

    private SavedJobResponseDTO mapToResponse(SavedJob savedJob) {
        SavedJobResponseDTO.SavedJobResponseDTOBuilder builder = SavedJobResponseDTO.builder()
                .id(savedJob.getId())
                .jobId(savedJob.getJob().getId())
                .jobTitle(savedJob.getJob().getTitle())
                .location(savedJob.getJob().getLocation())
                .salaryMin(savedJob.getJob().getSalaryMin())
                .salaryMax(savedJob.getJob().getSalaryMax())
                .status(savedJob.getJob().getStatus().name())
                .savedAt(savedJob.getSavedAt());

        if (savedJob.getJob().getEmployer() != null) {
            builder.companyName(savedJob.getJob().getEmployer().getCompanyName());
        }

        return builder.build();
    }
}
