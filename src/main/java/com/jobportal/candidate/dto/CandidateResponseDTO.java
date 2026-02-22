package com.jobportal.candidate.dto;

import java.time.LocalDateTime;

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
public class CandidateResponseDTO {

    private Long id;
    private Long userId;
    private String name;
    private String email;
    private String headline;
    private String summary;
    private String skills;
    private Integer experienceYears;
    private String education;
    private String resumeUrl;
    private String phone;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
