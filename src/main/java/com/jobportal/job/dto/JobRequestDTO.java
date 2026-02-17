package com.jobportal.job.dto;

import com.jobportal.job.entity.JobStatus;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobRequestDTO {

    @NotBlank
    private String title;

    private String description;

    @NotBlank
    private String location;

    @NotNull
    private Integer salaryMin;

    @NotNull
    private Integer salaryMax;

    private JobStatus status;

    @AssertTrue(message = "salaryMin must be less than or equal to salaryMax")
    private boolean isSalaryRangeValid() {
        if (salaryMin == null || salaryMax == null) {
            return true;
        }
        return salaryMin <= salaryMax;
    }
}
