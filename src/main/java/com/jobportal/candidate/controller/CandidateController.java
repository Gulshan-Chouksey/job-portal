package com.jobportal.candidate.controller;

import java.security.Principal;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jobportal.candidate.dto.CandidateDashboardDTO;
import com.jobportal.candidate.dto.CandidateRequestDTO;
import com.jobportal.candidate.dto.CandidateResponseDTO;
import com.jobportal.candidate.service.CandidateService;
import com.jobportal.common.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/candidates")
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateService candidateService;

    @PostMapping("/profile")
    public ResponseEntity<ApiResponse<CandidateResponseDTO>> createProfile(
            Principal principal,
            @Valid @RequestBody CandidateRequestDTO request) {
        CandidateResponseDTO response = candidateService.createProfile(principal.getName(), request);
        return new ResponseEntity<>(ApiResponse.success("Candidate profile created successfully", response),
                HttpStatus.CREATED);
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<CandidateResponseDTO>> getMyProfile(Principal principal) {
        CandidateResponseDTO response = candidateService.getProfile(principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Candidate profile retrieved", response));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<CandidateDashboardDTO>> getDashboard(Principal principal) {
        CandidateDashboardDTO response = candidateService.getDashboard(principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Candidate dashboard stats retrieved", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CandidateResponseDTO>> getCandidateById(@PathVariable Long id) {
        CandidateResponseDTO response = candidateService.getProfileById(id);
        return ResponseEntity.ok(ApiResponse.success("Candidate retrieved", response));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<CandidateResponseDTO>> updateProfile(
            Principal principal,
            @Valid @RequestBody CandidateRequestDTO request) {
        CandidateResponseDTO response = candidateService.updateProfile(principal.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Candidate profile updated successfully", response));
    }

    @PostMapping("/resume")
    public ResponseEntity<ApiResponse<CandidateResponseDTO>> uploadResume(
            Principal principal,
            @RequestParam("file") MultipartFile file) {
        CandidateResponseDTO response = candidateService.uploadResume(principal.getName(), file);
        return ResponseEntity.ok(ApiResponse.success("Resume uploaded successfully", response));
    }

    @GetMapping("/{id}/resume")
    public ResponseEntity<Resource> downloadResume(@PathVariable Long id) {
        Resource resource = candidateService.downloadResume(id);
        String filename = resource.getFilename() != null ? resource.getFilename() : "resume";
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }
}
