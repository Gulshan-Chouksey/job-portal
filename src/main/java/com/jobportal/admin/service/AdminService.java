package com.jobportal.admin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobportal.admin.dto.AdminDashboardDTO;
import com.jobportal.admin.dto.UpdateRoleDTO;
import com.jobportal.admin.dto.UserResponseDTO;
import com.jobportal.application.repository.ApplicationRepository;
import com.jobportal.auth.entity.Role;
import com.jobportal.auth.entity.User;
import com.jobportal.auth.repository.UserRepository;
import com.jobportal.common.exception.ResourceNotFoundException;
import com.jobportal.job.repository.JobRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

    private static final Logger log = LoggerFactory.getLogger(AdminService.class);

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;

    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        log.info("Fetching all users, page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return userRepository.findAll(pageable).map(this::mapToUserResponse);
    }

    public UserResponseDTO getUserById(Long id) {
        log.info("Fetching user by id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", id);
                    return new ResourceNotFoundException("User not found with id: " + id);
                });
        return mapToUserResponse(user);
    }

    @Transactional
    public UserResponseDTO updateUserRole(Long id, UpdateRoleDTO dto) {
        log.info("Updating role for user id: {} to {}", id, dto.getRole());
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", id);
                    return new ResourceNotFoundException("User not found with id: " + id);
                });
        user.setRole(dto.getRole());
        User updated = userRepository.save(user);
        log.info("Role updated successfully for user id: {}", id);
        return mapToUserResponse(updated);
    }

    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);
        if (!userRepository.existsById(id)) {
            log.warn("User not found with id: {}", id);
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
        log.info("User deleted successfully with id: {}", id);
    }

    public AdminDashboardDTO getDashboard() {
        log.info("Fetching admin dashboard stats");

        long totalUsers = userRepository.count();
        long totalCandidates = userRepository.countByRole(Role.CANDIDATE);
        long totalEmployers = userRepository.countByRole(Role.EMPLOYER);
        long totalAdmins = userRepository.countByRole(Role.ADMIN);
        long totalJobs = jobRepository.count();
        long totalApplications = applicationRepository.count();

        return AdminDashboardDTO.builder()
                .totalUsers(totalUsers)
                .totalCandidates(totalCandidates)
                .totalEmployers(totalEmployers)
                .totalAdmins(totalAdmins)
                .totalJobs(totalJobs)
                .totalApplications(totalApplications)
                .build();
    }

    private UserResponseDTO mapToUserResponse(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
