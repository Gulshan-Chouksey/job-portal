package com.jobportal.employer.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployerRequestDTO {

    @NotBlank(message = "Company name is required")
    private String companyName;

    private String companyDescription;

    private String companyWebsite;

    private String industry;

    private String location;
}
