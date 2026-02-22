package com.jobportal.candidate.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CandidateRequestDTO {

    private String headline;

    private String summary;

    private String skills;

    @Min(value = 0, message = "Experience years must be 0 or more")
    private Integer experienceYears;

    private String education;

    private String resumeUrl;

    private String phone;

    private String location;
}
