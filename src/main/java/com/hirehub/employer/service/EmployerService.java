package com.hirehub.employer.service;

import org.springframework.stereotype.Service;

import com.hirehub.application.repository.ApplicationRepository;
import com.hirehub.auth.entity.User;
import com.hirehub.auth.repository.UserRepository;
import com.hirehub.common.exception.DuplicateResourceException;
import com.hirehub.common.exception.ResourceNotFoundException;
import com.hirehub.employer.dto.EmployerDashboardDTO;
import com.hirehub.employer.dto.EmployerRequestDTO;
import com.hirehub.employer.dto.EmployerResponseDTO;
import com.hirehub.employer.entity.Employer;
import com.hirehub.employer.repository.EmployerRepository;
import com.hirehub.job.entity.JobStatus;
import com.hirehub.job.repository.JobRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployerService {

    private final EmployerRepository employerRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;

    public EmployerResponseDTO createProfile(String email, EmployerRequestDTO request) {
        log.info("Creating employer profile for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", email);
                    return new ResourceNotFoundException("User not found with email: " + email);
                });

        if (employerRepository.existsByUserId(user.getId())) {
            log.warn("Employer profile already exists for user: {}", email);
            throw new DuplicateResourceException("Employer profile already exists for user: " + email);
        }

        Employer employer = new Employer();
        employer.setUser(user);
        employer.setCompanyName(request.getCompanyName());
        employer.setCompanyDescription(request.getCompanyDescription());
        employer.setCompanyWebsite(request.getCompanyWebsite());
        employer.setIndustry(request.getIndustry());
        employer.setLocation(request.getLocation());

        Employer saved = employerRepository.save(employer);
        log.info("Employer profile created successfully with id: {} for user: {}", saved.getId(), email);

        return mapToResponse(saved);
    }

    public EmployerResponseDTO getProfile(String email) {
        log.info("Fetching employer profile for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", email);
                    return new ResourceNotFoundException("User not found with email: " + email);
                });

        Employer employer = employerRepository.findByUserId(user.getId())
                .orElseThrow(() -> {
                    log.warn("Employer profile not found for user: {}", email);
                    return new ResourceNotFoundException("Employer profile not found for user: " + email);
                });

        return mapToResponse(employer);
    }

    public EmployerResponseDTO getProfileById(Long id) {
        log.info("Fetching employer profile by id: {}", id);

        Employer employer = employerRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Employer not found with id: {}", id);
                    return new ResourceNotFoundException("Employer not found with id: " + id);
                });

        return mapToResponse(employer);
    }

    public EmployerResponseDTO updateProfile(String email, EmployerRequestDTO request) {
        log.info("Updating employer profile for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", email);
                    return new ResourceNotFoundException("User not found with email: " + email);
                });

        Employer employer = employerRepository.findByUserId(user.getId())
                .orElseThrow(() -> {
                    log.warn("Employer profile not found for user: {}", email);
                    return new ResourceNotFoundException("Employer profile not found for user: " + email);
                });

        employer.setCompanyName(request.getCompanyName());
        employer.setCompanyDescription(request.getCompanyDescription());
        employer.setCompanyWebsite(request.getCompanyWebsite());
        employer.setIndustry(request.getIndustry());
        employer.setLocation(request.getLocation());

        Employer updated = employerRepository.save(employer);
        log.info("Employer profile updated successfully for user: {}", email);

        return mapToResponse(updated);
    }

    public EmployerDashboardDTO getDashboard(String email) {
        log.info("Fetching dashboard stats for employer: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", email);
                    return new ResourceNotFoundException("User not found with email: " + email);
                });

        Employer employer = employerRepository.findByUserId(user.getId())
                .orElseThrow(() -> {
                    log.warn("Employer profile not found for user: {}", email);
                    return new ResourceNotFoundException("Employer profile not found for user: " + email);
                });

        long totalJobs = jobRepository.countByEmployerId(employer.getId());
        long activeJobs = jobRepository.countByEmployerIdAndStatus(employer.getId(), JobStatus.ACTIVE);
        long draftJobs = jobRepository.countByEmployerIdAndStatus(employer.getId(), JobStatus.DRAFT);
        long closedJobs = jobRepository.countByEmployerIdAndStatus(employer.getId(), JobStatus.CLOSED);
        long totalApplications = applicationRepository.countByJobEmployerId(employer.getId());

        log.info("Dashboard stats retrieved for employer: {} - totalJobs={}, activeJobs={}, totalApplications={}",
                email, totalJobs, activeJobs, totalApplications);

        return EmployerDashboardDTO.builder()
                .totalJobs(totalJobs)
                .activeJobs(activeJobs)
                .draftJobs(draftJobs)
                .closedJobs(closedJobs)
                .totalApplicationsReceived(totalApplications)
                .build();
    }

    private EmployerResponseDTO mapToResponse(Employer employer) {
        return EmployerResponseDTO.builder()
                .id(employer.getId())
                .userId(employer.getUser().getId())
                .name(employer.getUser().getName())
                .email(employer.getUser().getEmail())
                .companyName(employer.getCompanyName())
                .companyDescription(employer.getCompanyDescription())
                .companyWebsite(employer.getCompanyWebsite())
                .industry(employer.getIndustry())
                .location(employer.getLocation())
                .createdAt(employer.getCreatedAt())
                .updatedAt(employer.getUpdatedAt())
                .build();
    }
}
