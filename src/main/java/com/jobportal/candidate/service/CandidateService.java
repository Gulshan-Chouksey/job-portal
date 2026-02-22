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

@Service
@RequiredArgsConstructor
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final UserRepository userRepository;

    public CandidateResponseDTO createProfile(String email, CandidateRequestDTO request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        if (candidateRepository.existsByUserId(user.getId())) {
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

        return mapToResponse(saved);
    }

    public CandidateResponseDTO getProfile(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        Candidate candidate = candidateRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Candidate profile not found for user: " + email));

        return mapToResponse(candidate);
    }

    public CandidateResponseDTO getProfileById(Long id) {

        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found with id: " + id));

        return mapToResponse(candidate);
    }

    public CandidateResponseDTO updateProfile(String email, CandidateRequestDTO request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        Candidate candidate = candidateRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Candidate profile not found for user: " + email));

        candidate.setHeadline(request.getHeadline());
        candidate.setSummary(request.getSummary());
        candidate.setSkills(request.getSkills());
        candidate.setExperienceYears(request.getExperienceYears());
        candidate.setEducation(request.getEducation());
        candidate.setResumeUrl(request.getResumeUrl());
        candidate.setPhone(request.getPhone());
        candidate.setLocation(request.getLocation());

        Candidate updated = candidateRepository.save(candidate);

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
