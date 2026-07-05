package com.aihire.backend.controller;

import com.aihire.backend.dto.*;
import com.aihire.backend.response.ApiResponse;
import com.aihire.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication Controller", description = "Endpoints for user registration, login, logout, and profile loading")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/jobseeker")
    @Operation(summary = "Register a new Job Seeker")
    public ResponseEntity<ApiResponse<AuthResponse>> registerJobSeeker(@Valid @RequestBody RegisterJobSeekerRequest request) {
        AuthResponse response = authService.registerJobSeeker(request);
        return ResponseEntity.ok(ApiResponse.success("Job Seeker registered successfully", response));
    }

    @PostMapping("/register/recruiter")
    @Operation(summary = "Register a new Recruiter")
    public ResponseEntity<ApiResponse<AuthResponse>> registerRecruiter(@Valid @RequestBody RegisterRecruiterRequest request) {
        AuthResponse response = authService.registerRecruiter(request);
        return ResponseEntity.ok(ApiResponse.success("Recruiter registered successfully", response));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and return JWT token")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout the current session")
    public ResponseEntity<ApiResponse<Void>> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(ApiResponse.success("Logout successful"));
    }

    @GetMapping("/profile")
    @Operation(summary = "Retrieve current authenticated user profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Unauthorized"));
        }
        UserProfileResponse response = authService.getProfile(principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", response));
    }
}
