package com.jobportal.candidate.service;

import org.springframework.stereotype.Service;

import com.jobportal.auth.entity.User;
import com.jobportal.auth.repository.UserRepository;
import com.jobportal.candidate.dto.CandidateRequestDTO;
import com.jobportal.candidate.dto.CandidateResponseDTO;
import com.jobportal.candidate.entity.Candidate;
import com.jobportal.candidate.repository.CandidateRepository;
import com.jobportal.common.exception.DuplicateResourceException;
import com.jobportal.common.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final UserRepository userRepository;

    public CandidateResponseDTO createProfile(String email, CandidateRequestDTO request) {
        log.info("Creating candidate profile for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", email);
                    return new ResourceNotFoundException("User not found with email: " + email);
                });

        if (candidateRepository.existsByUserId(user.getId())) {
            log.warn("Candidate profile already exists for user: {}", email);
            throw new DuplicateResourceException("Candidate profile already exists for user: " + email);
        }

        Candidate candidate = new Candidate();
        candidate.setUser(user);
        candidate.setHeadline(request.getHeadline());
        candidate.setSummary(request.getSummary());
        candidate.setSkills(request.getSkills());
        candidate.setExperienceYears(request.getExperienceYears());
        candidate.setEducation(request.getEducation());
        candidate.setResumeUrl(request.getResumeUrl());
        candidate.setPhone(request.getPhone());
        candidate.setLocation(request.getLocation());

        Candidate saved = candidateRepository.save(candidate);
        log.info("Candidate profile created successfully with id: {} for user: {}", saved.getId(), email);

        return mapToResponse(saved);
    }

    public CandidateResponseDTO getProfile(String email) {
        log.info("Fetching candidate profile for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", email);
                    return new ResourceNotFoundException("User not found with email: " + email);
                });

        Candidate candidate = candidateRepository.findByUserId(user.getId())
                .orElseThrow(() -> {
                    log.warn("Candidate profile not found for user: {}", email);
                    return new ResourceNotFoundException("Candidate profile not found for user: " + email);
                });

        return mapToResponse(candidate);
    }

    public CandidateResponseDTO getProfileById(Long id) {
        log.info("Fetching candidate profile by id: {}", id);

        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Candidate not found with id: {}", id);
                    return new ResourceNotFoundException("Candidate not found with id: " + id);
                });

        return mapToResponse(candidate);
    }

    public CandidateResponseDTO updateProfile(String email, CandidateRequestDTO request) {
        log.info("Updating candidate profile for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", email);
                    return new ResourceNotFoundException("User not found with email: " + email);
                });

        Candidate candidate = candidateRepository.findByUserId(user.getId())
                .orElseThrow(() -> {
                    log.warn("Candidate profile not found for user: {}", email);
                    return new ResourceNotFoundException("Candidate profile not found for user: " + email);
                });

        candidate.setHeadline(request.getHeadline());
        candidate.setSummary(request.getSummary());
        candidate.setSkills(request.getSkills());
        candidate.setExperienceYears(request.getExperienceYears());
        candidate.setEducation(request.getEducation());
        candidate.setResumeUrl(request.getResumeUrl());
        candidate.setPhone(request.getPhone());
        candidate.setLocation(request.getLocation());

        Candidate updated = candidateRepository.save(candidate);
        log.info("Candidate profile updated successfully for user: {}", email);

        return mapToResponse(updated);
    }

    private CandidateResponseDTO mapToResponse(Candidate candidate) {
        return CandidateResponseDTO.builder()
                .id(candidate.getId())
                .userId(candidate.getUser().getId())
                .name(candidate.getUser().getName())
                .email(candidate.getUser().getEmail())
                .headline(candidate.getHeadline())
                .summary(candidate.getSummary())
                .skills(candidate.getSkills())
                .experienceYears(candidate.getExperienceYears())
                .education(candidate.getEducation())
                .resumeUrl(candidate.getResumeUrl())
                .phone(candidate.getPhone())
                .location(candidate.getLocation())
                .createdAt(candidate.getCreatedAt())
                .updatedAt(candidate.getUpdatedAt())
                .build();
    }
}
