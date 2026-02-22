package com.jobportal.admin.dto;

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
public class AdminDashboardDTO {

    private long totalUsers;
    private long totalCandidates;
    private long totalEmployers;
    private long totalAdmins;
    private long totalJobs;
    private long totalApplications;
}
