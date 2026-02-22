package com.jobportal.admin.dto;

import com.jobportal.auth.entity.Role;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRoleDTO {

    @NotNull(message = "Role is required")
    private Role role;
}
