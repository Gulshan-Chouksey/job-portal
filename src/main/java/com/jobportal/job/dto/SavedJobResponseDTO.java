package com.jobportal.job.dto;

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
public class SavedJobResponseDTO {

    private Long id;
    private Long jobId;
    private String jobTitle;
    private String companyName;
    private String location;
    private Integer salaryMin;
    private Integer salaryMax;
    private String status;
    private LocalDateTime savedAt;
}
