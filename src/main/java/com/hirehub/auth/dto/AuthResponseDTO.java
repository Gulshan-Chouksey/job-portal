package com.hirehub.auth.dto;

import com.hirehub.auth.entity.Role;

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
public class AuthResponseDTO {

    private Long id;
    private String name;
    private String email;
    private Role role;
    private String token;
    private String refreshToken;
}
