package com.jobportal.application.dto;

import java.time.LocalDateTime;

import com.jobportal.application.entity.ApplicationStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationResponseDTO {

    private Long id;
    private Long candidateId;
    private String candidateName;
    private String candidateEmail;
    private String candidateHeadline;
    private Long jobId;
    private String jobTitle;
    private String companyName;
    private String coverLetter;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;
}
