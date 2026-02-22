package com.jobportal.admin.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.admin.dto.AdminDashboardDTO;
import com.jobportal.admin.dto.UpdateRoleDTO;
import com.jobportal.admin.dto.UserResponseDTO;
import com.jobportal.admin.service.AdminService;
import com.jobportal.common.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Admin management APIs")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    @Operation(summary = "Get all users (paginated)")
    public ResponseEntity<ApiResponse<Page<UserResponseDTO>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<UserResponseDTO> users = adminService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success("Users fetched successfully", users));
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserById(@PathVariable Long id) {
        UserResponseDTO user = adminService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success("User fetched successfully", user));
    }

    @PutMapping("/users/{id}/role")
    @Operation(summary = "Update user role")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateUserRole(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRoleDTO dto) {
        UserResponseDTO user = adminService.updateUserRole(id, dto);
        return ResponseEntity.ok(ApiResponse.success("User role updated successfully", user));
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Delete user")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get admin dashboard stats")
    public ResponseEntity<ApiResponse<AdminDashboardDTO>> getDashboard() {
        AdminDashboardDTO dashboard = adminService.getDashboard();
        return ResponseEntity.ok(ApiResponse.success("Dashboard stats fetched successfully", dashboard));
    }
}
