package com.jobportal.employer.dto;

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
public class EmployerResponseDTO {

    private Long id;
    private Long userId;
    private String name;
    private String email;
    private String companyName;
    private String companyDescription;
    private String companyWebsite;
    private String industry;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
