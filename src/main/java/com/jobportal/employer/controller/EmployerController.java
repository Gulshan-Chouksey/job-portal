package com.jobportal.employer.controller;

import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.common.response.ApiResponse;
import com.jobportal.employer.dto.EmployerRequestDTO;
import com.jobportal.employer.dto.EmployerResponseDTO;
import com.jobportal.employer.service.EmployerService;
import com.jobportal.job.dto.JobResponseDTO;
import com.jobportal.job.service.JobService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/employers")
@RequiredArgsConstructor
public class EmployerController {

    private final EmployerService employerService;
    private final JobService jobService;

    @PostMapping("/profile")
    public ResponseEntity<ApiResponse<EmployerResponseDTO>> createProfile(
            Principal principal,
            @Valid @RequestBody EmployerRequestDTO request) {
        EmployerResponseDTO response = employerService.createProfile(principal.getName(), request);
        return new ResponseEntity<>(ApiResponse.success("Employer profile created successfully", response),
                HttpStatus.CREATED);
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<EmployerResponseDTO>> getMyProfile(Principal principal) {
        EmployerResponseDTO response = employerService.getProfile(principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Employer profile retrieved", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployerResponseDTO>> getEmployerById(@PathVariable Long id) {
        EmployerResponseDTO response = employerService.getProfileById(id);
        return ResponseEntity.ok(ApiResponse.success("Employer retrieved", response));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<EmployerResponseDTO>> updateProfile(
            Principal principal,
            @Valid @RequestBody EmployerRequestDTO request) {
        EmployerResponseDTO response = employerService.updateProfile(principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Employer profile updated successfully", response));
    }

    @GetMapping("/{id}/jobs")
    public ResponseEntity<ApiResponse<Page<JobResponseDTO>>> getEmployerJobs(
            @PathVariable Long id,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<JobResponseDTO> jobs = jobService.getJobsByEmployer(id, pageable);
        return ResponseEntity.ok(ApiResponse.success("Employer jobs retrieved", jobs));
    }
}
