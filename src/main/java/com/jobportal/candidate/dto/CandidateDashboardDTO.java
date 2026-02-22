package com.jobportal.candidate.dto;

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
public class CandidateDashboardDTO {

    private long totalApplications;
    private long pendingApplications;
    private long reviewedApplications;
    private long shortlistedApplications;
    private long interviewApplications;
    private long offeredApplications;
    private long rejectedApplications;
    private long withdrawnApplications;
    private long totalSavedJobs;
}
