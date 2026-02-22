package com.jobportal.employer.dto;

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
public class EmployerDashboardDTO {

    private long totalJobs;
    private long activeJobs;
    private long draftJobs;
    private long closedJobs;
    private long totalApplicationsReceived;
}