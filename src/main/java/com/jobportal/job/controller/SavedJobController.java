package com.jobportal.job.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.common.response.ApiResponse;
import com.jobportal.job.dto.SavedJobResponseDTO;
import com.jobportal.job.service.SavedJobService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/saved-jobs")
@RequiredArgsConstructor
public class SavedJobController {

    private final SavedJobService savedJobService;

    @PostMapping("/{jobId}")
    public ResponseEntity<ApiResponse<SavedJobResponseDTO>> saveJob(
            Authentication authentication,
            @PathVariable Long jobId) {

        SavedJobResponseDTO response = savedJobService.saveJob(
                authentication.getName(), jobId);

        return new ResponseEntity<>(ApiResponse.success("Job saved successfully", response),
                HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<SavedJobResponseDTO>>> getMySavedJobs(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<SavedJobResponseDTO> response = savedJobService.getMySavedJobs(
                authentication.getName(),
                PageRequest.of(page, size, Sort.by("savedAt").descending()));

        return ResponseEntity.ok(ApiResponse.success("Saved jobs fetched", response));
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<ApiResponse<Void>> unsaveJob(
            Authentication authentication,
            @PathVariable Long jobId) {

        savedJobService.unsaveJob(authentication.getName(), jobId);

        return ResponseEntity.ok(ApiResponse.success("Job unsaved successfully", null));
    }

    @GetMapping("/{jobId}/check")
    public ResponseEntity<ApiResponse<Boolean>> isJobSaved(
            Authentication authentication,
            @PathVariable Long jobId) {

        boolean isSaved = savedJobService.isJobSaved(authentication.getName(), jobId);

        return ResponseEntity.ok(ApiResponse.success("Check complete", isSaved));
    }
}
