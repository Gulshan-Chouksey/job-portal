package com.jobportal.job.dto;

import java.time.LocalDateTime;
import java.util.Set;

import com.jobportal.job.entity.JobStatus;

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
public class JobResponseDTO {

    private Long id;
    private String title;
    private String description;
    private String location;
    private Integer salaryMin;
    private Integer salaryMax;
    private JobStatus status;
    private Long employerId;
    private String companyName;
    private Set<CategoryResponseDTO> categories;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
