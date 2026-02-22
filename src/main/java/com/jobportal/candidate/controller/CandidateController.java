package com.jobportal.candidate.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
