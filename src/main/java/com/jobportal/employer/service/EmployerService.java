package com.jobportal.employer.service;

import org.springframework.stereotype.Service;

import com.jobportal.auth.entity.User;
import com.jobportal.auth.repository.UserRepository;
import com.jobportal.common.exception.DuplicateResourceException;
import com.jobportal.common.exception.ResourceNotFoundException;
import com.jobportal.employer.dto.EmployerRequestDTO;
import com.jobportal.employer.dto.EmployerResponseDTO;
import com.jobportal.employer.entity.Employer;
import com.jobportal.employer.repository.EmployerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployerService {

    private final EmployerRepository employerRepository;
    private final UserRepository userRepository;

    public EmployerResponseDTO createProfile(String email, EmployerRequestDTO request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        if (employerRepository.existsByUserId(user.getId())) {
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

        return mapToResponse(saved);
    }

    public EmployerResponseDTO getProfile(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        Employer employer = employerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Employer profile not found for user: " + email));

        return mapToResponse(employer);
    }

    public EmployerResponseDTO getProfileById(Long id) {

        Employer employer = employerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employer not found with id: " + id));

        return mapToResponse(employer);
    }

    public EmployerResponseDTO updateProfile(String email, EmployerRequestDTO request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        Employer employer = employerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Employer profile not found for user: " + email));

        employer.setCompanyName(request.getCompanyName());
        employer.setCompanyDescription(request.getCompanyDescription());
        employer.setCompanyWebsite(request.getCompanyWebsite());
        employer.setIndustry(request.getIndustry());
        employer.setLocation(request.getLocation());

        Employer updated = employerRepository.save(employer);

        return mapToResponse(updated);
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
