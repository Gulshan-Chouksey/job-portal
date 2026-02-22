package com.jobportal.application.controller;

import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.application.dto.ApplicationRequestDTO;
import com.jobportal.application.dto.ApplicationResponseDTO;
import com.jobportal.application.dto.StatusUpdateDTO;
import com.jobportal.application.service.ApplicationService;
import com.jobportal.common.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<ApiResponse<ApplicationResponseDTO>> apply(
            Principal principal,
            @Valid @RequestBody ApplicationRequestDTO request) {
        ApplicationResponseDTO response = applicationService.apply(principal.getName(), request);
        return new ResponseEntity<>(ApiResponse.success("Application submitted successfully", response),
                HttpStatus.CREATED);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<ApiResponse<Page<ApplicationResponseDTO>>> getMyApplications(
            Principal principal,
            @PageableDefault(size = 10, sort = "appliedAt") Pageable pageable) {
        Page<ApplicationResponseDTO> applications = applicationService.getMyApplications(
                principal.getName(), pageable);
        return ResponseEntity.ok(ApiResponse.success("Applications retrieved", applications));
    }

    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<ApiResponse<Page<ApplicationResponseDTO>>> getApplicationsForJob(
            @PathVariable Long jobId,
            @PageableDefault(size = 10, sort = "appliedAt") Pageable pageable) {
        Page<ApplicationResponseDTO> applications = applicationService.getApplicationsForJob(jobId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Applications for job retrieved", applications));
    }

    @GetMapping("/employer")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<ApiResponse<Page<ApplicationResponseDTO>>> getApplicationsForEmployer(
            Principal principal,
            @PageableDefault(size = 10, sort = "appliedAt") Pageable pageable) {
        Page<ApplicationResponseDTO> applications = applicationService.getApplicationsForEmployer(
                principal.getName(), pageable);
        return ResponseEntity.ok(ApiResponse.success("Employer applications retrieved", applications));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ApplicationResponseDTO>> getApplicationById(@PathVariable Long id) {
        ApplicationResponseDTO response = applicationService.getApplicationById(id);
        return ResponseEntity.ok(ApiResponse.success("Application retrieved", response));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<ApiResponse<ApplicationResponseDTO>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateDTO request) {
        ApplicationResponseDTO response = applicationService.updateStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Application status updated", response));
    }

    @PatchMapping("/{id}/withdraw")
    @PreAuthorize("hasRole('CANDIDATE')")
    public ResponseEntity<ApiResponse<ApplicationResponseDTO>> withdraw(
            @PathVariable Long id,
            Principal principal) {
        ApplicationResponseDTO response = applicationService.withdraw(id, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Application withdrawn successfully", response));
    }
}
